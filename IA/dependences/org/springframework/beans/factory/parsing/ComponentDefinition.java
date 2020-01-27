// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.parsing;

import org.springframework.beans.factory.config.BeanReference;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.BeanMetadataElement;

public interface ComponentDefinition extends BeanMetadataElement
{
    String getName();
    
    String getDescription();
    
    BeanDefinition[] getBeanDefinitions();
    
    BeanDefinition[] getInnerBeanDefinitions();
    
    BeanReference[] getBeanReferences();
}
