package br.com.orquestrator.orquestrator.repository;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.TaskCatalogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class TaskCatalogEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskCatalogRepository repository;

    @Test
    void shouldLoadTaskCatalogWithFeatures() {
        // This test will fail if the deserialization issue persists
        var tasks = repository.findAllActive();
        assertThat(tasks).isNotEmpty();
        for (var task : tasks) {
            assertThat(task.getFeatures()).isNotNull();
        }
    }
}
