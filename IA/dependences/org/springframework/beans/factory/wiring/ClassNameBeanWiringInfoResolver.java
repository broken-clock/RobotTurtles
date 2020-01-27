// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.wiring;

import org.springframework.util.ClassUtils;
import org.springframework.util.Assert;

public class ClassNameBeanWiringInfoResolver implements BeanWiringInfoResolver
{
    @Override
    public BeanWiringInfo resolveWiringInfo(final Object beanInstance) {
        Assert.notNull(beanInstance, "Bean instance must not be null");
        return new BeanWiringInfo(ClassUtils.getUserClass(beanInstance).getName(), true);
    }
}
