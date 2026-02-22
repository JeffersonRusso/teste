package br.com.orquestrator.orquestrator.core.context.init;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class ContextInitializerRegistry {

    private final ApplicationContext applicationContext;
    private final Map<String, ContextTaskInitializer> registry = new ConcurrentHashMap<>();

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent() {
        registry.putAll(applicationContext.getBeansOfType(ContextTaskInitializer.class));
    }

    public Optional<ContextTaskInitializer> getInitializer(String name) {
        return Optional.ofNullable(registry.get(name));
    }
}
