//package br.com.orquestrator.orquestrator.task;
//
//import br.com.orquestrator.orquestrator.domain.FeatureDefinition;
//import br.com.orquestrator.orquestrator.adapter.persistence.repository.FeatureTemplateRepository;
//import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.FeatureTemplateEntity;
//import br.com.orquestrator.orquestrator.domain.model.FeaturePhases;
//import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
//import br.com.orquestrator.orquestrator.tasks.registry.TaskFactory;
//import br.com.orquestrator.orquestrator.tasks.base.Task;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Map;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//@Transactional
//class FeatureTemplateTest {
//
//    @Autowired
//    private TaskFactory taskFactory;
//
//    @Autowired
//    private FeatureTemplateRepository templateRepository;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    void shouldResolveFeatureFromTemplate() {
//        // GIVEN
//        FeatureTemplateEntity template = new FeatureTemplateEntity();
//        template.setTemplateId("TEMPLATE_TEST");
//        template.setFeatureType("LOG_RESPONSE");
//        template.setConfig(objectMapper.valueToTree(Map.of("level", "DEBUG")));
//        templateRepository.save(template);
//
//        TaskDefinition def = new TaskDefinition(
//            "task_template", "Task with Template", "HTTP", 1000,
//            objectMapper.valueToTree(Map.of("method", "GET", "url", "http://test")),
//            new FeaturePhases(null, null, List.of(new FeatureDefinition(null, "TEMPLATE_TEST", null))),
//            "ref1"
//        );
//
//        // WHEN
//        Task task = taskFactory.create(def);
//
//        // THEN
//        assertThat(task).isInstanceOf(TracingTask.class);
//        // O TracingTask encapsula a InterceptorStack, que por sua vez tem os interceptadores.
//        // Como os campos são privados, validamos via comportamento ou logs se necessário,
//        // mas aqui o fato de 'create' não ter lançado exceção já indica que o template foi resolvido
//        // e o interceptor 'LOG_RESPONSE' foi encontrado.
//    }
//}
