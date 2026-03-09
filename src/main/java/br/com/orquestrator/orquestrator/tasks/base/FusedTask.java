//package br.com.orquestrator.orquestrator.tasks.base;
//
//import br.com.orquestrator.orquestrator.domain.model.DataValue;
//import lombok.RequiredArgsConstructor;
//import java.util.List;
//import java.util.Map;
//
///**
// * FusedTask: Executa uma lista de tarefas em sequência na mesma thread.
// */
//@RequiredArgsConstructor
//public class FusedTask implements Task {
//
//    private final List<Task> tasks;
//
//    @Override
//    public TaskResult execute(Map<String, DataValue> inputs) {
//        TaskResult lastResult = null;
//        for (Task task : tasks) {
//            lastResult = task.execute(inputs);
//        }
//        return lastResult;
//    }
//}
