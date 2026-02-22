-- =================================================================================
-- 1. INFRAESTRUTURA: Perfis e Templates (LIMPOS PARA TESTE DE PERFORMANCE)
-- =================================================================================

-- Deletamos as associações de features para o teste de performance pura
-- Isso remove RETRY, LOG_RESPONSE e VALIDACAO do caminho crítico.
DELETE FROM tb_profile_features;
DELETE FROM tb_feature_templates;

INSERT INTO tb_infra_profiles (profile_id, description) VALUES
('HTTP_PADRAO', 'Configuração básica (Sem Interceptores)'),
('HTTP_RESILIENTE', 'Configuração robusta (Sem Interceptores)');

-- =================================================================================
-- 2. CATÁLOGO DE TASKS (50 Tasks HTTP - Organizadas em 5 Camadas de Dependência)
-- =================================================================================

-- CAMADA 1: Tasks de Entrada (Dependem apenas do Contexto Inicial) - 10 Tasks
INSERT INTO tb_task_catalog (task_id, version, task_type, selector_expression, requires_json, produces_json, infra_profile_id, config_json) VALUES
('auth_01', 1, 'HTTP', 'true', '[{"name": "standard.documento"}]', '[{"name": "token_01", "path": "token"}]', 'HTTP_PADRAO', '{"method": "GET", "url": "http://127.0.0.1:9999/v1/auth/1"}'),
('auth_02', 1, 'HTTP', 'true', '[{"name": "standard.documento"}]', '[{"name": "token_02", "path": "token"}]', 'HTTP_PADRAO', '{"method": "GET", "url": "http://127.0.0.1:9999/v1/auth/2"}'),
('auth_03', 1, 'HTTP', 'true', '[{"name": "standard.documento"}]', '[{"name": "token_03", "path": "token"}]', 'HTTP_PADRAO', '{"method": "GET", "url": "http://127.0.0.1:9999/v1/auth/3"}'),
('auth_04', 1, 'HTTP', 'true', '[{"name": "standard.documento"}]', '[{"name": "token_04", "path": "token"}]', 'HTTP_PADRAO', '{"method": "GET", "url": "http://127.0.0.1:9999/v1/auth/4"}'),
('auth_05', 1, 'HTTP', 'true', '[{"name": "standard.documento"}]', '[{"name": "token_05", "path": "token"}]', 'HTTP_PADRAO', '{"method": "GET", "url": "http://127.0.0.1:9999/v1/auth/5"}'),
('init_01', 1, 'HTTP', 'true', '[{"name": "standard.documento"}]', '[{"name": "init_data_01", "path": "data"}]', 'HTTP_PADRAO', '{"method": "GET", "url": "http://127.0.0.1:9999/v1/init/1"}'),
('init_02', 1, 'HTTP', 'true', '[{"name": "standard.documento"}]', '[{"name": "init_data_02", "path": "data"}]', 'HTTP_PADRAO', '{"method": "GET", "url": "http://127.0.0.1:9999/v1/init/2"}'),
('init_03', 1, 'HTTP', 'true', '[{"name": "standard.documento"}]', '[{"name": "init_data_03", "path": "data"}]', 'HTTP_PADRAO', '{"method": "GET", "url": "http://127.0.0.1:9999/v1/init/3"}'),
('init_04', 1, 'HTTP', 'true', '[{"name": "standard.documento"}]', '[{"name": "init_data_04", "path": "data"}]', 'HTTP_PADRAO', '{"method": "GET", "url": "http://127.0.0.1:9999/v1/init/4"}'),
('init_05', 1, 'HTTP', 'true', '[{"name": "standard.documento"}]', '[{"name": "init_data_05", "path": "data"}]', 'HTTP_PADRAO', '{"method": "GET", "url": "http://127.0.0.1:9999/v1/init/5"}');

-- CAMADA 2: Enriquecimento (Dependem da Camada 1) - 10 Tasks
INSERT INTO tb_task_catalog (task_id, version, task_type, selector_expression, requires_json, produces_json, infra_profile_id, config_json) VALUES
('enrich_01', 1, 'HTTP', 'true', '[{"name": "token_01"}]', '[{"name": "ext_data_01", "path": "val"}]', 'HTTP_RESILIENTE', '{"method": "GET", "url": "http://127.0.0.1:9999/v1/enrich/1"}'),
('enrich_02', 1, 'HTTP', 'true', '[{"name": "token_02"}]', '[{"name": "ext_data_02", "path": "val"}]', 'HTTP_RESILIENTE', '{"method": "GET", "url": "http://127.0.0.1:9999/v1/enrich/2"}'),
('enrich_03', 1, 'HTTP', 'true', '[{"name": "token_03"}]', '[{"name": "ext_data_03", "path": "val"}]', 'HTTP_RESILIENTE', '{"method": "GET", "url": "http://127.0.0.1:9999/v1/enrich/3"}'),
('enrich_04', 1, 'HTTP', 'true', '[{"name": "token_04"}]', '[{"name": "ext_data_04", "path": "val"}]', 'HTTP_RESILIENTE', '{"method": "GET", "url": "http://127.0.0.1:9999/v1/enrich/4"}'),
('enrich_05', 1, 'HTTP', 'true', '[{"name": "token_05"}]', '[{"name": "ext_data_05", "path": "val"}]', 'HTTP_RESILIENTE', '{"method": "GET", "url": "http://127.0.0.1:9999/v1/enrich/5"}'),
('enrich_06', 1, 'HTTP', 'true', '[{"name": "init_data_01"}]', '[{"name": "ext_data_06", "path": "val"}]', 'HTTP_RESILIENTE', '{"method": "GET", "url": "http://127.0.0.1:9999/v1/enrich/6"}'),
('enrich_07', 1, 'HTTP', 'true', '[{"name": "init_data_02"}]', '[{"name": "ext_data_07", "path": "val"}]', 'HTTP_RESILIENTE', '{"method": "GET", "url": "http://127.0.0.1:9999/v1/enrich/7"}'),
('enrich_08', 1, 'HTTP', 'true', '[{"name": "init_data_03"}]', '[{"name": "ext_data_08", "path": "val"}]', 'HTTP_RESILIENTE', '{"method": "GET", "url": "http://127.0.0.1:9999/v1/enrich/8"}'),
('enrich_09', 1, 'HTTP', 'true', '[{"name": "init_data_04"}]', '[{"name": "ext_data_09", "path": "val"}]', 'HTTP_RESILIENTE', '{"method": "GET", "url": "http://127.0.0.1:9999/v1/enrich/9"}'),
('enrich_10', 1, 'HTTP', 'true', '[{"name": "init_data_05"}]', '[{"name": "ext_data_10", "path": "val"}]', 'HTTP_RESILIENTE', '{"method": "GET", "url": "http://127.0.0.1:9999/v1/enrich/10"}');

-- CAMADA 3: Bureaus e Validações (Dependem da Camada 2) - 10 Tasks
INSERT INTO tb_task_catalog (task_id, version, task_type, selector_expression, requires_json, produces_json, infra_profile_id, config_json) VALUES
('bureau_01', 1, 'HTTP', 'true', '[{"name": "ext_data_01"}]', '[{"name": "score_01", "path": "score"}]', 'HTTP_RESILIENTE', '{"method": "GET", "url": "http://127.0.0.1:9999/v1/bureau/1"}'),
('bureau_02', 1, 'HTTP', 'true', '[{"name": "ext_data_02"}]', '[{"name": "score_02", "path": "score"}]', 'HTTP_RESILIENTE', '{"method": "GET", "url": "http://127.0.0.1:9999/v1/bureau/2"}'),
('bureau_03', 1, 'HTTP', 'true', '[{"name": "ext_data_03"}]', '[{"name": "score_03", "path": "score"}]', 'HTTP_RESILIENTE', '{"method": "GET", "url": "http://127.0.0.1:9999/v1/bureau/3"}'),
('bureau_04', 1, 'HTTP', 'true', '[{"name": "ext_data_04"}]', '[{"name": "score_04", "path": "score"}]', 'HTTP_RESILIENTE', '{"method": "GET", "url": "http://127.0.0.1:9999/v1/bureau/4"}'),
('bureau_05', 1, 'HTTP', 'true', '[{"name": "ext_data_05"}]', '[{"name": "score_05", "path": "score"}]', 'HTTP_RESILIENTE', '{"method": "GET", "url": "http://127.0.0.1:9999/v1/bureau/5"}'),
('bureau_06', 1, 'HTTP', 'true', '[{"name": "ext_data_06"}]', '[{"name": "score_06", "path": "score"}]', 'HTTP_RESILIENTE', '{"method": "GET", "url": "http://127.0.0.1:9999/v1/bureau/6"}'),
('bureau_07', 1, 'HTTP', 'true', '[{"name": "ext_data_07"}]', '[{"name": "score_07", "path": "score"}]', 'HTTP_RESILIENTE', '{"method": "GET", "url": "http://127.0.0.1:9999/v1/bureau/7"}'),
('bureau_08', 1, 'HTTP', 'true', '[{"name": "ext_data_08"}]', '[{"name": "score_08", "path": "score"}]', 'HTTP_RESILIENTE', '{"method": "GET", "url": "http://127.0.0.1:9999/v1/bureau/8"}'),
('bureau_09', 1, 'HTTP', 'true', '[{"name": "ext_data_09"}]', '[{"name": "score_09", "path": "score"}]', 'HTTP_RESILIENTE', '{"method": "GET", "url": "http://127.0.0.1:9999/v1/bureau/9"}'),
('bureau_10', 1, 'HTTP', 'true', '[{"name": "ext_data_10"}]', '[{"name": "score_10", "path": "score"}]', 'HTTP_RESILIENTE', '{"method": "GET", "url": "http://127.0.0.1:9999/v1/bureau/10"}');

-- CAMADA 4: Antifraude e Agregações (Dependem da Camada 3) - 10 Tasks
INSERT INTO tb_task_catalog (task_id, version, task_type, selector_expression, requires_json, produces_json, infra_profile_id, config_json) VALUES
('fraud_01', 1, 'HTTP', 'true', '[{"name": "score_01"}]', '[{"name": "fraud_val_01", "path": "val"}]', 'HTTP_RESILIENTE', '{"method": "POST", "url": "http://127.0.0.1:9999/v1/fraud/1"}'),
('fraud_02', 1, 'HTTP', 'true', '[{"name": "score_02"}]', '[{"name": "fraud_val_02", "path": "val"}]', 'HTTP_RESILIENTE', '{"method": "POST", "url": "http://127.0.0.1:9999/v1/fraud/2"}'),
('fraud_03', 1, 'HTTP', 'true', '[{"name": "score_03"}]', '[{"name": "fraud_val_03", "path": "val"}]', 'HTTP_RESILIENTE', '{"method": "POST", "url": "http://127.0.0.1:9999/v1/fraud/3"}'),
('fraud_04', 1, 'HTTP', 'true', '[{"name": "score_04"}]', '[{"name": "fraud_val_04", "path": "val"}]', 'HTTP_RESILIENTE', '{"method": "POST", "url": "http://127.0.0.1:9999/v1/fraud/4"}'),
('fraud_05', 1, 'HTTP', 'true', '[{"name": "score_05"}]', '[{"name": "fraud_val_05", "path": "val"}]', 'HTTP_RESILIENTE', '{"method": "POST", "url": "http://127.0.0.1:9999/v1/fraud/5"}'),
('fraud_06', 1, 'HTTP', 'true', '[{"name": "score_06"}]', '[{"name": "fraud_val_06", "path": "val"}]', 'HTTP_RESILIENTE', '{"method": "POST", "url": "http://127.0.0.1:9999/v1/fraud/6"}'),
('fraud_07', 1, 'HTTP', 'true', '[{"name": "score_07"}]', '[{"name": "fraud_val_07", "path": "val"}]', 'HTTP_RESILIENTE', '{"method": "POST", "url": "http://127.0.0.1:9999/v1/fraud/7"}'),
('fraud_08', 1, 'HTTP', 'true', '[{"name": "score_08"}]', '[{"name": "fraud_val_08", "path": "val"}]', 'HTTP_RESILIENTE', '{"method": "POST", "url": "http://127.0.0.1:9999/v1/fraud/8"}'),
('fraud_09', 1, 'HTTP', 'true', '[{"name": "score_09"}]', '[{"name": "fraud_val_09", "path": "val"}]', 'HTTP_RESILIENTE', '{"method": "POST", "url": "http://127.0.0.1:9999/v1/fraud/9"}'),
('fraud_10', 1, 'HTTP', 'true', '[{"name": "score_10"}]', '[{"name": "fraud_val_10", "path": "val"}]', 'HTTP_RESILIENTE', '{"method": "POST", "url": "http://127.0.0.1:9999/v1/fraud/10"}');

-- CAMADA 5: Decisão Final (Dependem da Camada 4) - 10 Tasks
INSERT INTO tb_task_catalog (task_id, version, task_type, selector_expression, requires_json, produces_json, infra_profile_id, config_json) VALUES
('decision_01', 1, 'HTTP', 'true', '[{"name": "fraud_val_01"}]', '[{"name": "res_01", "path": "res"}]', 'HTTP_PADRAO', '{"method": "POST", "url": "http://127.0.0.1:9999/v1/decision/1"}'),
('decision_02', 1, 'HTTP', 'true', '[{"name": "fraud_val_02"}]', '[{"name": "res_02", "path": "res"}]', 'HTTP_PADRAO', '{"method": "POST", "url": "http://127.0.0.1:9999/v1/decision/2"}'),
('decision_03', 1, 'HTTP', 'true', '[{"name": "fraud_val_03"}]', '[{"name": "res_03", "path": "res"}]', 'HTTP_PADRAO', '{"method": "POST", "url": "http://127.0.0.1:9999/v1/decision/3"}'),
('decision_04', 1, 'HTTP', 'true', '[{"name": "fraud_val_04"}]', '[{"name": "res_04", "path": "res"}]', 'HTTP_PADRAO', '{"method": "POST", "url": "http://127.0.0.1:9999/v1/decision/4"}'),
('decision_05', 1, 'HTTP', 'true', '[{"name": "fraud_val_05"}]', '[{"name": "res_05", "path": "res"}]', 'HTTP_PADRAO', '{"method": "POST", "url": "http://127.0.0.1:9999/v1/decision/5"}'),
('decision_06', 1, 'HTTP', 'true', '[{"name": "fraud_val_06"}]', '[{"name": "res_06", "path": "res"}]', 'HTTP_PADRAO', '{"method": "POST", "url": "http://127.0.0.1:9999/v1/decision/6"}'),
('decision_07', 1, 'HTTP', 'true', '[{"name": "fraud_val_07"}]', '[{"name": "res_07", "path": "res"}]', 'HTTP_PADRAO', '{"method": "POST", "url": "http://127.0.0.1:9999/v1/decision/7"}'),
('decision_08', 1, 'HTTP', 'true', '[{"name": "fraud_val_08"}]', '[{"name": "res_08", "path": "res"}]', 'HTTP_PADRAO', '{"method": "POST", "url": "http://127.0.0.1:9999/v1/decision/8"}'),
('decision_09', 1, 'HTTP', 'true', '[{"name": "fraud_val_09"}]', '[{"name": "res_09", "path": "res"}]', 'HTTP_PADRAO', '{"method": "POST", "url": "http://127.0.0.1:9999/v1/decision/9"}'),
('decision_10', 1, 'HTTP', 'true', '[{"name": "fraud_val_10"}]', '[{"name": "resultado_final", "path": "res"}]', 'HTTP_PADRAO', '{"method": "POST", "url": "http://127.0.0.1:9999/v1/decision/10"}');

-- =================================================================================
-- 3. DEFINIÇÃO DE FLUXOS
-- =================================================================================

INSERT INTO tb_flow_config (operation_type, version, required_outputs, description, is_active) VALUES
('STANDARD_RISK', 1, '["resultado_final"]', 'Fluxo de Performance - 50 Tasks em 5 Camadas', true);

-- Composição do Fluxo (Todas as 50 tasks vinculadas)
INSERT INTO tb_flow_tasks (operation_type, flow_version, task_id, task_version)
SELECT 'STANDARD_RISK', 1, task_id, version FROM tb_task_catalog WHERE is_active = true;

-- =================================================================================
-- 4. PLANO DE INICIALIZAÇÃO
-- =================================================================================

INSERT INTO tb_initialization_plan (operation_type, description) VALUES
('STANDARD_RISK', 'Warmup de Conexões');

-- =================================================================================
-- 5. CONFIGURAÇÕES DE PIPELINE E OUTROS
-- =================================================================================

INSERT INTO tb_pipeline_config (operation_type, timeout_ms, description) VALUES
('STANDARD_RISK', 30000, 'Timeout global de 30 segundos');

INSERT INTO tb_input_normalization (operation_type, target_field, source_expression) VALUES
('STANDARD_RISK', 'valor', '#raw.transaction.amount'),
('STANDARD_RISK', 'documento', '#raw.customer.document');
