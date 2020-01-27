// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.style;

import org.springframework.util.ObjectUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.Assert;

public class DefaultToStringStyler implements ToStringStyler
{
    private final ValueStyler valueStyler;
    
    public DefaultToStringStyler(final ValueStyler valueStyler) {
        Assert.notNull(valueStyler, "ValueStyler must not be null");
        this.valueStyler = valueStyler;
    }
    
    protected final ValueStyler getValueStyler() {
        return this.valueStyler;
    }
    
    @Override
    public void styleStart(final StringBuilder buffer, final Object obj) {
        if (!obj.getClass().isArray()) {
            buffer.append('[').append(ClassUtils.getShortName(obj.getClass()));
            this.styleIdentityHashCode(buffer, obj);
        }
        else {
            buffer.append('[');
            this.styleIdentityHashCode(buffer, obj);
            buffer.append(' ');
            this.styleValue(buffer, obj);
        }
    }
    
    private void styleIdentityHashCode(final StringBuilder buffer, final Object obj) {
        buffer.append('@');
        buffer.append(ObjectUtils.getIdentityHexString(obj));
    }
    
    @Override
    public void styleEnd(final StringBuilder buffer, final Object o) {
        buffer.append(']');
    }
    
    @Override
    public void styleField(final StringBuilder buffer, final String fieldName, final Object value) {
        this.styleFieldStart(buffer, fieldName);
        this.styleValue(buffer, value);
        this.styleFieldEnd(buffer, fieldName);
    }
    
    protected void styleFieldStart(final StringBuilder buffer, final String fieldName) {
        buffer.append(' ').append(fieldName).append(" = ");
    }
    
    protected void styleFieldEnd(final StringBuilder buffer, final String fieldName) {
    }
    
    @Override
    public void styleValue(final StringBuilder buffer, final Object value) {
        buffer.append(this.valueStyler.style(value));
    }
    
    @Override
    public void styleFieldSeparator(final StringBuilder buffer) {
        buffer.append(',');
    }
}
