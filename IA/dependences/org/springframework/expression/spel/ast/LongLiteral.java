// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.expression.TypedValue;

public class LongLiteral extends Literal
{
    private final TypedValue value;
    
    LongLiteral(final String payload, final int pos, final long value) {
        super(payload, pos);
        this.value = new TypedValue(value);
    }
    
    @Override
    public TypedValue getLiteralValue() {
        return this.value;
    }
}
