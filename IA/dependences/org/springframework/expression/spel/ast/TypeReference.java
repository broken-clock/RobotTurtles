// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import java.lang.reflect.Array;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;

public class TypeReference extends SpelNodeImpl
{
    private final int dimensions;
    
    public TypeReference(final int pos, final SpelNodeImpl qualifiedId) {
        this(pos, qualifiedId, 0);
    }
    
    public TypeReference(final int pos, final SpelNodeImpl qualifiedId, final int dims) {
        super(pos, new SpelNodeImpl[] { qualifiedId });
        this.dimensions = dims;
    }
    
    @Override
    public TypedValue getValueInternal(final ExpressionState state) throws EvaluationException {
        final String typename = (String)this.children[0].getValueInternal(state).getValue();
        if (typename.indexOf(".") == -1 && Character.isLowerCase(typename.charAt(0))) {
            final TypeCode tc = TypeCode.valueOf(typename.toUpperCase());
            if (tc != TypeCode.OBJECT) {
                Class<?> clazz = tc.getType();
                clazz = this.makeArrayIfNecessary(clazz);
                return new TypedValue(clazz);
            }
        }
        Class<?> clazz2 = state.findType(typename);
        clazz2 = this.makeArrayIfNecessary(clazz2);
        return new TypedValue(clazz2);
    }
    
    private Class<?> makeArrayIfNecessary(Class<?> clazz) {
        if (this.dimensions != 0) {
            for (int i = 0; i < this.dimensions; ++i) {
                final Object o = Array.newInstance(clazz, 0);
                clazz = o.getClass();
            }
        }
        return clazz;
    }
    
    @Override
    public String toStringAST() {
        final StringBuilder sb = new StringBuilder();
        sb.append("T(");
        sb.append(this.getChild(0).toStringAST());
        for (int d = 0; d < this.dimensions; ++d) {
            sb.append("[]");
        }
        sb.append(")");
        return sb.toString();
    }
}
