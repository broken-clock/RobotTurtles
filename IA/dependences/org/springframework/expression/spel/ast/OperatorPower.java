// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.expression.EvaluationException;
import org.springframework.util.NumberUtils;
import java.math.BigDecimal;
import org.springframework.expression.Operation;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;

public class OperatorPower extends Operator
{
    public OperatorPower(final int pos, final SpelNodeImpl... operands) {
        super("^", pos, operands);
    }
    
    @Override
    public TypedValue getValueInternal(final ExpressionState state) throws EvaluationException {
        final SpelNodeImpl leftOp = this.getLeftOperand();
        final SpelNodeImpl rightOp = this.getRightOperand();
        final Object leftOperand = leftOp.getValueInternal(state).getValue();
        final Object rightOperand = rightOp.getValueInternal(state).getValue();
        if (!(leftOperand instanceof Number) || !(rightOperand instanceof Number)) {
            return state.operate(Operation.POWER, leftOperand, rightOperand);
        }
        final Number leftNumber = (Number)leftOperand;
        final Number rightNumber = (Number)rightOperand;
        if (leftNumber instanceof BigDecimal) {
            final BigDecimal leftBigDecimal = NumberUtils.convertNumberToTargetClass(leftNumber, BigDecimal.class);
            return new TypedValue(leftBigDecimal.pow(rightNumber.intValue()));
        }
        if (leftNumber instanceof Double || rightNumber instanceof Double) {
            return new TypedValue(Math.pow(leftNumber.doubleValue(), rightNumber.doubleValue()));
        }
        if (leftNumber instanceof Float || rightNumber instanceof Float) {
            return new TypedValue(Math.pow(leftNumber.floatValue(), rightNumber.floatValue()));
        }
        if (leftNumber instanceof Long || rightNumber instanceof Long) {
            final double d = Math.pow((double)leftNumber.longValue(), (double)rightNumber.longValue());
            return new TypedValue((long)d);
        }
        final double d = Math.pow((double)leftNumber.longValue(), (double)rightNumber.longValue());
        if (d > 2.147483647E9) {
            return new TypedValue((long)d);
        }
        return new TypedValue((int)d);
    }
}
