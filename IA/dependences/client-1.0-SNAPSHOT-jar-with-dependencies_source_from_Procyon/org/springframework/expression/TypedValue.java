// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression;

import org.springframework.core.convert.TypeDescriptor;

public class TypedValue
{
    public static final TypedValue NULL;
    private final Object value;
    private TypeDescriptor typeDescriptor;
    
    public TypedValue(final Object value) {
        this.value = value;
        this.typeDescriptor = null;
    }
    
    public TypedValue(final Object value, final TypeDescriptor typeDescriptor) {
        this.value = value;
        this.typeDescriptor = typeDescriptor;
    }
    
    public Object getValue() {
        return this.value;
    }
    
    public TypeDescriptor getTypeDescriptor() {
        if (this.typeDescriptor == null) {
            this.typeDescriptor = TypeDescriptor.forObject(this.value);
        }
        return this.typeDescriptor;
    }
    
    @Override
    public String toString() {
        final StringBuilder str = new StringBuilder();
        str.append("TypedValue: '").append(this.value).append("' of [").append(this.getTypeDescriptor()).append("]");
        return str.toString();
    }
    
    static {
        NULL = new TypedValue(null);
    }
}
