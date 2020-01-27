// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import org.springframework.core.io.ResourceLoader;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

public interface ConditionContext
{
    BeanDefinitionRegistry getRegistry();
    
    ConfigurableListableBeanFactory getBeanFactory();
    
    Environment getEnvironment();
    
    ResourceLoader getResourceLoader();
    
    ClassLoader getClassLoader();
}
