// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.TypedValue;

public class Identifier extends SpelNodeImpl
{
    private final TypedValue id;
    
    public Identifier(final String payload, final int pos) {
        super(pos, new SpelNodeImpl[0]);
        this.id = new TypedValue(payload);
    }
    
    @Override
    public String toStringAST() {
        return (String)this.id.getValue();
    }
    
    @Override
    public TypedValue getValueInternal(final ExpressionState state) {
        return this.id;
    }
}
