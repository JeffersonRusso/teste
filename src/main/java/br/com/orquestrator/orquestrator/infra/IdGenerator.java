package br.com.orquestrator.orquestrator.infra;

import org.springframework.stereotype.Component;
import java.util.concurrent.ThreadLocalRandom;

/**
 * IdGenerator: Gerador de IDs de ultra-performance.
 * Lock-free e amigável a Virtual Threads.
 */
@Component
public class IdGenerator {

    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    /**
     * Gera um ID rápido para rastreamento (Correlation ID, Node ID).
     * Otimizado para evitar a criação de objetos UUID e o lock do SecureRandom.
     */
    public String generateFastId() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        long msb = random.nextLong();
        long lsb = random.nextLong();
        
        char[] out = new char[32];
        formatHex(msb, out, 0);
        formatHex(lsb, out, 16);
        return new String(out);
    }

    private void formatHex(long val, char[] out, int offset) {
        for (int i = 15; i >= 0; i--) {
            out[offset + i] = HEX_CHARS[(int) (val & 0xF)];
            val >>>= 4;
        }
    }

    /**
     * Fallback para UUID v4 se necessário, mas evite no caminho crítico de 5k TPS.
     */
    public String generateV4() {
        return java.util.UUID.randomUUID().toString();
    }
}
