// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.annotation;

import org.springframework.beans.BeansException;
import java.util.Iterator;
import java.lang.annotation.Annotation;
import org.springframework.beans.factory.support.AutowireCandidateResolver;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.ClassUtils;
import java.util.Set;
import org.springframework.core.Ordered;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;

public class CustomAutowireConfigurer implements BeanFactoryPostProcessor, BeanClassLoaderAware, Ordered
{
    private int order;
    private Set<?> customQualifierTypes;
    private ClassLoader beanClassLoader;
    
    public CustomAutowireConfigurer() {
        this.order = Integer.MAX_VALUE;
        this.beanClassLoader = ClassUtils.getDefaultClassLoader();
    }
    
    public void setOrder(final int order) {
        this.order = order;
    }
    
    @Override
    public int getOrder() {
        return this.order;
    }
    
    @Override
    public void setBeanClassLoader(final ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }
    
    public void setCustomQualifierTypes(final Set<?> customQualifierTypes) {
        this.customQualifierTypes = customQualifierTypes;
    }
    
    @Override
    public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (this.customQualifierTypes != null) {
            if (!(beanFactory instanceof DefaultListableBeanFactory)) {
                throw new IllegalStateException("CustomAutowireConfigurer needs to operate on a DefaultListableBeanFactory");
            }
            final DefaultListableBeanFactory dlbf = (DefaultListableBeanFactory)beanFactory;
            if (!(dlbf.getAutowireCandidateResolver() instanceof QualifierAnnotationAutowireCandidateResolver)) {
                dlbf.setAutowireCandidateResolver(new QualifierAnnotationAutowireCandidateResolver());
            }
            final QualifierAnnotationAutowireCandidateResolver resolver = (QualifierAnnotationAutowireCandidateResolver)dlbf.getAutowireCandidateResolver();
            for (final Object value : this.customQualifierTypes) {
                Class<? extends Annotation> customType = null;
                if (value instanceof Class) {
                    customType = (Class<? extends Annotation>)value;
                }
                else {
                    if (!(value instanceof String)) {
                        throw new IllegalArgumentException("Invalid value [" + value + "] for custom qualifier type: needs to be Class or String.");
                    }
                    final String className = (String)value;
                    customType = (Class<? extends Annotation>)ClassUtils.resolveClassName(className, this.beanClassLoader);
                }
                if (!Annotation.class.isAssignableFrom(customType)) {
                    throw new IllegalArgumentException("Qualifier type [" + customType.getName() + "] needs to be annotation type");
                }
                resolver.addQualifierType(customType);
            }
        }
    }
}
