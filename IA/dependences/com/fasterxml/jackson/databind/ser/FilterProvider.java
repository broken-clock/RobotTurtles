// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;

public abstract class FilterProvider
{
    @Deprecated
    public abstract BeanPropertyFilter findFilter(final Object p0);
    
    public PropertyFilter findPropertyFilter(final Object filterId, final Object valueToFilter) {
        final BeanPropertyFilter old = this.findFilter(filterId);
        if (old == null) {
            return null;
        }
        return SimpleBeanPropertyFilter.from(old);
    }
}
