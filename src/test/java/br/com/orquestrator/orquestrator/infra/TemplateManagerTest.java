//package br.com.orquestrator.orquestrator.infra;
//
//import br.com.orquestrator.orquestrator.infra.el.PlaceholderStrategy;
//import br.com.orquestrator.orquestrator.infra.el.TemplateManager;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class TemplateManagerTest {
//
//    private TemplateManager templateManager;
//    private PlaceholderStrategy mockStrategy;
//
//    @BeforeEach
//    void setUp() {
//        mockStrategy = mock(PlaceholderStrategy.class);
//        when(mockStrategy.canResolve("prop")).thenReturn(true);
//
//        templateManager = new TemplateManager(List.of(mockStrategy));
//    }
//
//    @Test
//    void shouldResolveSimplePlaceholder() {
//        when(mockStrategy.resolve("api.url")).thenReturn("http://api.com");
//
//        String result = templateManager.resolve("URL: {{prop:api.url}}");
//
//        assertEquals("URL: http://api.com", result);
//        verify(mockStrategy, times(1)).resolve("api.url");
//    }
//
//    @Test
//    void shouldResolveWithDefaultValue() {
//        // Strategy retorna null, simulando propriedade não encontrada
//        when(mockStrategy.resolve("missing.prop")).thenReturn(null);
//
//        String result = templateManager.resolve("Value: {{prop:missing.prop|default_val}}");
//
//        assertEquals("Value: default_val", result);
//    }
//
//    @Test
//    void shouldUseCacheForSubsequentCalls() {
//        when(mockStrategy.resolve("cached.prop")).thenReturn("value1");
//
//        String firstCall = templateManager.resolve("{{prop:cached.prop}}");
//        String secondCall = templateManager.resolve("{{prop:cached.prop}}");
//
//        assertEquals("value1", firstCall);
//        assertEquals("value1", secondCall);
//
//        // Deve chamar a estratégia apenas UMA vez por causa do resolutionCache
//        verify(mockStrategy, times(1)).resolve("cached.prop");
//    }
//
//    @Test
//    void shouldHandleMultiplePlaceholders() {
//        when(mockStrategy.resolve("host")).thenReturn("localhost");
//        when(mockStrategy.resolve("port")).thenReturn("8080");
//
//        String result = templateManager.resolve("http://{{prop:host}}:{{prop:port}}/api");
//
//        assertEquals("http://localhost:8080/api", result);
//    }
//
//    @Test
//    void shouldThrowExceptionWhenNoStrategyFoundAndNoDefault() {
//        assertThrows(IllegalArgumentException.class, () -> {
//            templateManager.resolve("{{unknown:key}}");
//        });
//    }
//
//    @Test
//    void shouldHandleComplexTemplateWithMixedContent() {
//        when(mockStrategy.resolve("user")).thenReturn("admin");
//
//        String input = "User {{prop:user}} access to {{prop:resource|dashboard}}";
//        String result = templateManager.resolve(input);
//
//        assertEquals("User admin access to dashboard", result);
//    }
//}
