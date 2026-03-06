//package br.com.orquestrator.orquestrator.tasks.interceptor.core;
//
//import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
//import br.com.orquestrator.orquestrator.tasks.base.Task;
//import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
//import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
//import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
//import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
//import lombok.extern.slf4j.Slf4j;
//
//import java.util.List;
//
//@Slf4j
//public class DecoratorChain implements Task {
//
//    private final TaskChain executionChain;
//    private final String nodeId;
//
//    public DecoratorChain(final Task coreTask, final List<TaskDecorator> decorators, final TaskDefinition taskDefinition) {
//        this.nodeId = taskDefinition.nodeId().value();
//
//        TaskChain chain = coreTask::execute;
//        for (int i = decorators.size() - 1; i >= 0; i--) {
//            final TaskDecorator decorator = decorators.get(i);
//            final TaskChain next = chain;
//            chain = (ctx) -> decorator.apply(ctx, next);
//        }
//        this.executionChain = chain;
//    }
//
//    @Override
//    public TaskResult execute(TaskContext context) {
//        try {
//            return executionChain.proceed(context);
//        } catch (Exception e) {
//            throw new RuntimeException("Falha na execução da task " + nodeId, e);
//        }
//    }
//}
