package br.com.orquestrator.orquestrator.core.pipeline;

/**
 * CompilationStep: Uma etapa atômica no processo de compilação do pipeline.
 */
public interface CompilationStep {
    void execute(CompilationSession session);
    
    /** Define a ordem de execução do passo. */
    int getOrder();
}
