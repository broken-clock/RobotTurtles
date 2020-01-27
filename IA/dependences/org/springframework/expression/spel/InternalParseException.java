// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel;

public class InternalParseException extends RuntimeException
{
    public InternalParseException(final SpelParseException cause) {
        super(cause);
    }
    
    @Override
    public SpelParseException getCause() {
        return (SpelParseException)super.getCause();
    }
}
