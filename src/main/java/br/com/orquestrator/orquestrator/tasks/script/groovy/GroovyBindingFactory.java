package br.com.orquestrator.orquestrator.tasks.script.groovy;

import com.fasterxml.jackson.databind.JsonNode;
import groovy.lang.Binding;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GroovyBindingFactory {
    public Binding createBinding(Map<String, JsonNode> inputs) {
        Binding binding = new Binding();
        binding.setVariable("inputs", inputs);
        binding.setVariable("ctx", inputs);
        return binding;
    }
}
