// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.expression.TypedValue;

public class IntLiteral extends Literal
{
    private final TypedValue value;
    
    IntLiteral(final String payload, final int pos, final int value) {
        super(payload, pos);
        this.value = new TypedValue(value);
    }
    
    @Override
    public TypedValue getLiteralValue() {
        return this.value;
    }
}
