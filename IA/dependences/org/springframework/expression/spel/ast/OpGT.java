// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.expression.TypedValue;
import org.springframework.expression.EvaluationException;
import org.springframework.util.NumberUtils;
import java.math.BigDecimal;
import org.springframework.expression.spel.support.BooleanTypedValue;
import org.springframework.expression.spel.ExpressionState;

public class OpGT extends Operator
{
    public OpGT(final int pos, final SpelNodeImpl... operands) {
        super(">", pos, operands);
    }
    
    @Override
    public BooleanTypedValue getValueInternal(final ExpressionState state) throws EvaluationException {
        final Object left = this.getLeftOperand().getValueInternal(state).getValue();
        final Object right = this.getRightOperand().getValueInternal(state).getValue();
        if (!(left instanceof Number) || !(right instanceof Number)) {
            return BooleanTypedValue.forValue(state.getTypeComparator().compare(left, right) > 0);
        }
        final Number leftNumber = (Number)left;
        final Number rightNumber = (Number)right;
        if (leftNumber instanceof BigDecimal || rightNumber instanceof BigDecimal) {
            final BigDecimal leftBigDecimal = NumberUtils.convertNumberToTargetClass(leftNumber, BigDecimal.class);
            final BigDecimal rightBigDecimal = NumberUtils.convertNumberToTargetClass(rightNumber, BigDecimal.class);
            return BooleanTypedValue.forValue(leftBigDecimal.compareTo(rightBigDecimal) > 0);
        }
        if (leftNumber instanceof Double || rightNumber instanceof Double) {
            return BooleanTypedValue.forValue(leftNumber.doubleValue() > rightNumber.doubleValue());
        }
        if (leftNumber instanceof Float || rightNumber instanceof Float) {
            return BooleanTypedValue.forValue(leftNumber.floatValue() > rightNumber.floatValue());
        }
        if (leftNumber instanceof Long || rightNumber instanceof Long) {
            return BooleanTypedValue.forValue(leftNumber.longValue() > rightNumber.longValue());
        }
        return BooleanTypedValue.forValue(leftNumber.intValue() > rightNumber.intValue());
    }
}
