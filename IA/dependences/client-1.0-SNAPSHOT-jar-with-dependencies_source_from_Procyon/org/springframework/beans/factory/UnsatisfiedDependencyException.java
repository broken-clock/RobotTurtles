// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory;

import org.springframework.util.ClassUtils;
import org.springframework.beans.BeansException;

public class UnsatisfiedDependencyException extends BeanCreationException
{
    public UnsatisfiedDependencyException(final String resourceDescription, final String beanName, final String propertyName, final String msg) {
        super(resourceDescription, beanName, "Unsatisfied dependency expressed through bean property '" + propertyName + "'" + ((msg != null) ? (": " + msg) : ""));
    }
    
    public UnsatisfiedDependencyException(final String resourceDescription, final String beanName, final String propertyName, final BeansException ex) {
        this(resourceDescription, beanName, propertyName, (ex != null) ? (": " + ex.getMessage()) : "");
        this.initCause(ex);
    }
    
    public UnsatisfiedDependencyException(final String resourceDescription, final String beanName, final int ctorArgIndex, final Class<?> ctorArgType, final String msg) {
        super(resourceDescription, beanName, "Unsatisfied dependency expressed through constructor argument with index " + ctorArgIndex + " of type [" + ClassUtils.getQualifiedName(ctorArgType) + "]" + ((msg != null) ? (": " + msg) : ""));
    }
    
    public UnsatisfiedDependencyException(final String resourceDescription, final String beanName, final int ctorArgIndex, final Class<?> ctorArgType, final BeansException ex) {
        this(resourceDescription, beanName, ctorArgIndex, ctorArgType, (ex != null) ? (": " + ex.getMessage()) : "");
        this.initCause(ex);
    }
}
