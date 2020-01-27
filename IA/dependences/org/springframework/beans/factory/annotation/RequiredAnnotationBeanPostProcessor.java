// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.annotation;

import org.springframework.core.Conventions;
import java.lang.reflect.Method;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.BeansException;
import java.util.List;
import org.springframework.beans.factory.BeanInitializationException;
import java.util.ArrayList;
import java.beans.PropertyDescriptor;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.Assert;
import java.util.Map;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import java.lang.annotation.Annotation;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;

public class RequiredAnnotationBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements MergedBeanDefinitionPostProcessor, PriorityOrdered, BeanFactoryAware
{
    public static final String SKIP_REQUIRED_CHECK_ATTRIBUTE;
    private Class<? extends Annotation> requiredAnnotationType;
    private int order;
    private ConfigurableListableBeanFactory beanFactory;
    private final Set<String> validatedBeanNames;
    
    public RequiredAnnotationBeanPostProcessor() {
        this.requiredAnnotationType = Required.class;
        this.order = 2147483646;
        this.validatedBeanNames = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>(64));
    }
    
    public void setRequiredAnnotationType(final Class<? extends Annotation> requiredAnnotationType) {
        Assert.notNull(requiredAnnotationType, "'requiredAnnotationType' must not be null");
        this.requiredAnnotationType = requiredAnnotationType;
    }
    
    protected Class<? extends Annotation> getRequiredAnnotationType() {
        return this.requiredAnnotationType;
    }
    
    @Override
    public void setBeanFactory(final BeanFactory beanFactory) {
        if (beanFactory instanceof ConfigurableListableBeanFactory) {
            this.beanFactory = (ConfigurableListableBeanFactory)beanFactory;
        }
    }
    
    public void setOrder(final int order) {
        this.order = order;
    }
    
    @Override
    public int getOrder() {
        return this.order;
    }
    
    @Override
    public void postProcessMergedBeanDefinition(final RootBeanDefinition beanDefinition, final Class<?> beanType, final String beanName) {
    }
    
    @Override
    public PropertyValues postProcessPropertyValues(final PropertyValues pvs, final PropertyDescriptor[] pds, final Object bean, final String beanName) throws BeansException {
        if (!this.validatedBeanNames.contains(beanName)) {
            if (!this.shouldSkip(this.beanFactory, beanName)) {
                final List<String> invalidProperties = new ArrayList<String>();
                for (final PropertyDescriptor pd : pds) {
                    if (this.isRequiredProperty(pd) && !pvs.contains(pd.getName())) {
                        invalidProperties.add(pd.getName());
                    }
                }
                if (!invalidProperties.isEmpty()) {
                    throw new BeanInitializationException(this.buildExceptionMessage(invalidProperties, beanName));
                }
            }
            this.validatedBeanNames.add(beanName);
        }
        return pvs;
    }
    
    protected boolean shouldSkip(final ConfigurableListableBeanFactory beanFactory, final String beanName) {
        if (beanFactory == null || !beanFactory.containsBeanDefinition(beanName)) {
            return false;
        }
        final BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
        if (beanDefinition.getFactoryBeanName() != null) {
            return true;
        }
        final Object value = beanDefinition.getAttribute(RequiredAnnotationBeanPostProcessor.SKIP_REQUIRED_CHECK_ATTRIBUTE);
        return value != null && (Boolean.TRUE.equals(value) || Boolean.valueOf(value.toString()));
    }
    
    protected boolean isRequiredProperty(final PropertyDescriptor propertyDescriptor) {
        final Method setter = propertyDescriptor.getWriteMethod();
        return setter != null && AnnotationUtils.getAnnotation(setter, this.getRequiredAnnotationType()) != null;
    }
    
    private String buildExceptionMessage(final List<String> invalidProperties, final String beanName) {
        final int size = invalidProperties.size();
        final StringBuilder sb = new StringBuilder();
        sb.append((size == 1) ? "Property" : "Properties");
        for (int i = 0; i < size; ++i) {
            final String propertyName = invalidProperties.get(i);
            if (i > 0) {
                if (i == size - 1) {
                    sb.append(" and");
                }
                else {
                    sb.append(",");
                }
            }
            sb.append(" '").append(propertyName).append("'");
        }
        sb.append((size == 1) ? " is" : " are");
        sb.append(" required for bean '").append(beanName).append("'");
        return sb.toString();
    }
    
    static {
        SKIP_REQUIRED_CHECK_ATTRIBUTE = Conventions.getQualifiedAttributeName(RequiredAnnotationBeanPostProcessor.class, "skipRequiredCheck");
    }
}
