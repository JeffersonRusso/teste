package br.com.orquestrator.orquestrator.infra;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * LegacyBridge: Marca componentes que pertencem à ponte de migração legada.
 * Útil para identificar dívida técnica e planejar o desligamento do legado.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LegacyBridge {
    String description() default "";
    String targetMigrationDate() default "";
}
