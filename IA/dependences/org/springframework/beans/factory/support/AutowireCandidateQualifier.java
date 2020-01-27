// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import org.springframework.util.Assert;
import org.springframework.beans.BeanMetadataAttributeAccessor;

public class AutowireCandidateQualifier extends BeanMetadataAttributeAccessor
{
    public static String VALUE_KEY;
    private final String typeName;
    
    public AutowireCandidateQualifier(final Class<?> type) {
        this(type.getName());
    }
    
    public AutowireCandidateQualifier(final String typeName) {
        Assert.notNull(typeName, "Type name must not be null");
        this.typeName = typeName;
    }
    
    public AutowireCandidateQualifier(final Class<?> type, final Object value) {
        this(type.getName(), value);
    }
    
    public AutowireCandidateQualifier(final String typeName, final Object value) {
        Assert.notNull(typeName, "Type name must not be null");
        this.typeName = typeName;
        this.setAttribute(AutowireCandidateQualifier.VALUE_KEY, value);
    }
    
    public String getTypeName() {
        return this.typeName;
    }
    
    static {
        AutowireCandidateQualifier.VALUE_KEY = "value";
    }
}
