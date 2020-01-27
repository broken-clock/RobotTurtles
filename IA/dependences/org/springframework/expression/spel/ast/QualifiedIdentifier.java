// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.TypedValue;

public class QualifiedIdentifier extends SpelNodeImpl
{
    private TypedValue value;
    
    public QualifiedIdentifier(final int pos, final SpelNodeImpl... operands) {
        super(pos, operands);
    }
    
    @Override
    public TypedValue getValueInternal(final ExpressionState state) throws EvaluationException {
        if (this.value == null) {
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < this.getChildCount(); ++i) {
                final Object value = this.children[i].getValueInternal(state).getValue();
                if (i > 0 && !value.toString().startsWith("$")) {
                    sb.append(".");
                }
                sb.append(value);
            }
            this.value = new TypedValue(sb.toString());
        }
        return this.value;
    }
    
    @Override
    public String toStringAST() {
        final StringBuilder sb = new StringBuilder();
        if (this.value != null) {
            sb.append(this.value.getValue());
        }
        else {
            for (int i = 0; i < this.getChildCount(); ++i) {
                if (i > 0) {
                    sb.append(".");
                }
                sb.append(this.getChild(i).toStringAST());
            }
        }
        return sb.toString();
    }
}
