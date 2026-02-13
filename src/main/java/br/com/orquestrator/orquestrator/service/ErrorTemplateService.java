package br.com.orquestrator.orquestrator.service;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.CustomErrorRepository;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.CustomErrorEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ErrorTemplateService {

    private final CustomErrorRepository errorRepository;

    @Cacheable("error_bank")
    public String getTemplate(String errorCode) {
        return errorRepository.findById(errorCode)
                .map(CustomErrorEntity::getMessageTemplate)
                .orElse("Erro não catalogado: " + errorCode);
    }
}
