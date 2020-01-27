// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.xml;

import org.springframework.beans.factory.config.BeanDefinition;
import org.w3c.dom.Element;

public interface BeanDefinitionParser
{
    BeanDefinition parse(final Element p0, final ParserContext p1);
}
