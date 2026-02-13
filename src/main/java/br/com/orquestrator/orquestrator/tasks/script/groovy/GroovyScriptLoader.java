package br.com.orquestrator.orquestrator.tasks.script.groovy;

import br.com.orquestrator.orquestrator.infra.cache.GroovyScriptCache;
import groovy.lang.Script;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GroovyScriptLoader {

    private final GroovyScriptCache scriptCache;

    public Class<? extends Script> loadFromSource(String identifier, String source) {
        return scriptCache.getOrCompile(identifier, source);
    }

    public Class<? extends Script> loadFromFile(String scriptName) {
        return scriptCache.getOrCompileFile(scriptName);
    }
}
