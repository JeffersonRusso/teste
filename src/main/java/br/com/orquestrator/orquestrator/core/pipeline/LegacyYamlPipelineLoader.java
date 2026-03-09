//package br.com.orquestrator.orquestrator.core.pipeline;
//
//import br.com.orquestrator.orquestrator.core.context.identity.RequestIdentity;
//import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
//import br.com.orquestrator.orquestrator.domain.model.PipelineDefinition;
//import br.com.orquestrator.orquestrator.domain.vo.NodeId;
//import br.com.orquestrator.orquestrator.infra.LegacyBridge;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StreamUtils;
//
//import java.nio.charset.StandardCharsets;
//import java.util.*;
//
///**
// * LegacyYamlPipelineLoader: Carrega e traduz pipelines YAML do sistema legado.
// * Agora desacoplado do ExecutionContext e focado na identidade da requisição.
// */
//@Slf4j
//@Component
//@LegacyBridge(description = "Carrega e traduz pipelines YAML do sistema legado para o motor moderno.")
//public class LegacyYamlPipelineLoader implements PipelineLoader {
//
//    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
//    private final Map<String, Map<String, Object>> legacyPipelines = new HashMap<>();
//    private final Map<String, String> aviatorScripts = new HashMap<>();
//    private final TaskRepository taskRepository;
//
//    public LegacyYamlPipelineLoader(TaskRepository taskRepository) {
//        this.taskRepository = taskRepository;
//        loadAllFromResources();
//    }
//
//    @Override
//    public Optional<PipelineDefinition> load(RequestIdentity identity) {
//        String operationType = identity.getOperationType();
//        Map<String, Object> raw = legacyPipelines.get(operationType);
//        if (raw == null) return Optional.empty();
//        return Optional.of(translate(raw));
//    }
//
//    @Override
//    public boolean supports(RequestIdentity identity) {
//        return identity.getTags().contains("legacy") && legacyPipelines.containsKey(identity.getOperationType());
//    }
//
//    @SuppressWarnings("unchecked")
//    private PipelineDefinition translate(Map<String, Object> raw) {
//        Map<String, Object> pipe = (Map<String, Object>) raw.get("pipeline");
//        String opType = (String) pipe.get("tipoOperacao");
//        int timeout = (int) pipe.get("timeOut");
//
//        List<Map<String, Object>> enrichments = (List<Map<String, Object>>) pipe.get("enriquecimentos");
//        List<TaskDefinition> tasks = new ArrayList<>();
//        Map<String, String> globalMapping = new HashMap<>();
//        Set<String> requiredOutputs = new HashSet<>();
//
//        for (var enr : enrichments) {
//            String alias = (String) enr.get("alias");
//            String name = (String) enr.get("nome");
//
//            List<Map<String, String>> mappings = findMappings(enr);
//            Map<String, String> taskInputs = new HashMap<>();
//
//            for (var m : mappings) {
//                String de = m.get("de");
//                String para = m.get("para");
//                String conversor = m.get("conversor");
//
//                if (conversor != null && !conversor.isBlank()) {
//                    String convId = alias + "_" + para + "_in_conv";
//                    tasks.add(createConverterTask(convId, de, para, conversor));
//                    taskInputs.put(para, convId);
//                } else {
//                    taskInputs.put(para, translateLegacyPath(de));
//                }
//            }
//
//            TaskDefinition modernDef = taskRepository.findByName(name)
//                    .orElseThrow(() -> new RuntimeException("Task moderna não encontrada: " + name));
//
////            tasks.add(new TaskDefinition(
////                new NodeId(alias), modernDef.version(), modernDef.name(), modernDef.type(),
////                (int) enr.get("timeout"), modernDef.config(), modernDef.features(),
//////                modernDef.failFast(), taskInputs, Map.of(".", alias),
//////                Set.of("default"), null, false, 0
////            ));
//
//            requiredOutputs.add(alias);
//        }
//
//        return new PipelineDefinition(opType, 1, timeout, globalMapping, requiredOutputs, tasks);
//    }
//
//    private TaskDefinition createConverterTask(String id, String sourcePath, String targetKey, String scriptName) {
//        String scriptCode = aviatorScripts.getOrDefault(scriptName, "return input;");
//        return new TaskDefinition(
//            new NodeId(id), 1, scriptName, "AVIATOR", 100,
//            Map.of("script", scriptCode), List.of(), true,
//            Map.of("input", translateLegacyPath(sourcePath)),
//            Map.of(".", targetKey),
//            Set.of("default"), null, false, 0
//        );
//    }
//
//    @SuppressWarnings("unchecked")
//    private List<Map<String, String>> findMappings(Map<String, Object> enr) {
//        for (Object value : enr.values()) {
//            if (value instanceof List<?> list && !list.isEmpty()) {
//                Object first = list.get(0);
//                if (first instanceof Map<?, ?> m && m.containsKey("de") && m.containsKey("para")) {
//                    return (List<Map<String, String>>) list;
//                }
//            }
//        }
//        return List.of();
//    }
//
//    private String translateLegacyPath(String legacyPath) {
//        if (legacyPath == null || legacyPath.isBlank()) return "";
//        // Traduz para o novo padrão de sinais (raw em vez de $payload)
//        if (legacyPath.startsWith("$payload.")) return "raw." + legacyPath.substring(9);
//        if (legacyPath.startsWith("$enriquecimentos.")) return legacyPath.substring(17);
//        return legacyPath;
//    }
//
//    private void loadAllFromResources() {
//        try {
//            var resolver = new PathMatchingResourcePatternResolver();
//            Resource[] pipeResources = resolver.getResources("classpath:pipelines/legacy/*.yaml");
//            for (Resource res : pipeResources) {
//                Map<String, Object> content = yamlMapper.readValue(res.getInputStream(), Map.class);
//                Map<String, Object> pipe = (Map<String, Object>) content.get("pipeline");
//                legacyPipelines.put((String) pipe.get("tipoOperacao"), content);
//            }
//            Resource[] scriptResources = resolver.getResources("classpath:scripts/aviator/*.av");
//            for (Resource res : scriptResources) {
//                String code = StreamUtils.copyToString(res.getInputStream(), StandardCharsets.UTF_8);
//                String name = res.getFilename().replace(".av", "");
//                aviatorScripts.put(name, code);
//            }
//        } catch (Exception e) {
//            log.error("Erro ao carregar recursos legados: {}", e.getMessage());
//        }
//    }
//}
