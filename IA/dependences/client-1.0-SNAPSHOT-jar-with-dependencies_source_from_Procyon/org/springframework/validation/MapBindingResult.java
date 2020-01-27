// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.validation;

import org.springframework.util.Assert;
import java.util.Map;
import java.io.Serializable;

public class MapBindingResult extends AbstractBindingResult implements Serializable
{
    private final Map<?, ?> target;
    
    public MapBindingResult(final Map<?, ?> target, final String objectName) {
        super(objectName);
        Assert.notNull(target, "Target Map must not be null");
        this.target = target;
    }
    
    public final Map<?, ?> getTargetMap() {
        return this.target;
    }
    
    @Override
    public final Object getTarget() {
        return this.target;
    }
    
    @Override
    protected Object getActualFieldValue(final String field) {
        return this.target.get(field);
    }
}
