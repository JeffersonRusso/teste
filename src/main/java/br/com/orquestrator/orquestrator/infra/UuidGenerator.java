package br.com.orquestrator.orquestrator.infra;

import org.springframework.stereotype.Component;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.nio.ByteBuffer;

/**
 * UuidGenerator: Implementação de alta performance inspirada no UUID v7.
 * Otimizado para 100k TPS: Lock-free e Time-ordered.
 */
@Component
public class UuidGenerator implements IdGenerator {

    @Override
    public String generate() {
        // UUID v7 customizado: [48 bits timestamp] [4 bits version] [12 bits random] [2 bits variant] [62 bits random]
        long timestamp = System.currentTimeMillis();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        
        long msb = (timestamp << 16) | (0x7L << 12) | (random.nextLong() & 0xFFFL);
        long lsb = (0x2L << 62) | (random.nextLong() & 0x3FFFFFFFFFFFFFFFL);
        
        return new UUID(msb, lsb).toString();
    }

    @Override
    public String generateFastId() {
        // Versão ultra-rápida sem traços e com entropia local
        long timestamp = System.currentTimeMillis();
        long random = ThreadLocalRandom.current().nextLong();
        
        return Long.toHexString(timestamp) + Long.toHexString(random);
    }
}
