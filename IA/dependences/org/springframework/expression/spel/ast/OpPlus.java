// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.expression.TypeConverter;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationException;
import org.springframework.util.NumberUtils;
import java.math.BigDecimal;
import org.springframework.expression.Operation;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.util.Assert;

public class OpPlus extends Operator
{
    public OpPlus(final int pos, final SpelNodeImpl... operands) {
        super("+", pos, operands);
        Assert.notEmpty(operands);
    }
    
    @Override
    public TypedValue getValueInternal(final ExpressionState state) throws EvaluationException {
        final SpelNodeImpl leftOp = this.getLeftOperand();
        final SpelNodeImpl rightOp = this.getRightOperand();
        if (rightOp == null) {
            final Object operandOne = leftOp.getValueInternal(state).getValue();
            if (!(operandOne instanceof Number)) {
                return state.operate(Operation.ADD, operandOne, null);
            }
            if (operandOne instanceof Double || operandOne instanceof Long || operandOne instanceof BigDecimal) {
                return new TypedValue(operandOne);
            }
            if (operandOne instanceof Float) {
                return new TypedValue(((Number)operandOne).floatValue());
            }
            return new TypedValue(((Number)operandOne).intValue());
        }
        else {
            final TypedValue operandOneValue = leftOp.getValueInternal(state);
            final Object leftOperand = operandOneValue.getValue();
            final TypedValue operandTwoValue = rightOp.getValueInternal(state);
            final Object rightOperand = operandTwoValue.getValue();
            if (leftOperand instanceof Number && rightOperand instanceof Number) {
                final Number leftNumber = (Number)leftOperand;
                final Number rightNumber = (Number)rightOperand;
                if (leftNumber instanceof BigDecimal || rightNumber instanceof BigDecimal) {
                    final BigDecimal leftBigDecimal = NumberUtils.convertNumberToTargetClass(leftNumber, BigDecimal.class);
                    final BigDecimal rightBigDecimal = NumberUtils.convertNumberToTargetClass(rightNumber, BigDecimal.class);
                    return new TypedValue(leftBigDecimal.add(rightBigDecimal));
                }
                if (leftNumber instanceof Double || rightNumber instanceof Double) {
                    return new TypedValue(leftNumber.doubleValue() + rightNumber.doubleValue());
                }
                if (leftNumber instanceof Float || rightNumber instanceof Float) {
                    return new TypedValue(leftNumber.floatValue() + rightNumber.floatValue());
                }
                if (leftNumber instanceof Long || rightNumber instanceof Long) {
                    return new TypedValue(leftNumber.longValue() + rightNumber.longValue());
                }
                return new TypedValue(leftNumber.intValue() + rightNumber.intValue());
            }
            else {
                if (leftOperand instanceof String && rightOperand instanceof String) {
                    return new TypedValue((String)leftOperand + (String)rightOperand);
                }
                if (leftOperand instanceof String) {
                    final StringBuilder result = new StringBuilder((String)leftOperand);
                    result.append((rightOperand == null) ? "null" : convertTypedValueToString(operandTwoValue, state));
                    return new TypedValue(result.toString());
                }
                if (rightOperand instanceof String) {
                    final StringBuilder result = new StringBuilder((leftOperand == null) ? "null" : convertTypedValueToString(operandOneValue, state));
                    result.append((String)rightOperand);
                    return new TypedValue(result.toString());
                }
                return state.operate(Operation.ADD, leftOperand, rightOperand);
            }
        }
    }
    
    @Override
    public String toStringAST() {
        if (this.children.length < 2) {
            return "+" + this.getLeftOperand().toStringAST();
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
    
    private static String convertTypedValueToString(final TypedValue value, final ExpressionState state) {
        final TypeConverter typeConverter = state.getEvaluationContext().getTypeConverter();
        final TypeDescriptor typeDescriptor = TypeDescriptor.valueOf(String.class);
        if (typeConverter.canConvert(value.getTypeDescriptor(), typeDescriptor)) {
            final Object obj = typeConverter.convertValue(value.getValue(), value.getTypeDescriptor(), typeDescriptor);
            return String.valueOf(obj);
        }
        return String.valueOf(value.getValue());
    }
}
