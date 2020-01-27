// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.support.BooleanTypedValue;

public class BooleanLiteral extends Literal
{
    private final BooleanTypedValue value;
    
    public BooleanLiteral(final String payload, final int pos, final boolean value) {
        super(payload, pos);
        this.value = BooleanTypedValue.forValue(value);
    }
    
    @Override
    public BooleanTypedValue getLiteralValue() {
        return this.value;
    }
}
