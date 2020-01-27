// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public interface BeanDefinitionReader
{
    BeanDefinitionRegistry getRegistry();
    
    ResourceLoader getResourceLoader();
    
    ClassLoader getBeanClassLoader();
    
    BeanNameGenerator getBeanNameGenerator();
    
    int loadBeanDefinitions(final Resource p0) throws BeanDefinitionStoreException;
    
    int loadBeanDefinitions(final Resource... p0) throws BeanDefinitionStoreException;
    
    int loadBeanDefinitions(final String p0) throws BeanDefinitionStoreException;
    
    int loadBeanDefinitions(final String... p0) throws BeanDefinitionStoreException;
}
