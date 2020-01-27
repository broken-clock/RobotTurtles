// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.util.StringUtils;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.BeanWrapper;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;

public class PropertyPathFactoryBean implements FactoryBean<Object>, BeanNameAware, BeanFactoryAware
{
    private static final Log logger;
    private BeanWrapper targetBeanWrapper;
    private String targetBeanName;
    private String propertyPath;
    private Class<?> resultType;
    private String beanName;
    private BeanFactory beanFactory;
    
    public void setTargetObject(final Object targetObject) {
        this.targetBeanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(targetObject);
    }
    
    public void setTargetBeanName(final String targetBeanName) {
        this.targetBeanName = StringUtils.trimAllWhitespace(targetBeanName);
    }
    
    public void setPropertyPath(final String propertyPath) {
        this.propertyPath = StringUtils.trimAllWhitespace(propertyPath);
    }
    
    public void setResultType(final Class<?> resultType) {
        this.resultType = resultType;
    }
    
    @Override
    public void setBeanName(final String beanName) {
        this.beanName = StringUtils.trimAllWhitespace(BeanFactoryUtils.originalBeanName(beanName));
    }
    
    @Override
    public void setBeanFactory(final BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        if (this.targetBeanWrapper != null && this.targetBeanName != null) {
            throw new IllegalArgumentException("Specify either 'targetObject' or 'targetBeanName', not both");
        }
        if (this.targetBeanWrapper == null && this.targetBeanName == null) {
            if (this.propertyPath != null) {
                throw new IllegalArgumentException("Specify 'targetObject' or 'targetBeanName' in combination with 'propertyPath'");
            }
            final int dotIndex = this.beanName.indexOf(46);
            if (dotIndex == -1) {
                throw new IllegalArgumentException("Neither 'targetObject' nor 'targetBeanName' specified, and PropertyPathFactoryBean bean name '" + this.beanName + "' does not follow 'beanName.property' syntax");
            }
            this.targetBeanName = this.beanName.substring(0, dotIndex);
            this.propertyPath = this.beanName.substring(dotIndex + 1);
        }
        else if (this.propertyPath == null) {
            throw new IllegalArgumentException("'propertyPath' is required");
        }
        if (this.targetBeanWrapper == null && this.beanFactory.isSingleton(this.targetBeanName)) {
            final Object bean = this.beanFactory.getBean(this.targetBeanName);
            this.targetBeanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(bean);
            this.resultType = this.targetBeanWrapper.getPropertyType(this.propertyPath);
        }
    }
    
    @Override
    public Object getObject() throws BeansException {
        BeanWrapper target = this.targetBeanWrapper;
        if (target != null) {
            if (PropertyPathFactoryBean.logger.isWarnEnabled() && this.targetBeanName != null && this.beanFactory instanceof ConfigurableBeanFactory && ((ConfigurableBeanFactory)this.beanFactory).isCurrentlyInCreation(this.targetBeanName)) {
                PropertyPathFactoryBean.logger.warn("Target bean '" + this.targetBeanName + "' is still in creation due to a circular " + "reference - obtained value for property '" + this.propertyPath + "' may be outdated!");
            }
        }
        else {
            final Object bean = this.beanFactory.getBean(this.targetBeanName);
            target = PropertyAccessorFactory.forBeanPropertyAccess(bean);
        }
        return target.getPropertyValue(this.propertyPath);
    }
    
    @Override
    public Class<?> getObjectType() {
        return this.resultType;
    }
    
    @Override
    public boolean isSingleton() {
        return false;
    }
    
    static {
        logger = LogFactory.getLog(PropertyPathFactoryBean.class);
    }
}
