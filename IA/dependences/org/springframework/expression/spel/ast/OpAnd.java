// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.spel.support.BooleanTypedValue;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;

public class OpAnd extends Operator
{
    public OpAnd(final int pos, final SpelNodeImpl... operands) {
        super("and", pos, operands);
    }
    
    @Override
    public TypedValue getValueInternal(final ExpressionState state) throws EvaluationException {
        if (!this.getBooleanValue(state, this.getLeftOperand())) {
            return BooleanTypedValue.FALSE;
        }
        return BooleanTypedValue.forValue(this.getBooleanValue(state, this.getRightOperand()));
    }
    
    private boolean getBooleanValue(final ExpressionState state, final SpelNodeImpl operand) {
        try {
            final Boolean value = operand.getValue(state, Boolean.class);
            this.assertValueNotNull(value);
            return value;
        }
        catch (SpelEvaluationException ex) {
            ex.setPosition(operand.getStartPosition());
            throw ex;
        }
    }
    
    private void assertValueNotNull(final Boolean value) {
        if (value == null) {
            throw new SpelEvaluationException(SpelMessage.TYPE_CONVERSION_ERROR, new Object[] { "null", "boolean" });
        }
    }
}
