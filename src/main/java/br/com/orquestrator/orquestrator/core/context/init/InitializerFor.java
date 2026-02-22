package br.com.orquestrator.orquestrator.core.context.init;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(InitializerFor.List.class)
@Component
public @interface InitializerFor {
    String[] value();

    @Target(ElementType.TYPE) @Retention(RetentionPolicy.RUNTIME) @interface List { InitializerFor[] value(); }
}