// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;

public class Elvis extends SpelNodeImpl
{
    public Elvis(final int pos, final SpelNodeImpl... args) {
        super(pos, args);
    }
    
    @Override
    public TypedValue getValueInternal(final ExpressionState state) throws EvaluationException {
        final TypedValue value = this.children[0].getValueInternal(state);
        if (value.getValue() != null && (!(value.getValue() instanceof String) || ((String)value.getValue()).length() != 0)) {
            return value;
        }
        return this.children[1].getValueInternal(state);
    }
    
    @Override
    public String toStringAST() {
        return this.getChild(0).toStringAST() + " ?: " + this.getChild(1).toStringAST();
    }
}
