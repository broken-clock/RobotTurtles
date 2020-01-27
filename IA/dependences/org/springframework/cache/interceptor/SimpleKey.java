// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.interceptor;

import org.springframework.util.StringUtils;
import java.util.Arrays;
import org.springframework.util.Assert;
import java.io.Serializable;

public final class SimpleKey implements Serializable
{
    public static final SimpleKey EMPTY;
    private final Object[] params;
    
    public SimpleKey(final Object... elements) {
        Assert.notNull(elements, "Elements must not be null");
        System.arraycopy(elements, 0, this.params = new Object[elements.length], 0, elements.length);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj || (obj instanceof SimpleKey && Arrays.equals(this.params, ((SimpleKey)obj).params));
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.params);
    }
    
    @Override
    public String toString() {
        return "SimpleKey [" + StringUtils.arrayToCommaDelimitedString(this.params) + "]";
    }
    
    static {
        EMPTY = new SimpleKey(new Object[0]);
    }
}
