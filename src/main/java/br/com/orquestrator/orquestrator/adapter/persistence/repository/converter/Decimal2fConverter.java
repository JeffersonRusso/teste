package br.com.orquestrator.orquestrator.adapter.persistence.repository.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.decimal4j.immutable.Decimal2f;

import java.math.BigDecimal;

@Converter(autoApply = true)
public class Decimal2fConverter implements AttributeConverter<Decimal2f, BigDecimal> {

    @Override
    public BigDecimal convertToDatabaseColumn(Decimal2f attribute) {
        return attribute == null ? null : attribute.toBigDecimal();
    }

    @Override
    public Decimal2f convertToEntityAttribute(BigDecimal dbData) {
        return dbData == null ? null : Decimal2f.valueOf(dbData);
    }
}
