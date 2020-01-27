// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.expression.TypedValue;

public class StringLiteral extends Literal
{
    private final TypedValue value;
    
    public StringLiteral(final String payload, final int pos, String value) {
        super(payload, pos);
        value = value.substring(1, value.length() - 1);
        this.value = new TypedValue(value.replaceAll("''", "'").replaceAll("\"\"", "\""));
    }
    
    @Override
    public TypedValue getLiteralValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return "'" + this.getLiteralValue().getValue() + "'";
    }
}
