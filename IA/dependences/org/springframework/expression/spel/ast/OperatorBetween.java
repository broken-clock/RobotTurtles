// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.expression.TypedValue;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypeComparator;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import java.util.List;
import org.springframework.expression.spel.support.BooleanTypedValue;
import org.springframework.expression.spel.ExpressionState;

public class OperatorBetween extends Operator
{
    public OperatorBetween(final int pos, final SpelNodeImpl... operands) {
        super("between", pos, operands);
    }
    
    @Override
    public BooleanTypedValue getValueInternal(final ExpressionState state) throws EvaluationException {
        final Object left = this.getLeftOperand().getValueInternal(state).getValue();
        final Object right = this.getRightOperand().getValueInternal(state).getValue();
        if (!(right instanceof List) || ((List)right).size() != 2) {
            throw new SpelEvaluationException(this.getRightOperand().getStartPosition(), SpelMessage.BETWEEN_RIGHT_OPERAND_MUST_BE_TWO_ELEMENT_LIST, new Object[0]);
        }
        final List<?> l = (List<?>)right;
        final Object low = l.get(0);
        final Object high = l.get(1);
        final TypeComparator comparator = state.getTypeComparator();
        try {
            return BooleanTypedValue.forValue(comparator.compare(left, low) >= 0 && comparator.compare(left, high) <= 0);
        }
        catch (SpelEvaluationException ex) {
            ex.setPosition(this.getStartPosition());
            throw ex;
        }
    }
}
