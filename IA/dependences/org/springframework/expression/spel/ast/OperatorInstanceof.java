// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.support.BooleanTypedValue;
import org.springframework.expression.spel.ExpressionState;

public class OperatorInstanceof extends Operator
{
    public OperatorInstanceof(final int pos, final SpelNodeImpl... operands) {
        super("instanceof", pos, operands);
    }
    
    @Override
    public BooleanTypedValue getValueInternal(final ExpressionState state) throws EvaluationException {
        final TypedValue left = this.getLeftOperand().getValueInternal(state);
        final TypedValue right = this.getRightOperand().getValueInternal(state);
        final Object leftValue = left.getValue();
        final Object rightValue = right.getValue();
        if (leftValue == null) {
            return BooleanTypedValue.FALSE;
        }
        if (rightValue == null || !(rightValue instanceof Class)) {
            throw new SpelEvaluationException(this.getRightOperand().getStartPosition(), SpelMessage.INSTANCEOF_OPERATOR_NEEDS_CLASS_OPERAND, new Object[] { (rightValue == null) ? "null" : rightValue.getClass().getName() });
        }
        final Class<?> rightClass = (Class<?>)rightValue;
        return BooleanTypedValue.forValue(rightClass.isAssignableFrom(leftValue.getClass()));
    }
}
