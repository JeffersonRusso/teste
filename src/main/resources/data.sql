-- =================================================================================
-- 1. SEMÂNTICA DE DADOS
-- =================================================================================
INSERT INTO tb_semantic_definition (type_name, format_script, description) VALUES
('CPF', 'return string.substring(it, 0, 3) + ".***.***-" + string.substring(it, 9);', 'Máscara de CPF'),
('EMAIL', 'let at = string.indexOf(it, "@"); return string.substring(it, 0, 2) + "****" + string.substring(it, at);', 'Máscara de E-mail'),
('MONEY', 'return "R$ " + it;', 'Formatação de Moeda');

-- =================================================================================
-- 2. NORMALIZAÇÃO DE ENTRADA (Usando #{...} para expressões)
-- =================================================================================
INSERT INTO tb_input_normalization (operation_type, target_field, source_expression) VALUES
('CREDIT_ANALYSIS', 'documento', '#{#raw.customer.document}'),
('CREDIT_ANALYSIS', 'valor_solicitado', '#{#raw.transaction.amount}');

-- =================================================================================
-- 3. TEMPLATES DE DECORADORES
-- =================================================================================
INSERT INTO tb_decorator_template (template_id, type, phase, default_configuration) VALUES
('RETRY_3X', 'RETRY', 'AROUND', '{"maxAttempts": 3, "waitDurationMs": 200}'),
('LOG_BASIC', 'LOGGING', 'POST', '{"level": "INFO", "showBody": false}'),
('CB_STANDARD', 'CIRCUIT_BREAKER', 'AROUND', '{"failureRateThreshold": 50.0, "waitDurationInOpenStateMs": 10000, "slidingWindowSize": 10}');

-- =================================================================================
-- 4. VERSÃO DO PIPELINE
-- =================================================================================
INSERT INTO tb_pipeline_version (pipeline_id, operation_type, version, timeout_ms, required_outputs, is_active)
VALUES ('a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'CREDIT_ANALYSIS', 1, 15000, '["decisao_final"]', TRUE);

-- =================================================================================
-- 5. NÓS DO PIPELINE (35 TASKS)
-- =================================================================================

-- CAMADA 1: Entrada
INSERT INTO tb_pipeline_node (node_id, pipeline_id, name, type, configuration) VALUES
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380b01', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'auth_task', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/auth", "method": "GET"}'),
('c2eebc99-9c0b-4ef8-bb6d-6bb9bd380c02', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'enrich_task', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/enrich", "method": "GET"}');

INSERT INTO tb_pipeline_node_output (node_id, local_key, target_key) VALUES
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380b01', '#{token}', 'token_sessao'),
('c2eebc99-9c0b-4ef8-bb6d-6bb9bd380c02', '.', 'dados_cliente');

-- CAMADA 2: Consultas Cadastrais
INSERT INTO tb_pipeline_node (node_id, pipeline_id, name, type, configuration) VALUES
('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380d01', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'check_receita', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/receita", "method": "GET"}'),
('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380d02', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'check_cnh', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/detran", "method": "GET"}'),
('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380d03', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'check_antecedentes', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/policia", "method": "GET"}'),
('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380d04', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'check_pep', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/pep", "method": "GET"}'),
('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380d05', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'check_sanctions', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/sanctions", "method": "GET"}');

INSERT INTO tb_pipeline_node_input (node_id, local_key, source_expression) VALUES
('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380d01', 'token', 'token_sessao'), ('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380d02', 'dados', 'dados_cliente'), ('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380d03', 'dados', 'dados_cliente'), ('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380d04', 'dados', 'dados_cliente'), ('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380d05', 'dados', 'dados_cliente');

INSERT INTO tb_pipeline_node_output (node_id, local_key, target_key) VALUES
('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380d01', '.', 'receita_status'), ('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380d02', '.', 'cnh_status'), ('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380d03', '.', 'antecedentes'), ('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380d04', '.', 'is_pep'), ('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380d05', '.', 'sanctions');

-- CAMADA 3: Consultas Financeiras
INSERT INTO tb_pipeline_node (node_id, pipeline_id, name, type, configuration) VALUES
('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380e06', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'open_finance_itau', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/banks/itau", "method": "GET"}'),
('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380e07', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'open_finance_bradesco', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/banks/bradesco", "method": "GET"}'),
('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380e08', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'open_finance_nubank', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/banks/nubank", "method": "GET"}'),
('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380e09', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'scr_bacen', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/bacen/scr", "method": "GET"}'),
('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380e10', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'protestos', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/cartorio", "method": "GET"}');

INSERT INTO tb_pipeline_node_input (node_id, local_key, source_expression) VALUES
('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380e06', 'status', 'receita_status'), ('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380e07', 'status', 'receita_status'), ('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380e08', 'status', 'receita_status'), ('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380e09', 'status', 'receita_status'), ('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380e10', 'status', 'receita_status');

INSERT INTO tb_pipeline_node_output (node_id, local_key, target_key) VALUES
('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380e06', '.', 'saldo_itau'), ('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380e07', '.', 'saldo_bradesco'), ('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380e08', '.', 'saldo_nubank'), ('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380e09', '.', 'divida_total'), ('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380e10', '.', 'protestos');

-- CAMADA 4: Fraude e Interno
INSERT INTO tb_pipeline_node (node_id, pipeline_id, name, type, configuration) VALUES
('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380f11', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'device_fingerprint', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/device", "method": "GET"}'),
('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380f12', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'ip_reputation', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/ip", "method": "GET"}'),
('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380f13', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'behavior_biometrics', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/behavior", "method": "POST"}'),
('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380f14', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'sim_swap', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/telco/simswap", "method": "GET"}'),
('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380f15', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'email_age', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/email/age", "method": "POST"}'),
('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380f16', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'internal_history', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/internal/history", "method": "GET"}'),
('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380f17', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'internal_limits', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/internal/limits", "method": "GET"}'),
('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380f18', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'internal_blocklist', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/internal/blocklist", "method": "GET"}'),
('00000000-0000-0000-0000-000000000001', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'internal_vip', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/internal/vip", "method": "GET"}'),
('00000000-0000-0000-0000-000000000002', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'internal_campaigns', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/internal/campaigns", "method": "GET"}');

INSERT INTO tb_pipeline_node_input (node_id, local_key, source_expression) VALUES
('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380f11', 'divida', 'divida_total'), ('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380f12', 'divida', 'divida_total'), ('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380f13', 'divida', 'divida_total'), ('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380f14', 'divida', 'divida_total'), ('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380f15', 'divida', 'divida_total'),
('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380f16', 'divida', 'divida_total'), ('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380f17', 'divida', 'divida_total'), ('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380f18', 'divida', 'divida_total'), ('00000000-0000-0000-0000-000000000001', 'divida', 'divida_total'), ('00000000-0000-0000-0000-000000000002', 'divida', 'divida_total');

INSERT INTO tb_pipeline_node_output (node_id, local_key, target_key) VALUES
('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380f11', '.', 'device_score'), ('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380f12', '.', 'ip_score'), ('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380f13', '.', 'bio_score'), ('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380f14', '.', 'sim_swap_status'), ('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380f15', '.', 'email_score'),
('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380f16', '.', 'historico_interno'), ('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380f17', '.', 'limites_internos'), ('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380f18', '.', 'is_blocked'), ('00000000-0000-0000-0000-000000000001', '.', 'is_vip'), ('00000000-0000-0000-0000-000000000002', '.', 'active_campaigns');

-- CAMADA 5: Decisão e Ofertas
INSERT INTO tb_pipeline_node (node_id, pipeline_id, name, type, configuration) VALUES
('00000000-0000-0000-0000-000000000021', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'partner_a', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/partners/a/offers", "method": "POST"}'),
('00000000-0000-0000-0000-000000000022', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'partner_b', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/partners/b/offers", "method": "POST"}'),
('00000000-0000-0000-0000-000000000023', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'partner_c', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/partners/c/offers", "method": "POST"}'),
('00000000-0000-0000-0000-000000000024', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'partner_d', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/partners/d/offers", "method": "POST"}'),
('00000000-0000-0000-0000-000000000025', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'partner_e', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/partners/e/offers", "method": "POST"}'),
('00000000-0000-0000-0000-000000000026', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'decision_task', 'SPEL', '{"expression": "#{device_score > 50 ? ''APROVADO'' : ''NEGADO''}"}'),
('00000000-0000-0000-0000-000000000027', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'calc_renda', 'AVIATOR', '{"script": "return input * 1.2;"}'),
('00000000-0000-0000-0000-000000000028', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'sum_task', 'AVIATOR', '{"script": "return 5000.0;"}'),
('00000000-0000-0000-0000-000000000029', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'bureau_task', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/bureau", "method": "GET"}'),
('00000000-0000-0000-0000-000000000030', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'calc_risco_geo', 'AVIATOR', '{"script": "return 10;"}'),
('00000000-0000-0000-0000-000000000031', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'format_nome', 'AVIATOR', '{"script": "return \"JEFFERSON\";"}'),
('00000000-0000-0000-0000-000000000032', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'check_idade', 'AVIATOR', '{"script": "return true;"}'),
('00000000-0000-0000-0000-000000000033', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'calc_limite_teto', 'AVIATOR', '{"script": "return 1000.0;"}');

INSERT INTO tb_pipeline_node_input (node_id, local_key, source_expression) VALUES
('00000000-0000-0000-0000-000000000021', 'score', 'device_score'), ('00000000-0000-0000-0000-000000000022', 'score', 'device_score'), ('00000000-0000-0000-0000-000000000023', 'score', 'device_score'), ('00000000-0000-0000-0000-000000000024', 'score', 'device_score'), ('00000000-0000-0000-0000-000000000025', 'score', 'device_score'),
('00000000-0000-0000-0000-000000000026', 'device_score', 'device_score'), ('00000000-0000-0000-0000-000000000027', 'input', 'standard.valor_solicitado'), ('00000000-0000-0000-0000-000000000028', 'transactions', 'raw.customer'), ('00000000-0000-0000-0000-000000000029', 'doc', 'standard.documento'),
('00000000-0000-0000-0000-000000000030', 'input', 'standard.documento'), ('00000000-0000-0000-0000-000000000031', 'input', 'standard.documento'), ('00000000-0000-0000-0000-000000000032', 'input', 'standard.documento'), ('00000000-0000-0000-0000-000000000033', 'input', 'standard.valor_solicitado');

INSERT INTO tb_pipeline_node_output (node_id, local_key, target_key) VALUES
('00000000-0000-0000-0000-000000000021', '.', 'offer_a'), ('00000000-0000-0000-0000-000000000022', '.', 'offer_b'), ('00000000-0000-0000-0000-000000000023', '.', 'offer_c'), ('00000000-0000-0000-0000-000000000024', '.', 'offer_d'), ('00000000-0000-0000-0000-000000000025', '.', 'offer_e'),
('00000000-0000-0000-0000-000000000026', '.', 'decisao_final'), ('00000000-0000-0000-0000-000000000027', '.', 'renda_ajustada'), ('00000000-0000-0000-0000-000000000028', '.', 'valor_total_transacoes'), ('00000000-0000-0000-0000-000000000029', '.', 'score_credito'),
('00000000-0000-0000-0000-000000000030', '.', 'risco_geo'), ('00000000-0000-0000-0000-000000000031', '.', 'nome_upper'), ('00000000-0000-0000-0000-000000000032', '.', 'maior_idade'), ('00000000-0000-0000-0000-000000000033', '.', 'limite_maximo');

-- =================================================================================
-- 7. ASSOCIAÇÃO DE DECORADORES
-- =================================================================================
INSERT INTO tb_pipeline_node_decorator (node_id, template_id, execution_order) VALUES
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380b01', 'RETRY_3X', 10),
('c2eebc99-9c0b-4ef8-bb6d-6bb9bd380c02', 'LOG_BASIC', 100),
('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380e09', 'CB_STANDARD', 50);

-- =================================================================================
-- 8. REGRAS DE TAGS
-- =================================================================================
INSERT INTO tb_tag_rule (tag_name, condition_expression, priority)
VALUES ('audit', '#{#raw.customer.document != null}', 100);

-- =================================================================================
-- 9. CONTRATO DE DADOS
-- =================================================================================
INSERT INTO tb_data_contract (context_key, data_type, semantic_type, is_required) VALUES
('token_sessao', 'STRING', NULL, TRUE),
('dados_cliente', 'OBJECT', NULL, TRUE),
('standard.documento', 'STRING', 'CPF', TRUE),
('receita_status', 'OBJECT', NULL, TRUE),
('divida_total', 'OBJECT', NULL, TRUE),
('device_score', 'OBJECT', NULL, TRUE),
('decisao_final', 'STRING', NULL, TRUE);
