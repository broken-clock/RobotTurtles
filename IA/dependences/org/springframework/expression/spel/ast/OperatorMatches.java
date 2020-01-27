// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.expression.TypedValue;
import org.springframework.expression.EvaluationException;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;
import java.util.regex.Pattern;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.support.BooleanTypedValue;
import org.springframework.expression.spel.ExpressionState;

public class OperatorMatches extends Operator
{
    public OperatorMatches(final int pos, final SpelNodeImpl... operands) {
        super("matches", pos, operands);
    }
    
    @Override
    public BooleanTypedValue getValueInternal(final ExpressionState state) throws EvaluationException {
        final SpelNodeImpl leftOp = this.getLeftOperand();
        final SpelNodeImpl rightOp = this.getRightOperand();
        final Object left = leftOp.getValue(state, String.class);
        final Object right = this.getRightOperand().getValueInternal(state).getValue();
        try {
            if (!(left instanceof String)) {
                throw new SpelEvaluationException(leftOp.getStartPosition(), SpelMessage.INVALID_FIRST_OPERAND_FOR_MATCHES_OPERATOR, new Object[] { left });
            }
            if (!(right instanceof String)) {
                throw new SpelEvaluationException(rightOp.getStartPosition(), SpelMessage.INVALID_SECOND_OPERAND_FOR_MATCHES_OPERATOR, new Object[] { right });
            }
            final Pattern pattern = Pattern.compile((String)right);
            final Matcher matcher = pattern.matcher((CharSequence)left);
            return BooleanTypedValue.forValue(matcher.matches());
        }
        catch (PatternSyntaxException pse) {
            throw new SpelEvaluationException(rightOp.getStartPosition(), pse, SpelMessage.INVALID_PATTERN, new Object[] { right });
        }
    }
}
