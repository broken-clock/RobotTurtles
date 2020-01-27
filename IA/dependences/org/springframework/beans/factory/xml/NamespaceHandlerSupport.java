// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.xml;

import org.w3c.dom.Attr;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.w3c.dom.Node;
import org.springframework.beans.factory.config.BeanDefinition;
import org.w3c.dom.Element;
import java.util.HashMap;
import java.util.Map;

public abstract class NamespaceHandlerSupport implements NamespaceHandler
{
    private final Map<String, BeanDefinitionParser> parsers;
    private final Map<String, BeanDefinitionDecorator> decorators;
    private final Map<String, BeanDefinitionDecorator> attributeDecorators;
    
    public NamespaceHandlerSupport() {
        this.parsers = new HashMap<String, BeanDefinitionParser>();
        this.decorators = new HashMap<String, BeanDefinitionDecorator>();
        this.attributeDecorators = new HashMap<String, BeanDefinitionDecorator>();
    }
    
    @Override
    public BeanDefinition parse(final Element element, final ParserContext parserContext) {
        return this.findParserForElement(element, parserContext).parse(element, parserContext);
    }
    
    private BeanDefinitionParser findParserForElement(final Element element, final ParserContext parserContext) {
        final String localName = parserContext.getDelegate().getLocalName(element);
        final BeanDefinitionParser parser = this.parsers.get(localName);
        if (parser == null) {
            parserContext.getReaderContext().fatal("Cannot locate BeanDefinitionParser for element [" + localName + "]", element);
        }
        return parser;
    }
    
    @Override
    public BeanDefinitionHolder decorate(final Node node, final BeanDefinitionHolder definition, final ParserContext parserContext) {
        return this.findDecoratorForNode(node, parserContext).decorate(node, definition, parserContext);
    }
    
    private BeanDefinitionDecorator findDecoratorForNode(final Node node, final ParserContext parserContext) {
        BeanDefinitionDecorator decorator = null;
        final String localName = parserContext.getDelegate().getLocalName(node);
        if (node instanceof Element) {
            decorator = this.decorators.get(localName);
        }
        else if (node instanceof Attr) {
            decorator = this.attributeDecorators.get(localName);
        }
        else {
            parserContext.getReaderContext().fatal("Cannot decorate based on Nodes of type [" + node.getClass().getName() + "]", node);
        }
        if (decorator == null) {
            parserContext.getReaderContext().fatal("Cannot locate BeanDefinitionDecorator for " + ((node instanceof Element) ? "element" : "attribute") + " [" + localName + "]", node);
        }
        return decorator;
    }
    
    protected final void registerBeanDefinitionParser(final String elementName, final BeanDefinitionParser parser) {
        this.parsers.put(elementName, parser);
    }
    
    protected final void registerBeanDefinitionDecorator(final String elementName, final BeanDefinitionDecorator dec) {
        this.decorators.put(elementName, dec);
    }
    
    protected final void registerBeanDefinitionDecoratorForAttribute(final String attrName, final BeanDefinitionDecorator dec) {
        this.attributeDecorators.put(attrName, dec);
    }
}
