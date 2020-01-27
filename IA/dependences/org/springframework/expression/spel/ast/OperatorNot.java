// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.expression.TypedValue;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.support.BooleanTypedValue;
import org.springframework.expression.spel.ExpressionState;

public class OperatorNot extends SpelNodeImpl
{
    public OperatorNot(final int pos, final SpelNodeImpl operand) {
        super(pos, new SpelNodeImpl[] { operand });
    }
    
    @Override
    public BooleanTypedValue getValueInternal(final ExpressionState state) throws EvaluationException {
        try {
            final Boolean value = this.children[0].getValue(state, Boolean.class);
            if (value == null) {
                throw new SpelEvaluationException(SpelMessage.TYPE_CONVERSION_ERROR, new Object[] { "null", "boolean" });
            }
            return BooleanTypedValue.forValue(!value);
        }
        catch (SpelEvaluationException ex) {
            ex.setPosition(this.getChild(0).getStartPosition());
            throw ex;
        }
    }
    
    @Override
    public String toStringAST() {
        final StringBuilder sb = new StringBuilder();
        sb.append("!").append(this.getChild(0).toStringAST());
        return sb.toString();
    }
}
