// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import java.util.Iterator;
import java.util.Set;
import org.springframework.beans.factory.parsing.ComponentDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.springframework.beans.factory.xml.BeanDefinitionParser;

public class AnnotationConfigBeanDefinitionParser implements BeanDefinitionParser
{
    @Override
    public BeanDefinition parse(final Element element, final ParserContext parserContext) {
        final Object source = parserContext.extractSource(element);
        final Set<BeanDefinitionHolder> processorDefinitions = AnnotationConfigUtils.registerAnnotationConfigProcessors(parserContext.getRegistry(), source);
        final CompositeComponentDefinition compDefinition = new CompositeComponentDefinition(element.getTagName(), source);
        parserContext.pushContainingComponent(compDefinition);
        for (final BeanDefinitionHolder processorDefinition : processorDefinitions) {
            parserContext.registerComponent(new BeanComponentDefinition(processorDefinition));
        }
        parserContext.popAndRegisterContainingComponent();
        return null;
    }
}
