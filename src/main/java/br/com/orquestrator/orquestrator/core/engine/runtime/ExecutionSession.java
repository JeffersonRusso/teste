package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.context.*;
import br.com.orquestrator.orquestrator.core.context.identity.RequestIdentity;
import br.com.orquestrator.orquestrator.core.context.tag.TagManager;
import br.com.orquestrator.orquestrator.core.engine.binding.NormalizationCompiler;
import br.com.orquestrator.orquestrator.core.engine.binding.ResultExtractor;
import br.com.orquestrator.orquestrator.core.pipeline.PipelineService;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.infra.Flow;
import br.com.orquestrator.orquestrator.infra.el.SpelContextFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExecutionSession {

    private final ContextFactory contextFactory;
    private final TagManager tagManager;
    private final PipelineService pipelineService;
    private final ReactiveExecutionEngine engine;
    private final NormalizationCompiler normalizationCompiler;
    private final ResultExtractor resultExtractor;
    private final SpelContextFactory spelContextFactory;

    public Map<String, Object> run(RequestIdentity identity, Map<String, String> headers, Map<String, Object> body) {
        ExecutionContext context = contextFactory.create(identity, headers, body);
        EvaluationContext evalContext = spelContextFactory.create(context);

        try {
            return ScopedValue.where(ContextHolder.CONTEXT, context)
                    .where(ContextHolder.EVAL_CONTEXT, evalContext)
                    .call(() -> executeFlow(context));
        } catch (Exception e) {
            throw (e instanceof RuntimeException re) ? re : new RuntimeException(e);
        }
    }

    private Map<String, Object> executeFlow(ExecutionContext context) {
        // O código agora é puramente um DAG de bolinhas (Flow Step Pattern)
        return Flow.start(context)
                .next(this::resolveScenario)
                .next(this::normalizeInput)
                .next(this::runEngine)
                .finish(this::extractResult);
    }

    private Pipeline resolveScenario(ExecutionContext ctx) {
        tagManager.resolveAndApply(ctx.reader(), ctx.writer());
        return pipelineService.create(ctx.metadata());
    }

    private Pipeline normalizeInput(Pipeline pipeline) {
        normalizationCompiler.execute(pipeline.normalizationPlan(), ContextHolder.writer());
        return pipeline;
    }

    private Pipeline runEngine(Pipeline pipeline) {
        engine.execute(pipeline);
        return pipeline;
    }

    private Map<String, Object> extractResult(Pipeline pipeline) {
        return resultExtractor.extract(ContextHolder.reader(), pipeline);
    }
}
