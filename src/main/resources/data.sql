-- =================================================================================
-- 1. NORMALIZAÇÃO DE ENTRADA
-- =================================================================================
INSERT INTO tb_input_normalization (operation_type, target_field, source_expression, transformation_expression) VALUES
('CREDIT_ANALYSIS', 'documento', '#raw.customer.document', NULL),
('CREDIT_ANALYSIS', 'valor_solicitado', '#raw.transaction.amount', NULL);

-- =================================================================================
-- 2. TEMPLATES DE DECORADORES
-- =================================================================================
INSERT INTO tb_decorator_template (template_id, type, phase, default_configuration, description) VALUES
('RETRY_3X', 'RETRY', 'AROUND', '{"maxAttempts": 3, "waitDurationMs": 200}', 'Retry rápido'),
('LOG_BASIC', 'LOGGING', 'POST', '{"level": "INFO", "showBody": false}', 'Log simples'),
('CACHE_1MIN', 'CACHE', 'PRE', '{"key": "#standard.documento", "ttlMs": 60000, "provider": "IN_MEMORY"}', 'Cache de 1 minuto'),
('CB_STANDARD', 'CIRCUIT_BREAKER', 'AROUND', '{"failureRateThreshold": 50.0, "waitDurationInOpenStateMs": 10000, "slidingWindowSize": 10}', 'Circuit Breaker padrão'),
('FALLBACK_EMPTY', 'FALLBACK', 'AROUND', '{"value": {}}', 'Retorna objeto vazio em falha'),
('VALIDATE_STATUS', 'RESPONSE_VALIDATOR', 'POST', '{"rules": [{"condition": "#result.status == 200", "message": "Sucesso esperado", "errorCode": "ERR_001"}]}', 'Valida status 200');

-- =================================================================================
-- 3. VERSÃO DO PIPELINE
-- =================================================================================
INSERT INTO tb_pipeline_version (pipeline_id, operation_type, version, timeout_ms, input_mapping, required_outputs, is_active)
VALUES (
    'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
    'CREDIT_ANALYSIS',
    1,
    15000,
    '{"documento": "#raw.customer.document", "valor_solicitado": "#raw.transaction.amount"}',
    '["decisao_final"]',
    TRUE
);

-- =================================================================================
-- 4. NÓS DO PIPELINE (CONECTADOS POR DADOS)
-- =================================================================================

-- Task 1: Autenticação (Gera token_sessao)
INSERT INTO tb_pipeline_node (node_id, pipeline_id, name, type, configuration, inputs, outputs)
VALUES (
    'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380b01',
    'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
    'auth_task',
    'HTTP',
    '{"url": "http://127.0.0.1:9999/v1/auth/SISTEMA_ORQUESTRADOR", "method": "GET", "global": true, "cron": "0 */30 * * * *"}',
    '{}',
    '{"token": "token_sessao"}'
);

-- Task 2: Enriquecimento
INSERT INTO tb_pipeline_node (node_id, pipeline_id, name, type, configuration, inputs, outputs)
VALUES (
    'c2eebc99-9c0b-4ef8-bb6d-6bb9bd380c02',
    'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
    'enrich_task',
    'HTTP',
    '{"url": "http://127.0.0.1:9999/v1/enrich/${standard.documento}", "method": "GET", "headers": {"Authorization": "Bearer ${token_sessao}"}}',
    '{"token_sessao": "token_sessao", "standard.documento": "standard.documento"}',
    '{"val": "dados_cliente"}'
);

-- Task 3: Bureau de Crédito
INSERT INTO tb_pipeline_node (node_id, pipeline_id, name, type, configuration, inputs, outputs)
VALUES (
    'd3eebc99-9c0b-4ef8-bb6d-6bb9bd380d03',
    'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
    'bureau_task',
    'HTTP',
    '{"url": "http://127.0.0.1:9999/v1/bureau/${standard.documento}", "method": "GET"}',
    '{"standard.documento": "standard.documento"}',
    '{"score": "score_credito"}'
);

-- Task 4: Decisão Final
INSERT INTO tb_pipeline_node (node_id, pipeline_id, name, type, configuration, inputs, outputs)
VALUES (
    'e4eebc99-9c0b-4ef8-bb6d-6bb9bd380e04',
    'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
    'decision_task',
    'SPEL',
    '{"expression": "#score_credito > 700 and #standard.valor_solicitado < 5000 ? ''APROVADO'' : ''REVISAO_MANUAL''"}',
    '{"score_credito": "score_credito", "standard.valor_solicitado": "standard.valor_solicitado"}',
    '{"result": "decisao_final"}'
);

-- =================================================================================
-- 5. ASSOCIAÇÃO DE DECORADORES
-- =================================================================================
INSERT INTO tb_pipeline_node_decorator (node_id, template_id, execution_order) VALUES
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380b01', 'RETRY_3X', 10),
('c2eebc99-9c0b-4ef8-bb6d-6bb9bd380c02', 'LOG_BASIC', 100),
('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380d03', 'CB_STANDARD', 50);

-- =================================================================================
-- 6. REGRAS DE TAGS
-- =================================================================================
INSERT INTO tb_tag_rule (tag_name, condition_expression, priority, is_active)
VALUES ('audit', '#raw.transaction.amount > 1000', 100, TRUE);
