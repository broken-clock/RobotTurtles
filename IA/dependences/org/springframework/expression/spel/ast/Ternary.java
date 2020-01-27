// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;

public class Ternary extends SpelNodeImpl
{
    public Ternary(final int pos, final SpelNodeImpl... args) {
        super(pos, args);
    }
    
    @Override
    public TypedValue getValueInternal(final ExpressionState state) throws EvaluationException {
        final Boolean value = this.children[0].getValue(state, Boolean.class);
        if (value == null) {
            throw new SpelEvaluationException(this.getChild(0).getStartPosition(), SpelMessage.TYPE_CONVERSION_ERROR, new Object[] { "null", "boolean" });
        }
        if (value) {
            return this.children[1].getValueInternal(state);
        }
        return this.children[2].getValueInternal(state);
    }
    
    @Override
    public String toStringAST() {
        return this.getChild(0).toStringAST() + " ? " + this.getChild(1).toStringAST() + " : " + this.getChild(2).toStringAST();
    }
}
