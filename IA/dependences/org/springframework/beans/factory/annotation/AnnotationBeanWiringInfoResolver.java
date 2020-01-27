// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.annotation;

import org.springframework.util.ClassUtils;
import org.springframework.util.Assert;
import org.springframework.beans.factory.wiring.BeanWiringInfo;
import org.springframework.beans.factory.wiring.BeanWiringInfoResolver;

public class AnnotationBeanWiringInfoResolver implements BeanWiringInfoResolver
{
    @Override
    public BeanWiringInfo resolveWiringInfo(final Object beanInstance) {
        Assert.notNull(beanInstance, "Bean instance must not be null");
        final Configurable annotation = beanInstance.getClass().getAnnotation(Configurable.class);
        return (annotation != null) ? this.buildWiringInfo(beanInstance, annotation) : null;
    }
    
    protected BeanWiringInfo buildWiringInfo(final Object beanInstance, final Configurable annotation) {
        if (!Autowire.NO.equals(annotation.autowire())) {
            return new BeanWiringInfo(annotation.autowire().value(), annotation.dependencyCheck());
        }
        if (!"".equals(annotation.value())) {
            return new BeanWiringInfo(annotation.value(), false);
        }
        return new BeanWiringInfo(this.getDefaultBeanName(beanInstance), true);
    }
    
    protected String getDefaultBeanName(final Object beanInstance) {
        return ClassUtils.getUserClass(beanInstance).getName();
    }
}
