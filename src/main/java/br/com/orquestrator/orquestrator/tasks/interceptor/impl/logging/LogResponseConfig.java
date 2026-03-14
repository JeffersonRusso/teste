package br.com.orquestrator.orquestrator.tasks.interceptor.impl.logging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LogResponseConfig(
    String level,
    Boolean showBody
) {
    public String getLevel() {
        return level != null ? level : "INFO";
    }

    public boolean isShowBody() {
        return showBody != null && showBody;
    }
}
