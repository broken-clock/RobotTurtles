// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.expression.TypedValue;

public class NullLiteral extends Literal
{
    public NullLiteral(final int pos) {
        super(null, pos);
    }
    
    @Override
    public TypedValue getLiteralValue() {
        return TypedValue.NULL;
    }
    
    @Override
    public String toString() {
        return "null";
    }
}
