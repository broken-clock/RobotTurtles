// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.xml;

import java.util.Iterator;
import java.util.Collection;
import org.springframework.core.Conventions;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.util.StringUtils;
import org.w3c.dom.Attr;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.w3c.dom.Node;
import org.springframework.beans.factory.config.BeanDefinition;
import org.w3c.dom.Element;

public class SimpleConstructorNamespaceHandler implements NamespaceHandler
{
    private static final String REF_SUFFIX = "-ref";
    private static final String DELIMITER_PREFIX = "_";
    
    @Override
    public void init() {
    }
    
    @Override
    public BeanDefinition parse(final Element element, final ParserContext parserContext) {
        parserContext.getReaderContext().error("Class [" + this.getClass().getName() + "] does not support custom elements.", element);
        return null;
    }
    
    @Override
    public BeanDefinitionHolder decorate(final Node node, final BeanDefinitionHolder definition, final ParserContext parserContext) {
        if (node instanceof Attr) {
            final Attr attr = (Attr)node;
            String argName = StringUtils.trimWhitespace(parserContext.getDelegate().getLocalName(attr));
            final String argValue = StringUtils.trimWhitespace(attr.getValue());
            final ConstructorArgumentValues cvs = definition.getBeanDefinition().getConstructorArgumentValues();
            boolean ref = false;
            if (argName.endsWith("-ref")) {
                ref = true;
                argName = argName.substring(0, argName.length() - "-ref".length());
            }
            final ConstructorArgumentValues.ValueHolder valueHolder = new ConstructorArgumentValues.ValueHolder(ref ? new RuntimeBeanReference(argValue) : argValue);
            valueHolder.setSource(parserContext.getReaderContext().extractSource(attr));
            if (argName.startsWith("_")) {
                final String arg = argName.substring(1).trim();
                if (!StringUtils.hasText(arg)) {
                    cvs.addGenericArgumentValue(valueHolder);
                }
                else {
                    int index = -1;
                    try {
                        index = Integer.parseInt(arg);
                    }
                    catch (NumberFormatException ex) {
                        parserContext.getReaderContext().error("Constructor argument '" + argName + "' specifies an invalid integer", attr);
                    }
                    if (index < 0) {
                        parserContext.getReaderContext().error("Constructor argument '" + argName + "' specifies a negative index", attr);
                    }
                    if (cvs.hasIndexedArgumentValue(index)) {
                        parserContext.getReaderContext().error("Constructor argument '" + argName + "' with index " + index + " already defined using <constructor-arg>." + " Only one approach may be used per argument.", attr);
                    }
                    cvs.addIndexedArgumentValue(index, valueHolder);
                }
            }
            else {
                final String name = Conventions.attributeNameToPropertyName(argName);
                if (this.containsArgWithName(name, cvs)) {
                    parserContext.getReaderContext().error("Constructor argument '" + argName + "' already defined using <constructor-arg>." + " Only one approach may be used per argument.", attr);
                }
                valueHolder.setName(Conventions.attributeNameToPropertyName(argName));
                cvs.addGenericArgumentValue(valueHolder);
            }
        }
        return definition;
    }
    
    private boolean containsArgWithName(final String name, final ConstructorArgumentValues cvs) {
        return this.checkName(name, cvs.getGenericArgumentValues()) || this.checkName(name, cvs.getIndexedArgumentValues().values());
    }
    
    private boolean checkName(final String name, final Collection<ConstructorArgumentValues.ValueHolder> values) {
        for (final ConstructorArgumentValues.ValueHolder holder : values) {
            if (name.equals(holder.getName())) {
                return true;
            }
        }
        return false;
    }
}
