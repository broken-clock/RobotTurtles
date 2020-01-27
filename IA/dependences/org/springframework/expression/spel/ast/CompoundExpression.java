// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.ExpressionState;

public class CompoundExpression extends SpelNodeImpl
{
    public CompoundExpression(final int pos, final SpelNodeImpl... expressionComponents) {
        super(pos, expressionComponents);
        if (expressionComponents.length < 2) {
            throw new IllegalStateException("Do not build compound expression less than one entry: " + expressionComponents.length);
        }
    }
    
    @Override
    protected ValueRef getValueRef(final ExpressionState state) throws EvaluationException {
        if (this.getChildCount() == 1) {
            return this.children[0].getValueRef(state);
        }
        SpelNodeImpl nextNode = this.children[0];
        try {
            TypedValue result = nextNode.getValueInternal(state);
            final int cc = this.getChildCount();
            for (int i = 1; i < cc - 1; ++i) {
                try {
                    state.pushActiveContextObject(result);
                    nextNode = this.children[i];
                    result = nextNode.getValueInternal(state);
                }
                finally {
                    state.popActiveContextObject();
                }
            }
            try {
                state.pushActiveContextObject(result);
                nextNode = this.children[cc - 1];
                return nextNode.getValueRef(state);
            }
            finally {
                state.popActiveContextObject();
            }
        }
        catch (SpelEvaluationException ee) {
            ee.setPosition(nextNode.getStartPosition());
            throw ee;
        }
    }
    
    @Override
    public TypedValue getValueInternal(final ExpressionState state) throws EvaluationException {
        return this.getValueRef(state).getValue();
    }
    
    @Override
    public void setValue(final ExpressionState state, final Object value) throws EvaluationException {
        this.getValueRef(state).setValue(value);
    }
    
    @Override
    public boolean isWritable(final ExpressionState state) throws EvaluationException {
        return this.getValueRef(state).isWritable();
    }
    
    @Override
    public String toStringAST() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.getChildCount(); ++i) {
            if (i > 0) {
                sb.append(".");
            }
            sb.append(this.getChild(i).toStringAST());
        }
        return sb.toString();
    }
}
