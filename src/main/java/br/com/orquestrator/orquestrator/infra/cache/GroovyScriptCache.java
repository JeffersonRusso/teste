package br.com.orquestrator.orquestrator.infra.cache;

import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.infra.groovy.OrchestratorScript;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class GroovyScriptCache {

    private final Map<String, Class<? extends Script>> scriptCache = new ConcurrentHashMap<>();
    private final GroovyClassLoader loader;

    public GroovyScriptCache() {
        CompilerConfiguration config = new CompilerConfiguration();
        config.setScriptBaseClass(OrchestratorScript.class.getName());
        config.setSourceEncoding("UTF-8");
        this.loader = new GroovyClassLoader(getClass().getClassLoader(), config);
    }

    @SuppressWarnings("unchecked")
    public Class<? extends Script> getOrCompile(String key, String scriptContent) {
        return scriptCache.computeIfAbsent(key, k -> {
            log.debug("Compilando script Groovy: {}", k);
            return (Class<? extends Script>) loader.parseClass(scriptContent, k);
        });
    }

    @SuppressWarnings("unchecked")
    public Class<? extends Script> getOrCompileFile(String scriptName) {
        return scriptCache.computeIfAbsent("file:" + scriptName, k -> {
            log.debug("Compilando arquivo de script Groovy: {}", scriptName);
            try {
                File scriptFile = new File("scripts/" + scriptName);
                if (!scriptFile.exists()) {
                    try (InputStream resource = getClass().getClassLoader().getResourceAsStream("scripts/" + scriptName)) {
                        if (resource == null) throw new IOException("Script não encontrado: " + scriptName);
                        String content = new String(resource.readAllBytes(), StandardCharsets.UTF_8);
                        return (Class<? extends Script>) loader.parseClass(content, scriptName);
                    }
                }
                return (Class<? extends Script>) loader.parseClass(scriptFile);
            } catch (IOException e) {
                throw new PipelineException("Falha ao compilar script: " + scriptName, e);
            }
        });
    }

    public void clear() {
        scriptCache.clear();
        loader.clearCache();
    }
}
