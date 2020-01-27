// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.expression.common.ExpressionUtils;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.TypedValue;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.util.Assert;
import org.springframework.expression.spel.SpelNode;

public abstract class SpelNodeImpl implements SpelNode
{
    private static SpelNodeImpl[] NO_CHILDREN;
    protected int pos;
    protected SpelNodeImpl[] children;
    private SpelNodeImpl parent;
    
    public SpelNodeImpl(final int pos, final SpelNodeImpl... operands) {
        this.children = SpelNodeImpl.NO_CHILDREN;
        this.pos = pos;
        Assert.isTrue(pos != 0);
        if (operands != null && operands.length > 0) {
            this.children = operands;
            for (final SpelNodeImpl childnode : operands) {
                childnode.parent = this;
            }
        }
    }
    
    protected SpelNodeImpl getPreviousChild() {
        SpelNodeImpl result = null;
        if (this.parent != null) {
            for (final SpelNodeImpl child : this.parent.children) {
                if (this == child) {
                    break;
                }
                result = child;
            }
        }
        return result;
    }
    
    protected boolean nextChildIs(final Class<?>... clazzes) {
        if (this.parent != null) {
            final SpelNodeImpl[] peers = this.parent.children;
            int i = 0;
            final int max = peers.length;
            while (i < max) {
                if (peers[i] == this) {
                    if (i + 1 >= max) {
                        return false;
                    }
                    final Class<?> clazz = peers[i + 1].getClass();
                    for (final Class<?> desiredClazz : clazzes) {
                        if (clazz.equals(desiredClazz)) {
                            return true;
                        }
                    }
                    return false;
                }
                else {
                    ++i;
                }
            }
        }
        return false;
    }
    
    @Override
    public final Object getValue(final ExpressionState expressionState) throws EvaluationException {
        if (expressionState != null) {
            return this.getValueInternal(expressionState).getValue();
        }
        return this.getValue(new ExpressionState(new StandardEvaluationContext()));
    }
    
    @Override
    public final TypedValue getTypedValue(final ExpressionState expressionState) throws EvaluationException {
        if (expressionState != null) {
            return this.getValueInternal(expressionState);
        }
        return this.getTypedValue(new ExpressionState(new StandardEvaluationContext()));
    }
    
    @Override
    public boolean isWritable(final ExpressionState expressionState) throws EvaluationException {
        return false;
    }
    
    @Override
    public void setValue(final ExpressionState expressionState, final Object newValue) throws EvaluationException {
        throw new SpelEvaluationException(this.getStartPosition(), SpelMessage.SETVALUE_NOT_SUPPORTED, new Object[] { this.getClass() });
    }
    
    @Override
    public SpelNode getChild(final int index) {
        return this.children[index];
    }
    
    @Override
    public int getChildCount() {
        return this.children.length;
    }
    
    @Override
    public Class<?> getObjectClass(final Object obj) {
        if (obj == null) {
            return null;
        }
        return (Class<?>)((obj instanceof Class) ? ((Class)obj) : obj.getClass());
    }
    
    protected final <T> T getValue(final ExpressionState state, final Class<T> desiredReturnType) throws EvaluationException {
        return ExpressionUtils.convertTypedValue(state.getEvaluationContext(), this.getValueInternal(state), desiredReturnType);
    }
    
    @Override
    public int getStartPosition() {
        return this.pos >> 16;
    }
    
    @Override
    public int getEndPosition() {
        return this.pos & 0xFFFF;
    }
    
    protected ValueRef getValueRef(final ExpressionState state) throws EvaluationException {
        throw new SpelEvaluationException(this.pos, SpelMessage.NOT_ASSIGNABLE, new Object[] { this.toStringAST() });
    }
    
    public abstract TypedValue getValueInternal(final ExpressionState p0) throws EvaluationException;
    
    static {
        SpelNodeImpl.NO_CHILDREN = new SpelNodeImpl[0];
    }
}
