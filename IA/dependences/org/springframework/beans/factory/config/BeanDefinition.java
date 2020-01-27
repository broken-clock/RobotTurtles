// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.BeanMetadataElement;
import org.springframework.core.AttributeAccessor;

public interface BeanDefinition extends AttributeAccessor, BeanMetadataElement
{
    public static final String SCOPE_SINGLETON = "singleton";
    public static final String SCOPE_PROTOTYPE = "prototype";
    public static final int ROLE_APPLICATION = 0;
    public static final int ROLE_SUPPORT = 1;
    public static final int ROLE_INFRASTRUCTURE = 2;
    
    String getParentName();
    
    void setParentName(final String p0);
    
    String getBeanClassName();
    
    void setBeanClassName(final String p0);
    
    String getFactoryBeanName();
    
    void setFactoryBeanName(final String p0);
    
    String getFactoryMethodName();
    
    void setFactoryMethodName(final String p0);
    
    String getScope();
    
    void setScope(final String p0);
    
    boolean isLazyInit();
    
    void setLazyInit(final boolean p0);
    
    String[] getDependsOn();
    
    void setDependsOn(final String[] p0);
    
    boolean isAutowireCandidate();
    
    void setAutowireCandidate(final boolean p0);
    
    boolean isPrimary();
    
    void setPrimary(final boolean p0);
    
    ConstructorArgumentValues getConstructorArgumentValues();
    
    MutablePropertyValues getPropertyValues();
    
    boolean isSingleton();
    
    boolean isPrototype();
    
    boolean isAbstract();
    
    int getRole();
    
    String getDescription();
    
    String getResourceDescription();
    
    BeanDefinition getOriginatingBeanDefinition();
}
