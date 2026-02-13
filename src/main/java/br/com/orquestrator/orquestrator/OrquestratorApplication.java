package br.com.orquestrator.orquestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.client.HttpClientAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {
    HttpClientAutoConfiguration.class,
    RestClientAutoConfiguration.class
})
@EnableCaching
@EnableAsync
@EnableScheduling
public class OrquestratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrquestratorApplication.class, args);
	}

}
