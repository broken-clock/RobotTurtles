// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;

public class Assign extends SpelNodeImpl
{
    public Assign(final int pos, final SpelNodeImpl... operands) {
        super(pos, operands);
    }
    
    @Override
    public TypedValue getValueInternal(final ExpressionState state) throws EvaluationException {
        final TypedValue newValue = this.children[1].getValueInternal(state);
        this.getChild(0).setValue(state, newValue.getValue());
        return newValue;
    }
    
    @Override
    public String toStringAST() {
        return this.getChild(0).toStringAST() + "=" + this.getChild(1).toStringAST();
    }
}
