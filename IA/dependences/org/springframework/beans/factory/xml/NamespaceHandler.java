// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.xml;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.w3c.dom.Node;
import org.springframework.beans.factory.config.BeanDefinition;
import org.w3c.dom.Element;

public interface NamespaceHandler
{
    void init();
    
    BeanDefinition parse(final Element p0, final ParserContext p1);
    
    BeanDefinitionHolder decorate(final Node p0, final BeanDefinitionHolder p1, final ParserContext p2);
}
