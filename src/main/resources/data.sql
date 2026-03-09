-- LIMPEZA TOTAL PARA RECARGA OTIMIZADA
DELETE FROM tb_pipeline_node_input;
DELETE FROM tb_pipeline_node_output;
DELETE FROM tb_pipeline_node_decorator;
DELETE FROM tb_pipeline_node;
DELETE FROM tb_pipeline_version;
DELETE FROM tb_input_normalization;
DELETE FROM tb_data_contract;
DELETE FROM tb_semantic_definition;

-- 1. SEMÂNTICA
INSERT INTO tb_semantic_definition (type_name, description) VALUES ('CPF', 'CPF Formatado'), ('MONEY', 'Moeda BRL');

-- 2. VERSÃO DO PIPELINE
INSERT INTO tb_pipeline_version (pipeline_id, operation_type, version, timeout_ms, required_outputs, is_active)
VALUES ('a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'CREDIT_ANALYSIS', 1, 30000, '["/decisao_final"]', TRUE);

-- 3. NÍVEL 0: NORMALIZAÇÃO
INSERT INTO tb_pipeline_node (node_id, pipeline_id, name, type, configuration) VALUES
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380f00', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'standardizer', 'NORMALIZATION',
 '{"rules": {"cliente_id": "/raw/customer/id", "cliente_cpf": "/raw/customer/document"}}');

INSERT INTO tb_pipeline_node_input (node_id, local_key, source_signal, source_path) VALUES
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380f00', 'raw', 'raw', NULL);

INSERT INTO tb_pipeline_node_output (node_id, local_key, target_signal) VALUES
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380f00', 'cliente_id', 'cliente_id'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380f00', 'cliente_cpf', 'cliente_cpf');

-- 4. NÍVEL 1: FUNDAÇÃO
INSERT INTO tb_pipeline_node (node_id, pipeline_id, name, type, configuration) VALUES
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380b01', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'auth_task', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/auth", "method": "POST"}'),
('c2eebc99-9c0b-4ef8-bb6d-6bb9bd380c02', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'get_cliente', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/cliente/#{doc}", "method": "GET"}');

INSERT INTO tb_pipeline_node_input (node_id, local_key, source_signal, source_path) VALUES
('c2eebc99-9c0b-4ef8-bb6d-6bb9bd380c02', 'doc', 'cliente_cpf', NULL);

INSERT INTO tb_pipeline_node_output (node_id, local_key, target_signal) VALUES
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380b01', 'access_token', 'token_sessao'),
('c2eebc99-9c0b-4ef8-bb6d-6bb9bd380c02', '.', 'perfil_cliente');

-- 5. NÍVEL 2: PERFIL BÁSICO
INSERT INTO tb_pipeline_node (node_id, pipeline_id, name, type, configuration) VALUES
('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380d01', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'get_conta', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/conta/#{cid}", "method": "GET"}'),
('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380d02', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'get_score', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/score/#{cpf}", "method": "GET"}'),
('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380d03', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'get_hotlist', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/hotlist/#{cpf}", "method": "GET"}');

INSERT INTO tb_pipeline_node_input (node_id, local_key, source_signal, source_path) VALUES
('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380d01', 'cid', 'perfil_cliente', '/id'),
('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380d02', 'cpf', 'cliente_cpf', NULL),
('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380d03', 'cpf', 'cliente_cpf', NULL);

INSERT INTO tb_pipeline_node_output (node_id, local_key, target_signal) VALUES
('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380d01', 'id_conta', 'id_conta'),
('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380d02', 'score_atual', 'score_atual'),
('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380d03', 'status', 'status_hotlist');

-- 6. NÍVEL 3: DETALHAMENTO
INSERT INTO tb_pipeline_node (node_id, pipeline_id, name, type, configuration) VALUES
('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380e01', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'get_investimentos', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/investimentos/#{aid}", "method": "GET"}'),
('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380e02', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'get_cartoes', 'HTTP', '{"url": "http://127.0.0.1:9999/v1/cartoes/#{aid}", "method": "GET"}');

INSERT INTO tb_pipeline_node_input (node_id, local_key, source_signal, source_path) VALUES
('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380e01', 'aid', 'id_conta', NULL),
('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380e02', 'aid', 'id_conta', NULL);

INSERT INTO tb_pipeline_node_output (node_id, local_key, target_signal) VALUES
('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380e01', 'patrimonio_total', 'patrimonio_total'),
('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380e02', 'id_cartao', 'id_cartao');

-- 7. DECISÃO FINAL
INSERT INTO tb_pipeline_node (node_id, pipeline_id, name, type, configuration) VALUES
('99999999-9999-9999-9999-999999999999', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'decision_task', 'SPEL',
 '{"expression": "#{score > 600 && status == ''LIBERADO'' && patrimonio > 10000 ? ''APROVADO'' : ''NEGADO''}"}');

INSERT INTO tb_pipeline_node_input (node_id, local_key, source_signal, source_path) VALUES
('99999999-9999-9999-9999-999999999999', 'score', 'score_atual', NULL),
('99999999-9999-9999-9999-999999999999', 'status', 'status_hotlist', NULL),
('99999999-9999-9999-9999-999999999999', 'patrimonio', 'patrimonio_total', NULL);

INSERT INTO tb_pipeline_node_output (node_id, local_key, target_signal) VALUES
('99999999-9999-9999-9999-999999999999', '.', 'decisao_final');

-- 8. CONTRATO
INSERT INTO tb_data_contract (context_key, is_required) VALUES
('cliente_id', TRUE),
('score_atual', TRUE),
('status_hotlist', TRUE),
('patrimonio_total', TRUE),
('decisao_final', TRUE);
