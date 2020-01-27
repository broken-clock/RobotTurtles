// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.Operation;
import java.math.BigDecimal;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.util.Assert;

public class OpInc extends Operator
{
    private final boolean postfix;
    
    public OpInc(final int pos, final boolean postfix, final SpelNodeImpl... operands) {
        super("++", pos, operands);
        Assert.notEmpty(operands);
        this.postfix = postfix;
    }
    
    @Override
    public TypedValue getValueInternal(final ExpressionState state) throws EvaluationException {
        final SpelNodeImpl operand = this.getLeftOperand();
        final ValueRef valueRef = operand.getValueRef(state);
        final TypedValue typedValue = valueRef.getValue();
        final Object value = typedValue.getValue();
        TypedValue returnValue = typedValue;
        TypedValue newValue = null;
        if (value instanceof Number) {
            final Number op1 = (Number)value;
            if (op1 instanceof BigDecimal) {
                newValue = new TypedValue(((BigDecimal)op1).add(BigDecimal.ONE), typedValue.getTypeDescriptor());
            }
            else if (op1 instanceof Double) {
                newValue = new TypedValue(op1.doubleValue() + 1.0, typedValue.getTypeDescriptor());
            }
            else if (op1 instanceof Float) {
                newValue = new TypedValue(op1.floatValue() + 1.0f, typedValue.getTypeDescriptor());
            }
            else if (op1 instanceof Long) {
                newValue = new TypedValue(op1.longValue() + 1L, typedValue.getTypeDescriptor());
            }
            else if (op1 instanceof Short) {
                newValue = new TypedValue(op1.shortValue() + 1, typedValue.getTypeDescriptor());
            }
            else {
                newValue = new TypedValue(op1.intValue() + 1, typedValue.getTypeDescriptor());
            }
        }
        if (newValue == null) {
            try {
                newValue = state.operate(Operation.ADD, returnValue.getValue(), 1);
            }
            catch (SpelEvaluationException ex) {
                if (ex.getMessageCode() == SpelMessage.OPERATOR_NOT_SUPPORTED_BETWEEN_TYPES) {
                    throw new SpelEvaluationException(operand.getStartPosition(), SpelMessage.OPERAND_NOT_INCREMENTABLE, new Object[] { operand.toStringAST() });
                }
                throw ex;
            }
        }
        try {
            valueRef.setValue(newValue.getValue());
        }
        catch (SpelEvaluationException see) {
            if (see.getMessageCode() == SpelMessage.SETVALUE_NOT_SUPPORTED) {
                throw new SpelEvaluationException(operand.getStartPosition(), SpelMessage.OPERAND_NOT_INCREMENTABLE, new Object[0]);
            }
            throw see;
        }
        if (!this.postfix) {
            returnValue = newValue;
        }
        return returnValue;
    }
    
    @Override
    public String toStringAST() {
        return this.getLeftOperand().toStringAST() + "++";
    }
    
    @Override
    public SpelNodeImpl getRightOperand() {
        return null;
    }
}
