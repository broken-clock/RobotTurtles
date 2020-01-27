// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.expression.TypedValue;

public class FloatLiteral extends Literal
{
    private final TypedValue value;
    
    FloatLiteral(final String payload, final int pos, final float value) {
        super(payload, pos);
        this.value = new TypedValue(value);
    }
    
    @Override
    public TypedValue getLiteralValue() {
        return this.value;
    }
}
