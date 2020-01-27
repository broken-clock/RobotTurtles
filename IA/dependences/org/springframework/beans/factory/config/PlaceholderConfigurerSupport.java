// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.util.StringValueResolver;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;

public abstract class PlaceholderConfigurerSupport extends PropertyResourceConfigurer implements BeanNameAware, BeanFactoryAware
{
    public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";
    public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";
    public static final String DEFAULT_VALUE_SEPARATOR = ":";
    protected String placeholderPrefix;
    protected String placeholderSuffix;
    protected String valueSeparator;
    protected boolean ignoreUnresolvablePlaceholders;
    protected String nullValue;
    private BeanFactory beanFactory;
    private String beanName;
    
    public PlaceholderConfigurerSupport() {
        this.placeholderPrefix = "${";
        this.placeholderSuffix = "}";
        this.valueSeparator = ":";
        this.ignoreUnresolvablePlaceholders = false;
    }
    
    public void setPlaceholderPrefix(final String placeholderPrefix) {
        this.placeholderPrefix = placeholderPrefix;
    }
    
    public void setPlaceholderSuffix(final String placeholderSuffix) {
        this.placeholderSuffix = placeholderSuffix;
    }
    
    public void setValueSeparator(final String valueSeparator) {
        this.valueSeparator = valueSeparator;
    }
    
    public void setNullValue(final String nullValue) {
        this.nullValue = nullValue;
    }
    
    public void setIgnoreUnresolvablePlaceholders(final boolean ignoreUnresolvablePlaceholders) {
        this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
    }
    
    @Override
    public void setBeanName(final String beanName) {
        this.beanName = beanName;
    }
    
    @Override
    public void setBeanFactory(final BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
    
    protected void doProcessProperties(final ConfigurableListableBeanFactory beanFactoryToProcess, final StringValueResolver valueResolver) {
        final BeanDefinitionVisitor visitor = new BeanDefinitionVisitor(valueResolver);
        final String[] beanDefinitionNames;
        final String[] beanNames = beanDefinitionNames = beanFactoryToProcess.getBeanDefinitionNames();
        for (final String curName : beanDefinitionNames) {
            if (!curName.equals(this.beanName) || !beanFactoryToProcess.equals(this.beanFactory)) {
                final BeanDefinition bd = beanFactoryToProcess.getBeanDefinition(curName);
                try {
                    visitor.visitBeanDefinition(bd);
                }
                catch (Exception ex) {
                    throw new BeanDefinitionStoreException(bd.getResourceDescription(), curName, ex.getMessage(), ex);
                }
            }
        }
        beanFactoryToProcess.resolveAliases(valueResolver);
        beanFactoryToProcess.addEmbeddedValueResolver(valueResolver);
    }
}
