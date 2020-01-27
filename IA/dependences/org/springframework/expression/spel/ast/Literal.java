// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.expression.spel.InternalParseException;
import org.springframework.expression.spel.SpelParseException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;

public abstract class Literal extends SpelNodeImpl
{
    private final String originalValue;
    
    public Literal(final String originalValue, final int pos) {
        super(pos, new SpelNodeImpl[0]);
        this.originalValue = originalValue;
    }
    
    public final String getOriginalValue() {
        return this.originalValue;
    }
    
    @Override
    public final TypedValue getValueInternal(final ExpressionState state) throws SpelEvaluationException {
        return this.getLiteralValue();
    }
    
    @Override
    public String toString() {
        return this.getLiteralValue().getValue().toString();
    }
    
    @Override
    public String toStringAST() {
        return this.toString();
    }
    
    public abstract TypedValue getLiteralValue();
    
    public static Literal getIntLiteral(final String numberToken, final int pos, final int radix) {
        try {
            final int value = Integer.parseInt(numberToken, radix);
            return new IntLiteral(numberToken, pos, value);
        }
        catch (NumberFormatException nfe) {
            throw new InternalParseException(new SpelParseException(pos >> 16, nfe, SpelMessage.NOT_AN_INTEGER, new Object[] { numberToken }));
        }
    }
    
    public static Literal getLongLiteral(final String numberToken, final int pos, final int radix) {
        try {
            final long value = Long.parseLong(numberToken, radix);
            return new LongLiteral(numberToken, pos, value);
        }
        catch (NumberFormatException nfe) {
            throw new InternalParseException(new SpelParseException(pos >> 16, nfe, SpelMessage.NOT_A_LONG, new Object[] { numberToken }));
        }
    }
    
    public static Literal getRealLiteral(final String numberToken, final int pos, final boolean isFloat) {
        try {
            if (isFloat) {
                final float value = Float.parseFloat(numberToken);
                return new FloatLiteral(numberToken, pos, value);
            }
            final double value2 = Double.parseDouble(numberToken);
            return new RealLiteral(numberToken, pos, value2);
        }
        catch (NumberFormatException nfe) {
            throw new InternalParseException(new SpelParseException(pos >> 16, nfe, SpelMessage.NOT_A_REAL, new Object[] { numberToken }));
        }
    }
}
