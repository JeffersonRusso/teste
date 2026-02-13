package br.com.orquestrator.orquestrator.core.context.tag;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;

import java.util.Set;

public interface TagProvider {
    
    Set<String> resolveTags(ExecutionContext context);
}
