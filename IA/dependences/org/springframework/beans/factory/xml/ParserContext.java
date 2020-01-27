// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.xml;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.parsing.ComponentDefinition;
import java.util.Stack;
import org.springframework.beans.factory.config.BeanDefinition;

public final class ParserContext
{
    private final XmlReaderContext readerContext;
    private final BeanDefinitionParserDelegate delegate;
    private BeanDefinition containingBeanDefinition;
    private final Stack<ComponentDefinition> containingComponents;
    
    public ParserContext(final XmlReaderContext readerContext, final BeanDefinitionParserDelegate delegate) {
        this.containingComponents = new Stack<ComponentDefinition>();
        this.readerContext = readerContext;
        this.delegate = delegate;
    }
    
    public ParserContext(final XmlReaderContext readerContext, final BeanDefinitionParserDelegate delegate, final BeanDefinition containingBeanDefinition) {
        this.containingComponents = new Stack<ComponentDefinition>();
        this.readerContext = readerContext;
        this.delegate = delegate;
        this.containingBeanDefinition = containingBeanDefinition;
    }
    
    public final XmlReaderContext getReaderContext() {
        return this.readerContext;
    }
    
    public final BeanDefinitionRegistry getRegistry() {
        return this.readerContext.getRegistry();
    }
    
    public final BeanDefinitionParserDelegate getDelegate() {
        return this.delegate;
    }
    
    public final BeanDefinition getContainingBeanDefinition() {
        return this.containingBeanDefinition;
    }
    
    public final boolean isNested() {
        return this.containingBeanDefinition != null;
    }
    
    public boolean isDefaultLazyInit() {
        return "true".equals(this.delegate.getDefaults().getLazyInit());
    }
    
    public Object extractSource(final Object sourceCandidate) {
        return this.readerContext.extractSource(sourceCandidate);
    }
    
    public CompositeComponentDefinition getContainingComponent() {
        return this.containingComponents.isEmpty() ? null : this.containingComponents.lastElement();
    }
    
    public void pushContainingComponent(final CompositeComponentDefinition containingComponent) {
        this.containingComponents.push(containingComponent);
    }
    
    public CompositeComponentDefinition popContainingComponent() {
        return this.containingComponents.pop();
    }
    
    public void popAndRegisterContainingComponent() {
        this.registerComponent(this.popContainingComponent());
    }
    
    public void registerComponent(final ComponentDefinition component) {
        final CompositeComponentDefinition containingComponent = this.getContainingComponent();
        if (containingComponent != null) {
            containingComponent.addNestedComponent(component);
        }
        else {
            this.readerContext.fireComponentRegistered(component);
        }
    }
    
    public void registerBeanComponent(final BeanComponentDefinition component) {
        BeanDefinitionReaderUtils.registerBeanDefinition(component, this.getRegistry());
        this.registerComponent(component);
    }
}
