// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.support;

import org.springframework.util.NumberUtils;
import java.math.BigDecimal;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.TypeComparator;

public class StandardTypeComparator implements TypeComparator
{
    @Override
    public boolean canCompare(final Object left, final Object right) {
        return left == null || right == null || (left instanceof Number && right instanceof Number) || left instanceof Comparable;
    }
    
    @Override
    public int compare(final Object left, final Object right) throws SpelEvaluationException {
        if (left == null) {
            return (right == null) ? 0 : -1;
        }
        if (right == null) {
            return 1;
        }
        if (!(left instanceof Number) || !(right instanceof Number)) {
            try {
                if (left instanceof Comparable) {
                    return ((Comparable)left).compareTo(right);
                }
            }
            catch (ClassCastException ex) {
                throw new SpelEvaluationException(ex, SpelMessage.NOT_COMPARABLE, new Object[] { left.getClass(), right.getClass() });
            }
            throw new SpelEvaluationException(SpelMessage.NOT_COMPARABLE, new Object[] { left.getClass(), right.getClass() });
        }
        final Number leftNumber = (Number)left;
        final Number rightNumber = (Number)right;
        if (leftNumber instanceof BigDecimal || rightNumber instanceof BigDecimal) {
            final BigDecimal leftBigDecimal = NumberUtils.convertNumberToTargetClass(leftNumber, BigDecimal.class);
            final BigDecimal rightBigDecimal = NumberUtils.convertNumberToTargetClass(rightNumber, BigDecimal.class);
            return leftBigDecimal.compareTo(rightBigDecimal);
        }
        if (leftNumber instanceof Double || rightNumber instanceof Double) {
            return Double.compare(leftNumber.doubleValue(), rightNumber.doubleValue());
        }
        if (leftNumber instanceof Float || rightNumber instanceof Float) {
            return Float.compare(leftNumber.floatValue(), rightNumber.floatValue());
        }
        if (leftNumber instanceof Long || rightNumber instanceof Long) {
            return compare(leftNumber.longValue(), rightNumber.longValue());
        }
        return compare(leftNumber.intValue(), rightNumber.intValue());
    }
    
    private static int compare(final int x, final int y) {
        return (x < y) ? -1 : ((x > y) ? 1 : 0);
    }
    
    private static int compare(final long x, final long y) {
        return (x < y) ? -1 : ((x > y) ? 1 : 0);
    }
}
