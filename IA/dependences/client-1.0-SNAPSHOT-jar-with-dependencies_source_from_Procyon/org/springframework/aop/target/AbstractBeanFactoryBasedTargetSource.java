// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.target;

import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.apache.commons.logging.Log;
import java.io.Serializable;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.aop.TargetSource;

public abstract class AbstractBeanFactoryBasedTargetSource implements TargetSource, BeanFactoryAware, Serializable
{
    private static final long serialVersionUID = -4721607536018568393L;
    protected final Log logger;
    private String targetBeanName;
    private Class<?> targetClass;
    private BeanFactory beanFactory;
    
    public AbstractBeanFactoryBasedTargetSource() {
        this.logger = LogFactory.getLog(this.getClass());
    }
    
    public void setTargetBeanName(final String targetBeanName) {
        this.targetBeanName = targetBeanName;
    }
    
    public String getTargetBeanName() {
        return this.targetBeanName;
    }
    
    public void setTargetClass(final Class<?> targetClass) {
        this.targetClass = targetClass;
    }
    
    @Override
    public void setBeanFactory(final BeanFactory beanFactory) {
        if (this.targetBeanName == null) {
            throw new IllegalStateException("Property'targetBeanName' is required");
        }
        this.beanFactory = beanFactory;
    }
    
    public BeanFactory getBeanFactory() {
        return this.beanFactory;
    }
    
    @Override
    public synchronized Class<?> getTargetClass() {
        if (this.targetClass == null && this.beanFactory != null) {
            this.targetClass = this.beanFactory.getType(this.targetBeanName);
            if (this.targetClass == null) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("Getting bean with name '" + this.targetBeanName + "' in order to determine type");
                }
                final Object beanInstance = this.beanFactory.getBean(this.targetBeanName);
                if (beanInstance != null) {
                    this.targetClass = beanInstance.getClass();
                }
            }
        }
        return this.targetClass;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public void releaseTarget(final Object target) throws Exception {
    }
    
    protected void copyFrom(final AbstractBeanFactoryBasedTargetSource other) {
        this.targetBeanName = other.targetBeanName;
        this.targetClass = other.targetClass;
        this.beanFactory = other.beanFactory;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || !this.getClass().equals(other.getClass())) {
            return false;
        }
        final AbstractBeanFactoryBasedTargetSource otherTargetSource = (AbstractBeanFactoryBasedTargetSource)other;
        return ObjectUtils.nullSafeEquals(this.beanFactory, otherTargetSource.beanFactory) && ObjectUtils.nullSafeEquals(this.targetBeanName, otherTargetSource.targetBeanName);
    }
    
    @Override
    public int hashCode() {
        int hashCode = this.getClass().hashCode();
        hashCode = 13 * hashCode + ObjectUtils.nullSafeHashCode(this.beanFactory);
        hashCode = 13 * hashCode + ObjectUtils.nullSafeHashCode(this.targetBeanName);
        return hashCode;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(ClassUtils.getShortName(this.getClass()));
        sb.append(" for target bean '").append(this.targetBeanName).append("'");
        if (this.targetClass != null) {
            sb.append(" of type [").append(this.targetClass.getName()).append("]");
        }
        return sb.toString();
    }
}
