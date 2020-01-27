// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.expression.EvaluationException;
import java.math.RoundingMode;
import org.springframework.util.NumberUtils;
import java.math.BigDecimal;
import org.springframework.expression.Operation;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;

public class OpDivide extends Operator
{
    public OpDivide(final int pos, final SpelNodeImpl... operands) {
        super("/", pos, operands);
    }
    
    @Override
    public TypedValue getValueInternal(final ExpressionState state) throws EvaluationException {
        final Object leftOperand = this.getLeftOperand().getValueInternal(state).getValue();
        final Object rightOperand = this.getRightOperand().getValueInternal(state).getValue();
        if (!(leftOperand instanceof Number) || !(rightOperand instanceof Number)) {
            return state.operate(Operation.DIVIDE, leftOperand, rightOperand);
        }
        final Number leftNumber = (Number)leftOperand;
        final Number rightNumber = (Number)rightOperand;
        if (leftNumber instanceof BigDecimal || rightNumber instanceof BigDecimal) {
            final BigDecimal leftBigDecimal = NumberUtils.convertNumberToTargetClass(leftNumber, BigDecimal.class);
            final BigDecimal rightBigDecimal = NumberUtils.convertNumberToTargetClass(rightNumber, BigDecimal.class);
            final int scale = Math.max(leftBigDecimal.scale(), rightBigDecimal.scale());
            return new TypedValue(leftBigDecimal.divide(rightBigDecimal, scale, RoundingMode.HALF_EVEN));
        }
        if (leftNumber instanceof Double || rightNumber instanceof Double) {
            return new TypedValue(leftNumber.doubleValue() / rightNumber.doubleValue());
        }
        if (leftNumber instanceof Float || rightNumber instanceof Float) {
            return new TypedValue(leftNumber.floatValue() / rightNumber.floatValue());
        }
        if (leftNumber instanceof Long || rightNumber instanceof Long) {
            return new TypedValue(leftNumber.longValue() / rightNumber.longValue());
        }
        return new TypedValue(leftNumber.intValue() / rightNumber.intValue());
    }
}
