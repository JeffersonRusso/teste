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
		// Configura o Log4j2 para modo Assíncrono antes do Spring iniciar
		// Essencial para manter a performance de 1k TPS sem bloquear Virtual Threads
		System.setProperty("log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");

		try {
			SpringApplication.run(OrquestratorApplication.class, args);
		} catch (Throwable e) {
			e.printStackTrace();
			System.err.println("!!! ERRO CRÍTICO NO STARTUP: " + e.getMessage());
			System.exit(1);
		}
	}

}
