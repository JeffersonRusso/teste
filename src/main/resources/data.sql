-- =================================================================================
-- 1. INFRAESTRUTURA: Perfis de Resiliência (Policies)
-- =================================================================================

-- Perfil Padrão: Apenas Log
INSERT INTO tb_infra_profiles (profile_id, description, default_controls) VALUES
('HTTP_PADRAO', 'Configuração básica: Log e Validação 200', '{"postExecution": [{"templateRef": "VALIDACAO_HTTP_PADRAO"}], "monitors": [{"type": "LOG_RESPONSE", "config": {"level": "INFO", "showBody": true}}]}');

-- Perfil Resiliente: Log + Validação + Retry (Para chamadas externas críticas)
INSERT INTO tb_infra_profiles (profile_id, description, default_controls) VALUES
('HTTP_RESILIENTE', 'Configuração robusta: Retry(3x), Log e Validação 200', '{"preExecution": [{"templateRef": "RETRY_PADRAO"}], "postExecution": [{"templateRef": "VALIDACAO_HTTP_PADRAO"}], "monitors": [{"type": "LOG_RESPONSE", "config": {"level": "INFO", "showBody": true}}]}');

-- =================================================================================
-- 2. ADAPTERS (Input Normalization)
-- =================================================================================
INSERT INTO tb_input_normalization (operation_type, target_field, source_expression) VALUES
('STANDARD_RISK', 'valor', '#raw.transaction.amount'),
('STANDARD_RISK', 'documento', '#raw.customer.document'),
('STANDARD_RISK', 'device_id', '#raw.device.id'),
('STANDARD_RISK', 'ip_address', '#raw.device.ip');

-- =================================================================================
-- 3. BANCO DE EXCEPTIONS (Custom Errors)
-- =================================================================================
INSERT INTO tb_error_catalog (error_code, message_template) VALUES
('HTTP_NOT_200', 'Erro na integração com #{#node_id}: Status #{#node_status} retornado.'),
('CLIENT_NOT_FOUND', 'Cliente com documento #{#standard.documento} não foi encontrado na base legada.'),
('COMPLIANCE_FAILED', 'Falha na validação de compliance: #{#compliance_info.message}');

-- =================================================================================
-- 4. TEMPLATES DE FEATURES (Reutilização de Interceptores)
-- =================================================================================
INSERT INTO tb_feature_templates (template_id, feature_type, config_json, description) VALUES
('VALIDACAO_HTTP_PADRAO', 'RESPONSE_VALIDATOR', '{"rules": [{"condition": "#node_status != 200", "errorCode": "HTTP_NOT_200"}]}', 'Validação padrão de status HTTP 200'),
('RETRY_PADRAO', 'RETRY', '{"maxAttempts": 3, "waitDurationMs": 50}', 'Tenta 3 vezes com espera de 50ms (Rápido)');

-- =================================================================================
-- 5. REGRAS DE TAGS (Dinâmicas - Contexto da Transação)
-- =================================================================================
INSERT INTO tb_tag_rules (tag_name, condition_expression, priority, description) VALUES
('HIGH_VALUE', '#standard.valor > 50000', 1, 'Transações de alto valor'),
('MOBILE', '#standard.device_id != null', 1, 'Transações via dispositivo móvel');

-- =================================================================================
-- 6. CONFIGURAÇÃO DE PIPELINE (Timeouts)
-- =================================================================================
INSERT INTO tb_pipeline_config (operation_type, timeout_ms, description) VALUES
('STANDARD_RISK', 5000, 'Timeout global de 5 segundos para análise de risco padrão');

-- =================================================================================
-- 7. DEFINIÇÃO DE FLUXOS (Flow Definition - Menu à La Carte)
-- =================================================================================
INSERT INTO tb_flow_config (operation_type, version, required_outputs, allowed_tasks, description, is_active) VALUES
(
    'STANDARD_RISK',
    1,
    '["resultado_final"]',
    '[{"id": "authGlobalTask", "version": 1}, {"id": "getClienteData", "version": 1}, {"id": "getDeviceData", "version": 1}, {"id": "checkCompliance", "version": 1}, {"id": "enrichCompliance", "version": 1}, {"id": "getSerasaData", "version": 1}, {"id": "getBacenData", "version": 1}, {"id": "getGeoData", "version": 1}, {"id": "getEstatisticas", "version": 1}, {"id": "analiseCliente", "version": 1}, {"id": "getHistoricoPagamentos", "version": 1}, {"id": "getScoreComportamental", "version": 1}, {"id": "getListaNegra", "version": 1}, {"id": "getDadosReceita", "version": 1}, {"id": "checkMatchCadastral", "version": 1}, {"id": "getOpenFinance", "version": 1}, {"id": "calcCapacidadePagamento", "version": 1}, {"id": "checkBiometria", "version": 1}, {"id": "getDeviceReputation", "version": 1}, {"id": "checkFraudeML", "version": 1}, {"id": "calcRendaGroovy", "version": 1}, {"id": "analiseFraudeGroovy", "version": 1}, {"id": "consolidarScoreGroovy", "version": 1}, {"id": "checkRiscoDmn", "version": 1}, {"id": "finalRiskEngine", "version": 1}]',
    'Fluxo padrão com todas as tasks disponíveis',
    true
),
(
    'PIX_CASHOUT',
    1,
    '["resultado_final"]',
    '[{"id": "authGlobalTask", "version": 1}, {"id": "getClienteData", "version": 1}, {"id": "getDeviceData", "version": 1}, {"id": "checkCompliance", "version": 1}, {"id": "enrichCompliance", "version": 1}, {"id": "getGeoData", "version": 1}, {"id": "getEstatisticas", "version": 1}, {"id": "analiseCliente", "version": 1}, {"id": "getListaNegra", "version": 1}, {"id": "checkBiometria", "version": 1}, {"id": "getDeviceReputation", "version": 1}, {"id": "checkFraudeML", "version": 1}, {"id": "analiseFraudeGroovy", "version": 1}, {"id": "finalRiskEngine", "version": 1}]',
    'Fluxo PIX V1: Rápido, focado em fraude e dados básicos.',
    true
),
(
    'PIX_CASHOUT',
    2,
    '["resultado_final"]',
    '[{"id": "authGlobalTask", "version": 1}, {"id": "getClienteData", "version": 1}, {"id": "getDeviceData", "version": 1}, {"id": "checkCompliance", "version": 1}, {"id": "enrichCompliance", "version": 1}, {"id": "getGeoData", "version": 1}, {"id": "getEstatisticas", "version": 1}, {"id": "analiseCliente", "version": 1}, {"id": "getListaNegra", "version": 1}, {"id": "checkBiometria", "version": 1}, {"id": "getDeviceReputation", "version": 1}, {"id": "checkFraudeML", "version": 1}, {"id": "analiseFraudeGroovy", "version": 1}, {"id": "finalRiskEngine", "version": 1}, {"id": "getSerasaData", "version": 1}]',
    'Fluxo PIX V2 (Canary): Adiciona Serasa para teste de impacto.',
    true
),
(
    'BOLETO_PAGAR',
    1,
    '["resultado_final"]',
    '[{"id": "authGlobalTask", "version": 1}, {"id": "getClienteData", "version": 1}, {"id": "checkCompliance", "version": 1}, {"id": "enrichCompliance", "version": 1}, {"id": "getSerasaData", "version": 1}, {"id": "getBacenData", "version": 1}, {"id": "getEstatisticas", "version": 1}, {"id": "analiseCliente", "version": 1}, {"id": "getHistoricoPagamentos", "version": 1}, {"id": "getScoreComportamental", "version": 1}, {"id": "getDadosReceita", "version": 1}, {"id": "checkMatchCadastral", "version": 1}, {"id": "calcRendaGroovy", "version": 1}, {"id": "consolidarScoreGroovy", "version": 1}, {"id": "finalRiskEngine", "version": 1}]',
    'Fluxo Boleto: Focado em crédito e histórico. Sem biometria ou device reputation.',
    true
);

-- =================================================================================
-- 8. TASKS (Capabilities - Sem Tags, apenas IDs)
-- =================================================================================

-- TASK 0: Autenticação Global
INSERT INTO tb_task_catalog (task_id, version, task_type, selector_expression, requires_json, produces_json, infra_profile_id, config_json, features_json, is_active)
VALUES (
    'authGlobalTask',
    1,
    'HTTP',
    'true',
    null,
    '[{"name": "global_token", "path": "access_token", "type": "STRING"}]',
    'HTTP_PADRAO',
    '{"method": "POST", "url": "#{ @environment.getProperty(''integration.api.url'') }/oauth/token", "body": {"grant_type": "client_credentials", "client_id": "orquestrador", "client_secret": "segredo"}, "timeoutMs": 5000, "global": true, "refreshIntervalMs": 300000}',
    '{"preExecution": [{"type": "CACHE", "config": {"key": "token_global", "ttlMs": 300000, "provider": "IN_MEMORY"}}, {"templateRef": "RETRY_PADRAO"}], "postExecution": [{"templateRef": "VALIDACAO_HTTP_PADRAO"}]}',
    true
);

-- TASK 1: Obter Dados do Cliente
INSERT INTO tb_task_catalog (task_id, version, task_type, selector_expression, requires_json, produces_json, infra_profile_id, config_json, features_json, is_active)
VALUES (
    'getClienteData',
    1,
    'HTTP',
    'true',
    '[{"name": "standard.documento"}, {"name": "global_token"}]',
    '[{"name": "cliente_info"}, {"name": "tipo_cliente", "path": "dados.tipo", "type": "STRING"}]',
    'HTTP_PADRAO',
        '{"method": "GET", "url": "#{ @environment.getProperty(''integration.api.url'') }/v1/clientes/#{ #standard.documento }", "headers": {"Authorization": "Bearer #{ #global_token }"}, "timeoutMs": 2000}',
    '{"preExecution": [{"type": "CACHE", "config": {"key": "#{ #standard.documento }", "ttlMs": 60000, "provider": "IN_MEMORY"}}, {"templateRef": "RETRY_PADRAO"}], "postExecution": [{"templateRef": "VALIDACAO_HTTP_PADRAO"}]}',
    true
);

-- TASK 2: Obter Dados do Dispositivo
INSERT INTO tb_task_catalog (task_id, version, task_type, selector_expression, requires_json, produces_json, infra_profile_id, config_json, features_json, is_active)
VALUES (
    'getDeviceData',
    1,
    'HTTP',
    'true',
    '[{"name": "standard.device_id"}]',
    '[{"name": "device_info"}]',
    'HTTP_PADRAO',
    '{"method": "GET", "url": "#{ @environment.getProperty(''integration.api.url'') }/v1/devices/#{ #standard.device_id }", "timeoutMs": 2000}',
    '{"preExecution": [{"templateRef": "RETRY_PADRAO"}], "postExecution": [{"templateRef": "VALIDACAO_HTTP_PADRAO"}]}',
    true
);

-- TASK 3: Validação de Compliance
INSERT INTO tb_task_catalog (task_id, version, task_type, selector_expression, requires_json, produces_json, infra_profile_id, config_json, features_json, is_active)
VALUES (
    'checkCompliance',
    1,
    'HTTP',
    'true',
    '[{"name": "cliente_info"}, {"name": "tipo_cliente"}]',
    '[{"name": "compliance_info"}]',
    'HTTP_PADRAO',
    '{"method": "POST", "url": "#{ @environment.getProperty(''integration.api.url'') }/v1/compliance/validate", "body": {"documento": "#cliente_info.dados.documento", "tipo": "#tipo_cliente"}, "timeoutMs": 2000}',
    '{"preExecution": [{"templateRef": "RETRY_PADRAO"}], "postExecution": [{"type": "RESPONSE_VALIDATOR", "config": {"rules": [{"condition": "#node_status != 200", "errorCode": "HTTP_NOT_200"}]}}]}',
    true
);

-- TASK 3.5: Enriquecimento de Compliance
INSERT INTO tb_task_catalog (task_id, version, task_type, selector_expression, requires_json, produces_json, infra_profile_id, config_json, is_active)
VALUES (
    'enrichCompliance',
    1,
    'SPEL',
    'true',
    '[{"name": "compliance_info"}]',
    '[{"name": "compliance_enriched"}]',
    'HTTP_PADRAO',
    '{"expression": "#compliance_info.put(''isPilot'', true) ?: #compliance_info", "timeoutMs": 100}',
    true
);

-- TASK 5: Serasa
INSERT INTO tb_task_catalog (task_id, version, task_type, selector_expression, requires_json, produces_json, infra_profile_id, config_json, features_json, is_active)
VALUES (
    'getSerasaData',
    1,
    'HTTP',
    'true',
    '[{"name": "standard.documento"}]',
    '[{"name": "serasa_info"}]',
    'HTTP_RESILIENTE',
    '{"method": "GET", "url": "#{ @environment.getProperty(''integration.api.url'') }/v1/serasa/#{ #standard.documento }", "timeoutMs": 2000}',
    '{"preExecution": [{"templateRef": "RETRY_PADRAO"}], "postExecution": [{"templateRef": "VALIDACAO_HTTP_PADRAO"}]}',
    true
);

-- TASK 6: Bacen
INSERT INTO tb_task_catalog (task_id, version, task_type, selector_expression, requires_json, produces_json, infra_profile_id, config_json, features_json, is_active)
VALUES (
    'getBacenData',
    1,
    'HTTP',
    'true',
    '[{"name": "standard.documento"}]',
    '[{"name": "bacen_info"}]',
    'HTTP_RESILIENTE',
    '{"method": "GET", "url": "#{ @environment.getProperty(''integration.api.url'') }/v1/bacen/sanctions/#{ #standard.documento }", "timeoutMs": 2000}',
    '{"preExecution": [{"templateRef": "RETRY_PADRAO"}], "postExecution": [{"templateRef": "VALIDACAO_HTTP_PADRAO"}]}',
    true
);

-- TASK 7: Geo IP (Com Fallback)
INSERT INTO tb_task_catalog (task_id, version, task_type, selector_expression, requires_json, produces_json, infra_profile_id, config_json, features_json, is_active)
VALUES (
    'getGeoData',
    1,
    'HTTP',
    'true',
    '[{"name": "standard.ip_address"}]',
    '[{"name": "geo_info"}]',
    'HTTP_RESILIENTE',
    '{"method": "GET", "url": "#{ @environment.getProperty(''integration.api.url'') }/v1/geo/ip/#{ #standard.ip_address }", "timeoutMs": 2000}',
    '{"preExecution": [{"templateRef": "RETRY_PADRAO"}], "postExecution": [{"templateRef": "VALIDACAO_HTTP_PADRAO"}, {"type": "FALLBACK", "config": {"value": {"country": "BR", "city": "FALLBACK_CITY", "lat": 0.0, "lon": 0.0}}}]}',
    true
);

-- TASK 8: Estatísticas
INSERT INTO tb_task_catalog (task_id, version, task_type, selector_expression, requires_json, produces_json, infra_profile_id, config_json, features_json, is_active)
VALUES (
    'getEstatisticas',
    1,
    'HTTP',
    'true',
    '[{"name": "standard.documento"}]',
    '[{"name": "media_gastos_mensal", "path": "gastos.mediaMensal"}, {"name": "frequencia_compras", "path": "comportamento.frequencia"}, {"name": "maior_compra_ano", "path": "gastos.maiorCompra"}, {"name": "dias_desde_ultima_compra", "path": "comportamento.diasUltimaCompra"}, {"name": "categoria_cliente", "path": "perfil.categoria"}, {"name": "score_interno", "path": "perfil.scoreInterno"}, {"name": "risco_churn", "path": "risco.churnProbability"}, {"name": "lifetime_value", "path": "valor.ltv"}, {"name": "canal_preferido", "path": "preferencias.canal"}, {"name": "regiao_principal", "path": "preferencias.regiao"}]',
    'HTTP_RESILIENTE',
    '{"method": "GET", "url": "#{ @environment.getProperty(''integration.api.url'') }/v1/estatisticas/#{ #standard.documento }", "timeoutMs": 2000}',
    '{"preExecution": [{"templateRef": "RETRY_PADRAO"}], "postExecution": [{"templateRef": "VALIDACAO_HTTP_PADRAO"}]}',
    true
);

-- TASK 9: Análise Cliente
INSERT INTO tb_task_catalog (task_id, version, task_type, selector_expression, requires_json, produces_json, infra_profile_id, config_json, features_json, is_active)
VALUES (
    'analiseCliente',
    1,
    'HTTP',
    'true',
    '[{"name": "media_gastos_mensal"}, {"name": "categoria_cliente"}, {"name": "risco_churn"}]',
    '[{"name": "analise_cliente_result"}]',
    'HTTP_RESILIENTE',
    '{"method": "POST", "url": "#{ @environment.getProperty(''integration.api.url'') }/v1/analise-cliente", "body": {"gastos": "#media_gastos_mensal", "categoria": "#categoria_cliente", "churn": "#risco_churn"}, "timeoutMs": 2000}',
    '{"preExecution": [{"templateRef": "RETRY_PADRAO"}], "postExecution": [{"templateRef": "VALIDACAO_HTTP_PADRAO"}]}',
    true
);

-- TASK 10: Histórico Pagamentos
INSERT INTO tb_task_catalog (task_id, version, task_type, selector_expression, requires_json, produces_json, infra_profile_id, config_json, is_active)
VALUES ('getHistoricoPagamentos', 1, 'HTTP', 'true', '[{"name": "standard.documento"}]', '[{"name": "historico_pagamentos"}]', 'HTTP_RESILIENTE',
'{"method": "GET", "url": "#{ @environment.getProperty(''integration.api.url'') }/v1/historico-pagamentos/#{ #standard.documento }", "timeoutMs": 2000}',
true);

-- TASK 11: Score Comportamental
INSERT INTO tb_task_catalog (task_id, version, task_type, selector_expression, requires_json, produces_json, infra_profile_id, config_json, is_active)
VALUES ('getScoreComportamental', 1, 'HTTP', 'true', '[{"name": "historico_pagamentos"}]', '[{"name": "score_comportamental"}]', 'HTTP_RESILIENTE',
'{"method": "POST", "url": "#{ @environment.getProperty(''integration.api.url'') }/v1/score-comportamental", "body": {"historico": "#historico_pagamentos"}, "timeoutMs": 2000}',
true);

-- TASK 12: Lista Negra Fraude
INSERT INTO tb_task_catalog (task_id, version, task_type, selector_expression, requires_json, produces_json, infra_profile_id, config_json, features_json, is_active)
VALUES ('getListaNegra', 1, 'HTTP', 'true', '[{"name": "standard.documento"}]', '[{"name": "lista_negra"}]', 'HTTP_RESILIENTE',
'{"method": "GET", "url": "#{ @environment.getProperty(''integration.api.url'') }/v1/lista-negra-fraude/#{ #standard.documento }", "timeoutMs": 2000}',
'{"preExecution": [{"templateRef": "RETRY_PADRAO"}], "postExecution": [{"templateRef": "VALIDACAO_HTTP_PADRAO"}]}',
true);

-- TASK 13: Dados Cadastrais Receita
INSERT INTO tb_task_catalog (task_id, version, task_type, selector_expression, requires_json, produces_json, infra_profile_id, config_json, features_json, is_active)
VALUES ('getDadosReceita', 1, 'HTTP', 'true', '[{"name": "standard.documento"}]', '[{"name": "dados_receita"}]', 'HTTP_RESILIENTE',
'{"method": "GET", "url": "#{ @environment.getProperty(''integration.api.url'') }/v1/dados-cadastrais-receita/#{ #standard.documento }", "timeoutMs": 2000}',
'{"preExecution": [{"templateRef": "RETRY_PADRAO"}], "postExecution": [{"templateRef": "VALIDACAO_HTTP_PADRAO"}]}',
true);

-- TASK 14: Match Cadastral
INSERT INTO tb_task_catalog (task_id, version, task_type, selector_expression, requires_json, produces_json, infra_profile_id, config_json, features_json, is_active)
VALUES ('checkMatchCadastral', 1, 'HTTP', 'true', '[{"name": "dados_receita"}, {"name": "cliente_info"}]', '[{"name": "match_cadastral"}]', 'HTTP_RESILIENTE',
'{"method": "POST", "url": "#{ @environment.getProperty(''integration.api.url'') }/v1/match-cadastral", "body": {"receita": "#dados_receita", "banco": "#cliente_info"}, "timeoutMs": 2000}',
'{"preExecution": [{"templateRef": "RETRY_PADRAO"}], "postExecution": [{"templateRef": "VALIDACAO_HTTP_PADRAO"}]}',
true);

-- TASK 15: Open Finance
INSERT INTO tb_task_catalog (task_id, version, task_type, selector_expression, requires_json, produces_json, infra_profile_id, config_json, features_json, is_active)
VALUES ('getOpenFinance', 1, 'HTTP', 'true', '[{"name": "standard.documento"}]', '[{"name": "open_finance"}]', 'HTTP_RESILIENTE',
'{"method": "GET", "url": "#{ @environment.getProperty(''integration.api.url'') }/v1/open-finance/contas/#{ #standard.documento }", "timeoutMs": 2000}',
'{"preExecution": [{"templateRef": "RETRY_PADRAO"}], "postExecution": [{"templateRef": "VALIDACAO_HTTP_PADRAO"}]}',
true);

-- TASK 16: Capacidade Pagamento
INSERT INTO tb_task_catalog (task_id, version, task_type, selector_expression, requires_json, produces_json, infra_profile_id, config_json, features_json, is_active)
VALUES ('calcCapacidadePagamento', 1, 'HTTP', 'true', '[{"name": "open_finance"}]', '[{"name": "capacidade_pagamento"}]', 'HTTP_RESILIENTE',
'{"method": "POST", "url": "#{ @environment.getProperty(''integration.api.url'') }/v1/capacidade-pagamento", "body": {"open_finance": "#open_finance"}, "timeoutMs": 2000}',
'{"preExecution": [{"templateRef": "RETRY_PADRAO"}], "postExecution": [{"templateRef": "VALIDACAO_HTTP_PADRAO"}]}',
true);

-- TASK 17: Biometria Facial
INSERT INTO tb_task_catalog (task_id, version, task_type, selector_expression, requires_json, produces_json, infra_profile_id, config_json, features_json, is_active)
VALUES ('checkBiometria', 1, 'HTTP', 'true', '[{"name": "standard.documento"}]', '[{"name": "biometria_info"}]', 'HTTP_RESILIENTE',
'{"method": "GET", "url": "#{ @environment.getProperty(''integration.api.url'') }/v1/biometria-facial/#{ #standard.documento }", "timeoutMs": 2000}',
'{"preExecution": [{"templateRef": "RETRY_PADRAO"}], "postExecution": [{"templateRef": "VALIDACAO_HTTP_PADRAO"}]}',
true);

-- TASK 18: Device Reputation
INSERT INTO tb_task_catalog (task_id, version, task_type, selector_expression, requires_json, produces_json, infra_profile_id, config_json, features_json, is_active)
VALUES ('getDeviceReputation', 1, 'HTTP', 'true', '[{"name": "standard.device_id"}]', '[{"name": "device_reputation"}]', 'HTTP_RESILIENTE',
'{"method": "GET", "url": "#{ @environment.getProperty(''integration.api.url'') }/v1/device-reputation/#{ #standard.device_id }", "timeoutMs": 2000}',
'{"preExecution": [{"templateRef": "RETRY_PADRAO"}], "postExecution": [{"templateRef": "VALIDACAO_HTTP_PADRAO"}]}',
true);

-- TASK 19: Fraude ML V2
INSERT INTO tb_task_catalog (task_id, version, task_type, selector_expression, requires_json, produces_json, infra_profile_id, config_json, features_json, is_active)
VALUES ('checkFraudeML', 1, 'HTTP', 'true', '[{"name": "lista_negra"}, {"name": "biometria_info"}, {"name": "device_reputation"}]', '[{"name": "fraude_ml"}]', 'HTTP_RESILIENTE',
'{"method": "POST", "url": "#{ @environment.getProperty(''integration.api.url'') }/v1/fraude-ml-v2", "body": {"lista": "#lista_negra", "bio": "#biometria_info", "device": "#device_reputation"}, "timeoutMs": 2000}',
'{"preExecution": [{"templateRef": "RETRY_PADRAO"}], "postExecution": [{"templateRef": "VALIDACAO_HTTP_PADRAO"}]}',
true);

-- TASK 20: Calcular Renda Presumida (Groovy)
INSERT INTO tb_task_catalog (task_id, version, task_type, selector_expression, requires_json, produces_json, infra_profile_id, config_json, is_active)
VALUES ('calcRendaGroovy', 1, 'GROOVY_SCRIPT', 'true', '[{"name": "historico_pagamentos"}]', '[{"name": "renda_calculada"}]', 'HTTP_PADRAO',
'{"scriptName": "CalcularRenda.groovy", "timeoutMs": 500}',
true);

-- TASK 21: Analisar Fraude Device (Groovy)
INSERT INTO tb_task_catalog (task_id, version, task_type, selector_expression, requires_json, produces_json, infra_profile_id, config_json, is_active)
VALUES ('analiseFraudeGroovy', 1, 'GROOVY_SCRIPT', 'true', '[{"name": "device_reputation"}, {"name": "geo_info"}]', '[{"name": "sinal_fraude_device"}]', 'HTTP_PADRAO',
'{"scriptName": "AnalisarFraudeDevice.groovy", "timeoutMs": 500}',
true);

-- TASK 22: Consolidar Score (Groovy)
INSERT INTO tb_task_catalog (task_id, version, task_type, selector_expression, requires_json, produces_json, infra_profile_id, config_json, is_active)
VALUES ('consolidarScoreGroovy', 1, 'GROOVY_SCRIPT', 'true', '[{"name": "score_comportamental"}, {"name": "serasa_info"}, {"name": "analise_cliente_result"}]', '[{"name": "score_consolidado"}]', 'HTTP_PADRAO',
'{"scriptName": "ConsolidarScore.groovy", "timeoutMs": 500}',
true);

-- TASK 23: Regras de Risco DMN
INSERT INTO tb_task_catalog (task_id, version, task_type, selector_expression, requires_json, produces_json, infra_profile_id, config_json, is_active)
VALUES (
    'checkRiscoDmn',
    1,
    'DMN',
    'true',
    '[{"name": "serasa_info.score"}, {"name": "standard.valor"}]',
    '[{"name": "resultado_dmn", "path": "resultado"}, {"name": "motivo_dmn", "path": "motivo"}]',
    'HTTP_PADRAO',
    '{"dmnFile": "risco_teste.dmn", "decisionKey": "decisao_risco_simples", "timeoutMs": 200}',
    true
);

-- TASK 4: Motor de Risco Final

INSERT INTO tb_task_catalog (task_id, version, task_type, selector_expression, requires_json, produces_json, infra_profile_id, config_json, features_json, is_active)
VALUES (
    'finalRiskEngine',
    1,
    'HTTP',
    'true',
    '[{"name": "cliente_info"}, {"name": "device_info"}, {"name": "compliance_enriched"}, {"name": "standard.valor"}, {"name": "serasa_info"}, {"name": "bacen_info"}, {"name": "geo_info"}, {"name": "analise_cliente_result"}, {"name": "score_comportamental"}, {"name": "match_cadastral"}, {"name": "capacidade_pagamento"}, {"name": "fraude_ml"}, {"name": "renda_calculada"}, {"name": "sinal_fraude_device"}, {"name": "score_consolidado"}, {"name": "resultado_dmn"}]',
    '[{"name": "resultado_final"}]',
    'HTTP_RESILIENTE',
    '{"method": "POST", "url": "#{ @environment.getProperty(''integration.api.url'') }/v1/risk/analyze", "body": {"cliente": "#cliente_info", "device": "#device_info", "compliance": "#compliance_enriched", "serasa": "#serasa_info", "bacen": "#bacen_info", "geo": "#geo_info", "analise_cliente": "#analise_cliente_result", "score_comp": "#score_comportamental", "match_cad": "#match_cadastral", "cap_pag": "#capacidade_pagamento", "fraude": "#fraude_ml", "renda_groovy": "#renda_calculada", "fraude_groovy": "#sinal_fraude_device", "score_groovy": "#score_consolidado", "valor_transacao": "#standard.valor", "decisao_dmn": "#resultado_dmn"}, "timeoutMs": 2000}',
    '{"preExecution": [{"templateRef": "RETRY_PADRAO"}], "postExecution": [{"templateRef": "VALIDACAO_HTTP_PADRAO"}]}',
    true
);
