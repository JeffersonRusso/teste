package br.com.orquestrator.orquestrator.infra.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.appconfigdata.AppConfigDataClient;
import software.amazon.awssdk.services.appconfigdata.model.GetLatestConfigurationRequest;
import software.amazon.awssdk.services.appconfigdata.model.GetLatestConfigurationResponse;
import software.amazon.awssdk.services.appconfigdata.model.StartConfigurationSessionRequest;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppConfigService {

    private final ObjectMapper objectMapper;

    @Value("${aws.appconfig.application:orquestrator-app}")
    private String application;

    @Value("${aws.appconfig.environment:production}")
    private String environment;

    @Value("${aws.appconfig.profile:flow-routing}")
    private String profile;
    
    @Value("${aws.endpoint:http://localhost:4566}")
    private String endpoint;
    
    @Value("${aws.region:us-east-1}")
    private String region;

    private AppConfigDataClient client;
    private String initialConfigurationToken;
    
    private final AtomicReference<JsonNode> configCache = new AtomicReference<>();

    // @PostConstruct
    public void init() {
        try {
            this.client = AppConfigDataClient.builder()
                    .endpointOverride(URI.create(endpoint))
                    .region(Region.of(region)) // Define região explícita para evitar lookup de sistema
                    .build();

            startSession();
            fetchConfiguration();
        } catch (Exception e) {
            log.error("Falha ao inicializar AppConfig. Usando defaults.", e);
        }
    }

    private void startSession() {
        var response = client.startConfigurationSession(StartConfigurationSessionRequest.builder()
                .applicationIdentifier(application)
                .environmentIdentifier(environment)
                .configurationProfileIdentifier(profile)
                .requiredMinimumPollIntervalInSeconds(15)
                .build());
        
        this.initialConfigurationToken = response.initialConfigurationToken();
    }

    // @Scheduled(fixedRate = 60000)
    public void fetchConfiguration() {
        if (initialConfigurationToken == null) return;

        try {
            GetLatestConfigurationResponse response = client.getLatestConfiguration(GetLatestConfigurationRequest.builder()
                    .configurationToken(initialConfigurationToken)
                    .build());

            this.initialConfigurationToken = response.nextPollConfigurationToken();

            if (response.configuration() != null && response.configuration().asByteArray().length > 0) {
                String json = response.configuration().asString(StandardCharsets.UTF_8);
                JsonNode node = objectMapper.readTree(json);
                configCache.set(node);
                log.info("Configuração do AppConfig atualizada: {}", json);
            }
        } catch (Exception e) {
            log.error("Erro ao buscar configuração do AppConfig", e);
        }
    }

    public JsonNode getConfig(String key) {
        JsonNode root = configCache.get();
        return root != null ? root.get(key) : null;
    }
}
