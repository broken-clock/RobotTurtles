// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.spel.support.BooleanTypedValue;
import org.springframework.expression.spel.ExpressionState;

public class OpOr extends Operator
{
    public OpOr(final int pos, final SpelNodeImpl... operands) {
        super("or", pos, operands);
    }
    
    @Override
    public BooleanTypedValue getValueInternal(final ExpressionState state) throws EvaluationException {
        if (this.getBooleanValue(state, this.getLeftOperand())) {
            return BooleanTypedValue.TRUE;
        }
        return BooleanTypedValue.forValue(this.getBooleanValue(state, this.getRightOperand()));
    }
    
    private boolean getBooleanValue(final ExpressionState state, final SpelNodeImpl operand) {
        try {
            final Boolean value = operand.getValue(state, Boolean.class);
            this.assertValueNotNull(value);
            return value;
        }
        catch (SpelEvaluationException ee) {
            ee.setPosition(operand.getStartPosition());
            throw ee;
        }
    }
    
    private void assertValueNotNull(final Boolean value) {
        if (value == null) {
            throw new SpelEvaluationException(SpelMessage.TYPE_CONVERSION_ERROR, new Object[] { "null", "boolean" });
        }
    }
}
