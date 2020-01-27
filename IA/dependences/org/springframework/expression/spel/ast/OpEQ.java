// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.expression.TypedValue;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.spel.support.BooleanTypedValue;
import org.springframework.expression.spel.ExpressionState;

public class OpEQ extends Operator
{
    public OpEQ(final int pos, final SpelNodeImpl... operands) {
        super("==", pos, operands);
    }
    
    @Override
    public BooleanTypedValue getValueInternal(final ExpressionState state) throws EvaluationException {
        final Object left = this.getLeftOperand().getValueInternal(state).getValue();
        final Object right = this.getRightOperand().getValueInternal(state).getValue();
        return BooleanTypedValue.forValue(this.equalityCheck(state, left, right));
    }
}
