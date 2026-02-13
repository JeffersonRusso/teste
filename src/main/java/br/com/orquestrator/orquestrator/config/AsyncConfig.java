package br.com.orquestrator.orquestrator.config;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig {

    public static final String AUDIT_EXECUTOR = "auditTaskExecutor";

    @Bean(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    @Primary
    public AsyncTaskExecutor applicationTaskExecutor() {
        ThreadFactory factory = Thread.ofVirtual().name("orquestrador-async-", 0).factory();
        TaskExecutorAdapter executor = new TaskExecutorAdapter(Executors.newThreadPerTaskExecutor(factory));
        executor.setTaskDecorator(scopedValueDecorator());
        return executor;
    }

    @Bean(name = AUDIT_EXECUTOR)
    public Executor auditTaskExecutor() {
        ThreadFactory factory = Thread.ofVirtual().name("orquestrador-audit-", 0).factory();
        TaskExecutorAdapter executor = new TaskExecutorAdapter(Executors.newThreadPerTaskExecutor(factory));
        executor.setTaskDecorator(scopedValueDecorator());
        return executor;
    }

    @Bean(name = "applicationEventMulticaster")
    public ApplicationEventMulticaster applicationEventMulticaster(
            @Qualifier(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME) AsyncTaskExecutor taskExecutor) {
        SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
        eventMulticaster.setTaskExecutor(taskExecutor);
        eventMulticaster.setErrorHandler(ex -> log.error("Erro no processamento assíncrono de evento: {}", ex.getMessage()));
        return eventMulticaster;
    }

    /**
     * Decorator leve para propagar ScopedValues para threads assíncronas.
     */
    private TaskDecorator scopedValueDecorator() {
        return runnable -> {
            // Captura o ID atual (se houver)
            String correlationId = ContextHolder.getCorrelationId().orElse("-");
            return () -> ScopedValue.where(ContextHolder.CORRELATION_ID, correlationId).run(runnable);
        };
    }
}
