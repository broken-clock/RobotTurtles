// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.config;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.util.Assert;
import org.springframework.beans.factory.config.BeanReference;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.AbstractComponentDefinition;

public class AdvisorComponentDefinition extends AbstractComponentDefinition
{
    private final String advisorBeanName;
    private final BeanDefinition advisorDefinition;
    private String description;
    private BeanReference[] beanReferences;
    private BeanDefinition[] beanDefinitions;
    
    public AdvisorComponentDefinition(final String advisorBeanName, final BeanDefinition advisorDefinition) {
        this(advisorBeanName, advisorDefinition, null);
    }
    
    public AdvisorComponentDefinition(final String advisorBeanName, final BeanDefinition advisorDefinition, final BeanDefinition pointcutDefinition) {
        Assert.notNull(advisorBeanName, "'advisorBeanName' must not be null");
        Assert.notNull(advisorDefinition, "'advisorDefinition' must not be null");
        this.advisorBeanName = advisorBeanName;
        this.unwrapDefinitions(this.advisorDefinition = advisorDefinition, pointcutDefinition);
    }
    
    private void unwrapDefinitions(final BeanDefinition advisorDefinition, final BeanDefinition pointcutDefinition) {
        final MutablePropertyValues pvs = advisorDefinition.getPropertyValues();
        final BeanReference adviceReference = (BeanReference)pvs.getPropertyValue("adviceBeanName").getValue();
        if (pointcutDefinition != null) {
            this.beanReferences = new BeanReference[] { adviceReference };
            this.beanDefinitions = new BeanDefinition[] { advisorDefinition, pointcutDefinition };
            this.description = this.buildDescription(adviceReference, pointcutDefinition);
        }
        else {
            final BeanReference pointcutReference = (BeanReference)pvs.getPropertyValue("pointcut").getValue();
            this.beanReferences = new BeanReference[] { adviceReference, pointcutReference };
            this.beanDefinitions = new BeanDefinition[] { advisorDefinition };
            this.description = this.buildDescription(adviceReference, pointcutReference);
        }
    }
    
    private String buildDescription(final BeanReference adviceReference, final BeanDefinition pointcutDefinition) {
        return "Advisor <advice(ref)='" + adviceReference.getBeanName() + "', pointcut(expression)=[" + pointcutDefinition.getPropertyValues().getPropertyValue("expression").getValue() + "]>";
    }
    
    private String buildDescription(final BeanReference adviceReference, final BeanReference pointcutReference) {
        return "Advisor <advice(ref)='" + adviceReference.getBeanName() + "', pointcut(ref)='" + pointcutReference.getBeanName() + "'>";
    }
    
    @Override
    public String getName() {
        return this.advisorBeanName;
    }
    
    @Override
    public String getDescription() {
        return this.description;
    }
    
    @Override
    public BeanDefinition[] getBeanDefinitions() {
        return this.beanDefinitions;
    }
    
    @Override
    public BeanReference[] getBeanReferences() {
        return this.beanReferences;
    }
    
    @Override
    public Object getSource() {
        return this.advisorDefinition.getSource();
    }
}
