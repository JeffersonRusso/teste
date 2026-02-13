package br.com.orquestrator.orquestrator.infra;

import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class IdGenerator {

    /**
     * Gera um UUID v4 padrão (SecureRandom).
     * Use para chaves de negócio que exigem unicidade global forte.
     */
    public String generateV4() {
        return UUID.randomUUID().toString();
    }

    /**
     * Gera um ID rápido para rastreamento (Correlation ID, Node ID).
     * Otimizado para evitar alocação excessiva de objetos UUID.
     */
    public String generateFastId() {
        // Gera uma string hexadecimal direta, evitando a criação de objeto UUID
        // 16 bytes (128 bits) representados em hex
        long msb = ThreadLocalRandom.current().nextLong();
        long lsb = ThreadLocalRandom.current().nextLong();
        return Long.toHexString(msb) + Long.toHexString(lsb);
    }
}
