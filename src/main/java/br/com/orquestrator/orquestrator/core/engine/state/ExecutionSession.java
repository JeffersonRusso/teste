//package br.com.orquestrator.orquestrator.core.engine.state;
//
//import br.com.orquestrator.orquestrator.core.context.OrquestratorContext;
//import br.com.orquestrator.orquestrator.core.pipeline.service.PipelineService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class ExecutionSession {
//
//    private final PipelineService pipelineService;
//
//    public Map<String, Object> execute(Map<String, Object> input) {
//        var identity = OrquestratorContext.get();
//
//        log.info("Iniciando execução para operação: {}", identity.getOperationType());
//        return pipelineService.execute(input);
//    }
//}
