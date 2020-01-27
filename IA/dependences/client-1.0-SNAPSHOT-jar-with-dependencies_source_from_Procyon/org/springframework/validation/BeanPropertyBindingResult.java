// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.validation;

import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.Assert;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.BeanWrapper;
import java.io.Serializable;

public class BeanPropertyBindingResult extends AbstractPropertyBindingResult implements Serializable
{
    private final Object target;
    private final boolean autoGrowNestedPaths;
    private final int autoGrowCollectionLimit;
    private transient BeanWrapper beanWrapper;
    
    public BeanPropertyBindingResult(final Object target, final String objectName) {
        this(target, objectName, true, Integer.MAX_VALUE);
    }
    
    public BeanPropertyBindingResult(final Object target, final String objectName, final boolean autoGrowNestedPaths, final int autoGrowCollectionLimit) {
        super(objectName);
        this.target = target;
        this.autoGrowNestedPaths = autoGrowNestedPaths;
        this.autoGrowCollectionLimit = autoGrowCollectionLimit;
    }
    
    @Override
    public final Object getTarget() {
        return this.target;
    }
    
    @Override
    public final ConfigurablePropertyAccessor getPropertyAccessor() {
        if (this.beanWrapper == null) {
            (this.beanWrapper = this.createBeanWrapper()).setExtractOldValueForEditor(true);
            this.beanWrapper.setAutoGrowNestedPaths(this.autoGrowNestedPaths);
            this.beanWrapper.setAutoGrowCollectionLimit(this.autoGrowCollectionLimit);
        }
        return this.beanWrapper;
    }
    
    protected BeanWrapper createBeanWrapper() {
        Assert.state(this.target != null, "Cannot access properties on null bean instance '" + this.getObjectName() + "'!");
        return PropertyAccessorFactory.forBeanPropertyAccess(this.target);
    }
}
