//package br.com.orquestrator.orquestrator.core;
//
//import br.com.orquestrator.orquestrator.core.context.identity.RequestIdentity;
//import br.com.orquestrator.orquestrator.core.engine.runtime.ExecutionSession;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.Map;
//
//@Service
//@RequiredArgsConstructor
//public class RiskAnalysisService {
//
//    private final ExecutionSession executionSession;
//
//    public Map<String, Object> analyze(RequestIdentity identity, Map<String, String> headers, Map<String, Object> body) {
//        return executionSession.run(identity, headers, body);
//    }
//}
