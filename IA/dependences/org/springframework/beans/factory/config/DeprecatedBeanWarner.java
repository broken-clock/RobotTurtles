// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import org.springframework.util.StringUtils;
import org.springframework.beans.BeansException;
import java.lang.annotation.Annotation;
import org.springframework.util.ClassUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

public class DeprecatedBeanWarner implements BeanFactoryPostProcessor
{
    protected transient Log logger;
    
    public DeprecatedBeanWarner() {
        this.logger = LogFactory.getLog(this.getClass());
    }
    
    public void setLoggerName(final String loggerName) {
        this.logger = LogFactory.getLog(loggerName);
    }
    
    @Override
    public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (this.isLogEnabled()) {
            final String[] beanDefinitionNames;
            final String[] beanNames = beanDefinitionNames = beanFactory.getBeanDefinitionNames();
            for (String nameToLookup : beanDefinitionNames) {
                final String beanName = nameToLookup;
                if (beanFactory.isFactoryBean(beanName)) {
                    nameToLookup = "&" + beanName;
                }
                final Class<?> beanType = ClassUtils.getUserClass(beanFactory.getType(nameToLookup));
                if (beanType != null && beanType.isAnnotationPresent(Deprecated.class)) {
                    final BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
                    this.logDeprecatedBean(beanName, beanType, beanDefinition);
                }
            }
        }
    }
    
    protected void logDeprecatedBean(final String beanName, final Class<?> beanType, final BeanDefinition beanDefinition) {
        final StringBuilder builder = new StringBuilder();
        builder.append(beanType);
        builder.append(" ['");
        builder.append(beanName);
        builder.append('\'');
        final String resourceDescription = beanDefinition.getResourceDescription();
        if (StringUtils.hasLength(resourceDescription)) {
            builder.append(" in ");
            builder.append(resourceDescription);
        }
        builder.append("] has been deprecated");
        this.writeToLog(builder.toString());
    }
    
    protected void writeToLog(final String message) {
        this.logger.warn(message);
    }
    
    protected boolean isLogEnabled() {
        return this.logger.isWarnEnabled();
    }
}
