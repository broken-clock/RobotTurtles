// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.parsing;

import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import java.util.List;
import java.util.ArrayList;
import org.springframework.beans.factory.config.BeanReference;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;

public class BeanComponentDefinition extends BeanDefinitionHolder implements ComponentDefinition
{
    private BeanDefinition[] innerBeanDefinitions;
    private BeanReference[] beanReferences;
    
    public BeanComponentDefinition(final BeanDefinition beanDefinition, final String beanName) {
        super(beanDefinition, beanName);
        this.findInnerBeanDefinitionsAndBeanReferences(beanDefinition);
    }
    
    public BeanComponentDefinition(final BeanDefinition beanDefinition, final String beanName, final String[] aliases) {
        super(beanDefinition, beanName, aliases);
        this.findInnerBeanDefinitionsAndBeanReferences(beanDefinition);
    }
    
    public BeanComponentDefinition(final BeanDefinitionHolder holder) {
        super(holder);
        this.findInnerBeanDefinitionsAndBeanReferences(holder.getBeanDefinition());
    }
    
    private void findInnerBeanDefinitionsAndBeanReferences(final BeanDefinition beanDefinition) {
        final List<BeanDefinition> innerBeans = new ArrayList<BeanDefinition>();
        final List<BeanReference> references = new ArrayList<BeanReference>();
        final PropertyValues propertyValues = beanDefinition.getPropertyValues();
        for (int i = 0; i < propertyValues.getPropertyValues().length; ++i) {
            final PropertyValue propertyValue = propertyValues.getPropertyValues()[i];
            final Object value = propertyValue.getValue();
            if (value instanceof BeanDefinitionHolder) {
                innerBeans.add(((BeanDefinitionHolder)value).getBeanDefinition());
            }
            else if (value instanceof BeanDefinition) {
                innerBeans.add((BeanDefinition)value);
            }
            else if (value instanceof BeanReference) {
                references.add((BeanReference)value);
            }
        }
        this.innerBeanDefinitions = innerBeans.toArray(new BeanDefinition[innerBeans.size()]);
        this.beanReferences = references.toArray(new BeanReference[references.size()]);
    }
    
    @Override
    public String getName() {
        return this.getBeanName();
    }
    
    @Override
    public String getDescription() {
        return this.getShortDescription();
    }
    
    @Override
    public BeanDefinition[] getBeanDefinitions() {
        return new BeanDefinition[] { this.getBeanDefinition() };
    }
    
    @Override
    public BeanDefinition[] getInnerBeanDefinitions() {
        return this.innerBeanDefinitions;
    }
    
    @Override
    public BeanReference[] getBeanReferences() {
        return this.beanReferences;
    }
    
    @Override
    public String toString() {
        return this.getDescription();
    }
    
    @Override
    public boolean equals(final Object other) {
        return this == other || (other instanceof BeanComponentDefinition && super.equals(other));
    }
}
