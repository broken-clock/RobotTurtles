// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.config;

import org.springframework.util.Assert;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.AbstractComponentDefinition;

public class PointcutComponentDefinition extends AbstractComponentDefinition
{
    private final String pointcutBeanName;
    private final BeanDefinition pointcutDefinition;
    private final String description;
    
    public PointcutComponentDefinition(final String pointcutBeanName, final BeanDefinition pointcutDefinition, final String expression) {
        Assert.notNull(pointcutBeanName, "Bean name must not be null");
        Assert.notNull(pointcutDefinition, "Pointcut definition must not be null");
        Assert.notNull(expression, "Expression must not be null");
        this.pointcutBeanName = pointcutBeanName;
        this.pointcutDefinition = pointcutDefinition;
        this.description = "Pointcut <name='" + pointcutBeanName + "', expression=[" + expression + "]>";
    }
    
    @Override
    public String getName() {
        return this.pointcutBeanName;
    }
    
    @Override
    public String getDescription() {
        return this.description;
    }
    
    @Override
    public BeanDefinition[] getBeanDefinitions() {
        return new BeanDefinition[] { this.pointcutDefinition };
    }
    
    @Override
    public Object getSource() {
        return this.pointcutDefinition.getSource();
    }
}
