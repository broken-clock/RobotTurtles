// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.xml;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.w3c.dom.Node;

public interface BeanDefinitionDecorator
{
    BeanDefinitionHolder decorate(final Node p0, final BeanDefinitionHolder p1, final ParserContext p2);
}
