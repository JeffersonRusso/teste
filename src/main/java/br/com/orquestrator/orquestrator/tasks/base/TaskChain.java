package br.com.orquestrator.orquestrator.tasks.base;

@FunctionalInterface
public interface TaskChain {
    TaskResult proceed(TaskContext context);
}
