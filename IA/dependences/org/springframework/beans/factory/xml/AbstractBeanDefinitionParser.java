// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.xml;

import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.parsing.ComponentDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.w3c.dom.Node;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.w3c.dom.Element;

public abstract class AbstractBeanDefinitionParser implements BeanDefinitionParser
{
    public static final String ID_ATTRIBUTE = "id";
    public static final String NAME_ATTRIBUTE = "name";
    
    @Override
    public final BeanDefinition parse(final Element element, final ParserContext parserContext) {
        final AbstractBeanDefinition definition = this.parseInternal(element, parserContext);
        if (definition != null && !parserContext.isNested()) {
            try {
                final String id = this.resolveId(element, definition, parserContext);
                if (!StringUtils.hasText(id)) {
                    parserContext.getReaderContext().error("Id is required for element '" + parserContext.getDelegate().getLocalName(element) + "' when used as a top-level tag", element);
                }
                String[] aliases = new String[0];
                final String name = element.getAttribute("name");
                if (StringUtils.hasLength(name)) {
                    aliases = StringUtils.trimArrayElements(StringUtils.commaDelimitedListToStringArray(name));
                }
                final BeanDefinitionHolder holder = new BeanDefinitionHolder(definition, id, aliases);
                this.registerBeanDefinition(holder, parserContext.getRegistry());
                if (this.shouldFireEvents()) {
                    final BeanComponentDefinition componentDefinition = new BeanComponentDefinition(holder);
                    this.postProcessComponentDefinition(componentDefinition);
                    parserContext.registerComponent(componentDefinition);
                }
            }
            catch (BeanDefinitionStoreException ex) {
                parserContext.getReaderContext().error(ex.getMessage(), element);
                return null;
            }
        }
        return definition;
    }
    
    protected String resolveId(final Element element, final AbstractBeanDefinition definition, final ParserContext parserContext) throws BeanDefinitionStoreException {
        if (this.shouldGenerateId()) {
            return parserContext.getReaderContext().generateBeanName(definition);
        }
        String id = element.getAttribute("id");
        if (!StringUtils.hasText(id) && this.shouldGenerateIdAsFallback()) {
            id = parserContext.getReaderContext().generateBeanName(definition);
        }
        return id;
    }
    
    protected void registerBeanDefinition(final BeanDefinitionHolder definition, final BeanDefinitionRegistry registry) {
        BeanDefinitionReaderUtils.registerBeanDefinition(definition, registry);
    }
    
    protected abstract AbstractBeanDefinition parseInternal(final Element p0, final ParserContext p1);
    
    protected boolean shouldGenerateId() {
        return false;
    }
    
    protected boolean shouldGenerateIdAsFallback() {
        return false;
    }
    
    protected boolean shouldFireEvents() {
        return true;
    }
    
    protected void postProcessComponentDefinition(final BeanComponentDefinition componentDefinition) {
    }
}
