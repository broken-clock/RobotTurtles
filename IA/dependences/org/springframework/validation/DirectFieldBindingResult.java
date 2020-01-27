// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.validation;

import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.Assert;
import org.springframework.beans.ConfigurablePropertyAccessor;

public class DirectFieldBindingResult extends AbstractPropertyBindingResult
{
    private final Object target;
    private transient ConfigurablePropertyAccessor directFieldAccessor;
    
    public DirectFieldBindingResult(final Object target, final String objectName) {
        super(objectName);
        this.target = target;
    }
    
    @Override
    public final Object getTarget() {
        return this.target;
    }
    
    @Override
    public final ConfigurablePropertyAccessor getPropertyAccessor() {
        if (this.directFieldAccessor == null) {
            (this.directFieldAccessor = this.createDirectFieldAccessor()).setExtractOldValueForEditor(true);
        }
        return this.directFieldAccessor;
    }
    
    protected ConfigurablePropertyAccessor createDirectFieldAccessor() {
        Assert.state(this.target != null, "Cannot access fields on null target instance '" + this.getObjectName() + "'!");
        return PropertyAccessorFactory.forDirectFieldAccess(this.target);
    }
}
