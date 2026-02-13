    # Script de teste para o Orquestrador (PowerShell)
# Envia uma transação do App Mobile para análise de risco

$body = @{
    operation_type = "STANDARD_RISK"
    transaction = @{
        amount = 2500.00
        currency = "BRL"
    }
    customer = @{
        document = "123.456.789-00"
    }
    device = @{
        id = "android_v14_secure_xyz"
    }
} | ConvertTo-Json -Depth 10

Invoke-RestMethod -Uri http://localhost:8080/api/v1/analise `
  -Method Post `
  -Headers @{ 
      "Content-Type" = "application/json" 
  } `
  -Body $body
