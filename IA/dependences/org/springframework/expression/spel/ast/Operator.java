// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.util.ObjectUtils;
import org.springframework.util.NumberUtils;
import java.math.BigDecimal;
import org.springframework.expression.spel.ExpressionState;

public abstract class Operator extends SpelNodeImpl
{
    private final String operatorName;
    
    public Operator(final String payload, final int pos, final SpelNodeImpl... operands) {
        super(pos, operands);
        this.operatorName = payload;
    }
    
    public SpelNodeImpl getLeftOperand() {
        return this.children[0];
    }
    
    public SpelNodeImpl getRightOperand() {
        return this.children[1];
    }
    
    public final String getOperatorName() {
        return this.operatorName;
    }
    
    @Override
    public String toStringAST() {
        final StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(this.getChild(0).toStringAST());
        for (int i = 1; i < this.getChildCount(); ++i) {
            sb.append(" ").append(this.getOperatorName()).append(" ");
            sb.append(this.getChild(i).toStringAST());
        }
        sb.append(")");
        return sb.toString();
    }
    
    protected boolean equalityCheck(final ExpressionState state, final Object left, final Object right) {
        if (left instanceof Number && right instanceof Number) {
            final Number leftNumber = (Number)left;
            final Number rightNumber = (Number)right;
            if (leftNumber instanceof BigDecimal || rightNumber instanceof BigDecimal) {
                final BigDecimal leftBigDecimal = NumberUtils.convertNumberToTargetClass(leftNumber, BigDecimal.class);
                final BigDecimal rightBigDecimal = NumberUtils.convertNumberToTargetClass(rightNumber, BigDecimal.class);
                return (leftBigDecimal == null) ? (rightBigDecimal == null) : (leftBigDecimal.compareTo(rightBigDecimal) == 0);
            }
            if (leftNumber instanceof Double || rightNumber instanceof Double) {
                return leftNumber.doubleValue() == rightNumber.doubleValue();
            }
            if (leftNumber instanceof Float || rightNumber instanceof Float) {
                return leftNumber.floatValue() == rightNumber.floatValue();
            }
            if (leftNumber instanceof Long || rightNumber instanceof Long) {
                return leftNumber.longValue() == rightNumber.longValue();
            }
            return leftNumber.intValue() == rightNumber.intValue();
        }
        else {
            if (left != null && left instanceof Comparable) {
                return state.getTypeComparator().compare(left, right) == 0;
            }
            return ObjectUtils.nullSafeEquals(left, right);
        }
    }
}
