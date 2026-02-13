#!/bin/bash
echo "Configurando AppConfig no LocalStack..."

# 1. Criar Aplicação
APP_ID=$(aws --endpoint-url=http://localhost:4566 appconfig create-application --name orquestrator-app --query 'Id' --output text)
echo "App ID: $APP_ID"

# 2. Criar Environment
ENV_ID=$(aws --endpoint-url=http://localhost:4566 appconfig create-environment --application-id $APP_ID --name production --query 'Id' --output text)
echo "Env ID: $ENV_ID"

# 3. Criar Profile
PROFILE_ID=$(aws --endpoint-url=http://localhost:4566 appconfig create-configuration-profile --application-id $APP_ID --name flow-routing --location-uri hosted --query 'Id' --output text)
echo "Profile ID: $PROFILE_ID"

# 4. Criar Conteúdo Inicial (Versão 1)
# PIX_CASHOUT: 100% v1, 0% v2
CONTENT='{
  "PIX_CASHOUT": {
    "default_version": 1,
    "canary": {
      "version": 2,
      "percentage": 0
    }
  },
  "STANDARD_RISK": {
    "default_version": 1
  }
}'

# Criar versão da configuração
VERSION=$(aws --endpoint-url=http://localhost:4566 appconfig create-hosted-configuration-version \
  --application-id $APP_ID \
  --configuration-profile-id $PROFILE_ID \
  --content "$CONTENT" \
  --content-type "application/json" \
  --query 'Version-Number' --output text)

# 5. Deploy (Start Deployment)
aws --endpoint-url=http://localhost:4566 appconfig start-deployment \
  --application-id $APP_ID \
  --environment-id $ENV_ID \
  --configuration-profile-id $PROFILE_ID \
  --configuration-version $VERSION \
  --deployment-strategy-id AppConfig.AllAtOnce \
  --description "Initial Deployment"

echo "AppConfig configurado com sucesso!"
