//package br.com.orquestrator.orquestrator.core.pipeline;
//
//import br.com.orquestrator.orquestrator.domain.model.DataSpec;
//import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
//import org.springframework.stereotype.Component;
//
//import java.util.*;
//
///**
// * PipelinePlanner: Organiza tasks em camadas paralelas.
// * Otimizado para evitar Stream/Lambda e buscas O(N) no hot path.
// */
//@Component
//public class PipelinePlanner {
//
//    public List<List<TaskDefinition>> plan(List<TaskDefinition> tasks, Set<String> initialKeys) {
//        List<List<TaskDefinition>> layers = new ArrayList<>();
//        Set<String> available = new HashSet<>(initialKeys);
//        List<TaskDefinition> remaining = new ArrayList<>(tasks);
//
//        while (!remaining.isEmpty()) {
//            List<TaskDefinition> ready = new ArrayList<>();
//
//            // OTIMIZAÇÃO: Loop manual para evitar overhead de Stream
//            Iterator<TaskDefinition> iterator = remaining.iterator();
//            while (iterator.hasNext()) {
//                TaskDefinition t = iterator.next();
//                if (isReady(t, available)) {
//                    ready.add(t);
//                    iterator.remove();
//                }
//            }
//
//            if (ready.isEmpty()) {
//                layers.add(new ArrayList<>(remaining));
//                break;
//            }
//
//            layers.add(ready);
//            // OTIMIZAÇÃO: Adiciona chaves produzidas de forma eficiente
//            for (TaskDefinition t : ready) {
//                available.add(t.getNodeId().value());
//                List<DataSpec> produces = t.getProduces();
//                if (produces != null) {
//                    for (DataSpec p : produces) {
//                        available.add(p.name());
//                    }
//                }
//            }
//        }
//        return layers;
//    }
//
//    private boolean isReady(TaskDefinition task, Set<String> available) {
//        List<DataSpec> requires = task.getRequires();
//        if (requires == null || requires.isEmpty()) return true;
//
//        for (DataSpec req : requires) {
//            if (!req.optional() && !isAvailable(req.name(), available)) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    private boolean isAvailable(String key, Set<String> available) {
//        // OTIMIZAÇÃO: O(1) lookup primeiro
//        if (available.contains(key)) return true;
//
//        // OTIMIZAÇÃO: Busca por prefixo apenas se necessário, evitando Stream
//        for (String k : available) {
//            if (key.startsWith(k) && key.length() > k.length() && key.charAt(k.length()) == '.') {
//                return true;
//            }
//        }
//        return false;
//    }
//}
