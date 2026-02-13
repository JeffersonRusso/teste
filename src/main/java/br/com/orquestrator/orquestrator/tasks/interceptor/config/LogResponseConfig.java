package br.com.orquestrator.orquestrator.tasks.interceptor.config;

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
