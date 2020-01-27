// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import org.springframework.util.StringUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.util.Assert;
import org.springframework.beans.BeanMetadataElement;

public class BeanDefinitionHolder implements BeanMetadataElement
{
    private final BeanDefinition beanDefinition;
    private final String beanName;
    private final String[] aliases;
    
    public BeanDefinitionHolder(final BeanDefinition beanDefinition, final String beanName) {
        this(beanDefinition, beanName, null);
    }
    
    public BeanDefinitionHolder(final BeanDefinition beanDefinition, final String beanName, final String[] aliases) {
        Assert.notNull(beanDefinition, "BeanDefinition must not be null");
        Assert.notNull(beanName, "Bean name must not be null");
        this.beanDefinition = beanDefinition;
        this.beanName = beanName;
        this.aliases = aliases;
    }
    
    public BeanDefinitionHolder(final BeanDefinitionHolder beanDefinitionHolder) {
        Assert.notNull(beanDefinitionHolder, "BeanDefinitionHolder must not be null");
        this.beanDefinition = beanDefinitionHolder.getBeanDefinition();
        this.beanName = beanDefinitionHolder.getBeanName();
        this.aliases = beanDefinitionHolder.getAliases();
    }
    
    public BeanDefinition getBeanDefinition() {
        return this.beanDefinition;
    }
    
    public String getBeanName() {
        return this.beanName;
    }
    
    public String[] getAliases() {
        return this.aliases;
    }
    
    @Override
    public Object getSource() {
        return this.beanDefinition.getSource();
    }
    
    public boolean matchesName(final String candidateName) {
        return candidateName != null && (candidateName.equals(this.beanName) || candidateName.equals(BeanFactoryUtils.transformedBeanName(this.beanName)) || ObjectUtils.containsElement(this.aliases, candidateName));
    }
    
    public String getShortDescription() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Bean definition with name '").append(this.beanName).append("'");
        if (this.aliases != null) {
            sb.append(" and aliases [").append(StringUtils.arrayToCommaDelimitedString(this.aliases)).append("]");
        }
        return sb.toString();
    }
    
    public String getLongDescription() {
        final StringBuilder sb = new StringBuilder(this.getShortDescription());
        sb.append(": ").append(this.beanDefinition);
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return this.getLongDescription();
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof BeanDefinitionHolder)) {
            return false;
        }
        final BeanDefinitionHolder otherHolder = (BeanDefinitionHolder)other;
        return this.beanDefinition.equals(otherHolder.beanDefinition) && this.beanName.equals(otherHolder.beanName) && ObjectUtils.nullSafeEquals(this.aliases, otherHolder.aliases);
    }
    
    @Override
    public int hashCode() {
        int hashCode = this.beanDefinition.hashCode();
        hashCode = 29 * hashCode + this.beanName.hashCode();
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.aliases);
        return hashCode;
    }
}
