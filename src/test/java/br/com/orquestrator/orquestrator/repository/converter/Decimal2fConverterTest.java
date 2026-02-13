package br.com.orquestrator.orquestrator.repository.converter;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.converter.Decimal2fConverter;
import org.decimal4j.immutable.Decimal2f;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class Decimal2fConverterTest {

    private final Decimal2fConverter converter = new Decimal2fConverter();

    @Test
    void shouldConvertToDatabaseColumn() {
        Decimal2f value = Decimal2f.valueOf(10.55);
        BigDecimal result = converter.convertToDatabaseColumn(value);
        
        assertNotNull(result);
        assertEquals(0, new BigDecimal("10.55").compareTo(result));
    }

    @Test
    void shouldReturnNullWhenConvertingNullToDatabase() {
        assertNull(converter.convertToDatabaseColumn(null));
    }

    @Test
    void shouldConvertToEntityAttribute() {
        BigDecimal value = new BigDecimal("25.99");
        Decimal2f result = converter.convertToEntityAttribute(value);
        
        assertNotNull(result);
        assertEquals(25.99, result.doubleValue());
    }

    @Test
    void shouldReturnNullWhenConvertingNullToEntity() {
        assertNull(converter.convertToEntityAttribute(null));
    }
}
