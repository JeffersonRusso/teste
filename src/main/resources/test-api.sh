#!/bin/bash
# Script de teste para o Orquestrador
# Envia uma transação do App Mobile para análise de risco

curl -X POST http://localhost:8080/api/v1/analise \
     -H "Content-Type: application/json" \
     -H "Client-ID: APP_MOBILE" \
     -d '{
           "transaction": {
             "amount": 2500.00,
             "currency": "BRL"
           },
           "customer": {
             "document": "123.456.789-00"
           },
           "device": {
             "id": "android_v14_secure_xyz"
           }
         }'
