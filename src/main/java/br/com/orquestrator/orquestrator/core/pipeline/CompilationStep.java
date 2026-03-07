package br.com.orquestrator.orquestrator.core.pipeline;

/**
 * CompilationStep: Uma etapa no DAG de compilação do pipeline.
 */
public interface CompilationStep {
    
    /** Executa a transformação e retorna a sessão para a próxima bolinha. */
    CompilationSession execute(CompilationSession session);

    /** Define a ordem de execução no grafo linear. */
    int getOrder();
}
