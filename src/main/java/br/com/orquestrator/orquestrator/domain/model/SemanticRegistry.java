package br.com.orquestrator.orquestrator.domain.model;

import br.com.orquestrator.orquestrator.infra.semantic.ScriptedSemanticHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class SemanticRegistry {

    private final JdbcTemplate jdbcTemplate;
    private final Map<String, SemanticHandler> handlers = new ConcurrentHashMap<>();
    private static SemanticRegistry instance;

    @PostConstruct
    public void loadFromDatabase() {
        log.info("Carregando definições semânticas do banco...");
        jdbcTemplate.query("SELECT * FROM tb_semantic_definition", (rs, rowNum) -> {
            String typeName = rs.getString("type_name");
            String format = rs.getString("format_script");
            String plus = rs.getString("plus_script");
            String concat = rs.getString("concat_script");
            
            handlers.put(typeName.toUpperCase(), new ScriptedSemanticHandler(typeName, format, plus, concat));
            return null;
        });
        instance = this;
    }

    public static SemanticHandler getHandler(String typeName) {
        if (instance == null) return null;
        return instance.handlers.get(typeName.toUpperCase());
    }
}
