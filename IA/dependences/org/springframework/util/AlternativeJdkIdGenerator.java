// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

import java.util.UUID;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class AlternativeJdkIdGenerator implements IdGenerator
{
    private final Random random;
    
    public AlternativeJdkIdGenerator() {
        final SecureRandom secureRandom = new SecureRandom();
        final byte[] seed = new byte[8];
        secureRandom.nextBytes(seed);
        this.random = new Random(new BigInteger(seed).longValue());
    }
    
    @Override
    public UUID generateId() {
        final byte[] randomBytes = new byte[16];
        this.random.nextBytes(randomBytes);
        long mostSigBits = 0L;
        for (int i = 0; i < 8; ++i) {
            mostSigBits = (mostSigBits << 8 | (long)(randomBytes[i] & 0xFF));
        }
        long leastSigBits = 0L;
        for (int j = 8; j < 16; ++j) {
            leastSigBits = (leastSigBits << 8 | (long)(randomBytes[j] & 0xFF));
        }
        return new UUID(mostSigBits, leastSigBits);
    }
}
