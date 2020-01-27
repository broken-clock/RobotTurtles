// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.AliasRegistry;

public interface BeanDefinitionRegistry extends AliasRegistry
{
    void registerBeanDefinition(final String p0, final BeanDefinition p1) throws BeanDefinitionStoreException;
    
    void removeBeanDefinition(final String p0) throws NoSuchBeanDefinitionException;
    
    BeanDefinition getBeanDefinition(final String p0) throws NoSuchBeanDefinitionException;
    
    boolean containsBeanDefinition(final String p0);
    
    String[] getBeanDefinitionNames();
    
    int getBeanDefinitionCount();
    
    boolean isBeanNameInUse(final String p0);
}
