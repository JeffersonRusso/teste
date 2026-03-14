-- =================================================================================
-- DATA INICIAL - ATUALIZADO COM ESTRATÉGIA ASYNC
-- =================================================================================

-- 1. LIMPEZA
DELETE FROM tb_pipeline_node_input;
DELETE FROM tb_pipeline_node_output;
DELETE FROM tb_pipeline_node_decorator;
DELETE FROM tb_pipeline_node;
DELETE FROM tb_pipeline_version;
DELETE FROM tb_data_contract;
DELETE FROM tb_semantic_definition;
DELETE FROM tb_tag_rule;
DELETE FROM tb_task_template;
DELETE FROM tb_decorator_template;

-- 2. SEMÂNTICA
INSERT INTO tb_semantic_definition (type_name, description, format_script, validation_script) VALUES
('CPF', 'CPF com Mascaramento', 'value.toString().replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.***.***-$4")', 'value.toString().length() == 11'),
('MONEY', 'Formatação Monetária BRL', 'String.format("R$ %.2f", Double.valueOf(value.toString()))', 'Double.valueOf(value.toString()) >= 0');

-- 3. VERSÕES DOS PIPELINES (Agora com execution_strategy='ASYNC')
INSERT INTO tb_pipeline_version (pipeline_id, operation_type, version, timeout_ms, required_outputs, execution_strategy, is_active)
VALUES ('a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'CREDIT_ANALYSIS', 1, 30000, '["decisao_final"]', 'ASYNC', TRUE);

-- 4. NÓS DO PIPELINE
INSERT INTO tb_pipeline_node (node_id, pipeline_id, name, type, configuration) VALUES
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380f00', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'standardizer', 'NORMALIZATION', '{
    "config": { "rules": { "cliente_id": "/customer/document", "cliente_cpf": "/customer/document" } },
    "features": [
        { "type": "logging", "config": { "level": "INFO", "showBody": true } },
        { "type": "cache", "config": { "ttlMs": 300000, "key": "std_cache_#{raw.id}", "provider": "local" } },
        { "type": "retry", "config": { "maxAttempts": 3, "waitDurationMs": 500, "exponentialBackoff": true } }
    ]
}'),
('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380d02', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'get_score', 'HTTP', '{
    "behavior": { "guard": "cpf != null" },
    "config": { "url": "http://127.0.0.1:9999/v1/score/#{cpf}", "method": "GET" },
    "features": [
        { "type": "circuitbreak", "config": { "failureRateThreshold": 50.0, "waitDurationInOpenStateMs": 10000, "slidingWindowSize": 10 } }
    ]
}'),
('99999999-9999-9999-9999-999999999999', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'decision_task', 'SPEL', '{
    "config": { "expression": "score > 600 ? ''APROVADO'' : ''NEGADO''" }
}');

-- 5. FIAÇÃO
INSERT INTO tb_pipeline_node_input (node_id, local_key, source_signal, expected_semantic_type) VALUES
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380f00', 'raw', 'raw', NULL),
('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380d02', 'cpf', 'cliente_cpf', 'CPF'),
('99999999-9999-9999-9999-999999999999', 'score', 'score_atual', 'MONEY');

INSERT INTO tb_pipeline_node_output (node_id, local_key, target_signal, produced_semantic_type) VALUES
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380f00', 'cliente_id', 'cliente_id', NULL),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380f00', 'cliente_cpf', 'cliente_cpf', 'CPF'),
('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380d02', 'score_atual', 'score_atual', 'MONEY'),
('99999999-9999-9999-9999-999999999999', '', 'decisao_final', NULL);

-- 6. CONTRATOS (REGEX AJUSTADA)
INSERT INTO tb_data_contract (context_key, schema_definition, description) VALUES
('cliente_cpf', '{"type": "string", "minLength": 11, "maxLength": 11}', 'CPF do cliente'),
('score_atual', '{"type": "number", "minimum": 0, "maximum": 1000}', 'Score de crédito'),
('decisao_final', '{"type": "string", "enum": ["APROVADO", "NEGADO"]}', 'Resultado Final do Pipeline');

-- 7. REGRAS DE TAGS
INSERT INTO tb_tag_rule (tag_name, condition_expression, priority, is_active) VALUES
('VIP', '#headers[''customer-level''] == ''GOLD''', 10, TRUE),
('MOBILE', '#headers[''user-agent''].contains(''Android'')', 5, TRUE);
