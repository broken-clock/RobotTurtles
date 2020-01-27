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

public class OpMinus extends Operator
{
    public OpMinus(final int pos, final SpelNodeImpl... operands) {
        super("-", pos, operands);
    }
    
    @Override
    public TypedValue getValueInternal(final ExpressionState state) throws EvaluationException {
        final SpelNodeImpl leftOp = this.getLeftOperand();
        final SpelNodeImpl rightOp = this.getRightOperand();
        if (rightOp == null) {
            final Object operand = leftOp.getValueInternal(state).getValue();
            if (!(operand instanceof Number)) {
                return state.operate(Operation.SUBTRACT, operand, null);
            }
            final Number n = (Number)operand;
            if (operand instanceof BigDecimal) {
                final BigDecimal bdn = (BigDecimal)n;
                return new TypedValue(bdn.negate());
            }
            if (operand instanceof Double) {
                return new TypedValue(0.0 - n.doubleValue());
            }
            if (operand instanceof Float) {
                return new TypedValue(0.0f - n.floatValue());
            }
            if (operand instanceof Long) {
                return new TypedValue(0L - n.longValue());
            }
            return new TypedValue(0 - n.intValue());
        }
        else {
            final Object left = leftOp.getValueInternal(state).getValue();
            final Object right = rightOp.getValueInternal(state).getValue();
            if (left instanceof Number && right instanceof Number) {
                final Number leftNumber = (Number)left;
                final Number rightNumber = (Number)right;
                if (leftNumber instanceof BigDecimal || rightNumber instanceof BigDecimal) {
                    final BigDecimal leftBigDecimal = NumberUtils.convertNumberToTargetClass(leftNumber, BigDecimal.class);
                    final BigDecimal rightBigDecimal = NumberUtils.convertNumberToTargetClass(rightNumber, BigDecimal.class);
                    return new TypedValue(leftBigDecimal.subtract(rightBigDecimal));
                }
                if (leftNumber instanceof Double || rightNumber instanceof Double) {
                    return new TypedValue(leftNumber.doubleValue() - rightNumber.doubleValue());
                }
                if (leftNumber instanceof Float || rightNumber instanceof Float) {
                    return new TypedValue(leftNumber.floatValue() - rightNumber.floatValue());
                }
                if (leftNumber instanceof Long || rightNumber instanceof Long) {
                    return new TypedValue(leftNumber.longValue() - rightNumber.longValue());
                }
                return new TypedValue(leftNumber.intValue() - rightNumber.intValue());
            }
            else {
                if (left instanceof String && right instanceof Integer && ((String)left).length() == 1) {
                    final String theString = (String)left;
                    final Integer theInteger = (Integer)right;
                    return new TypedValue(Character.toString((char)(theString.charAt(0) - theInteger)));
                }
                return state.operate(Operation.SUBTRACT, left, right);
            }
        }
    }
    
    @Override
    public String toStringAST() {
        if (this.getRightOperand() == null) {
            return "-" + this.getLeftOperand().toStringAST();
        }
        return super.toStringAST();
    }
    
    @Override
    public SpelNodeImpl getRightOperand() {
        if (this.children.length < 2) {
            return null;
        }
        return this.children[1];
    }
}
