package br.com.orquestrator.orquestrator.api.signal;

public enum CoreSignals implements Signal {
    RAW("raw"),
    ERROR("error"),
    PIPELINE_RESULT("pipeline_result");

    private final String value;

    CoreSignals(String value) {
        this.value = value;
    }

    @Override
    public String signalName() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
