// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import org.springframework.core.annotation.AnnotationUtils;
import java.util.LinkedHashMap;
import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.springframework.beans.factory.SmartFactoryBean;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanIsNotAFactoryException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import java.util.Collection;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.ListableBeanFactory;

public class StaticListableBeanFactory implements ListableBeanFactory
{
    private final Map<String, Object> beans;
    
    public StaticListableBeanFactory() {
        this.beans = new HashMap<String, Object>();
    }
    
    public void addBean(final String name, final Object bean) {
        this.beans.put(name, bean);
    }
    
    @Override
    public Object getBean(final String name) throws BeansException {
        final String beanName = BeanFactoryUtils.transformedBeanName(name);
        final Object bean = this.beans.get(beanName);
        if (bean == null) {
            throw new NoSuchBeanDefinitionException(beanName, "Defined beans are [" + StringUtils.collectionToCommaDelimitedString(this.beans.keySet()) + "]");
        }
        if (BeanFactoryUtils.isFactoryDereference(name) && !(bean instanceof FactoryBean)) {
            throw new BeanIsNotAFactoryException(beanName, bean.getClass());
        }
        if (bean instanceof FactoryBean && !BeanFactoryUtils.isFactoryDereference(name)) {
            try {
                return ((FactoryBean)bean).getObject();
            }
            catch (Exception ex) {
                throw new BeanCreationException(beanName, "FactoryBean threw exception on object creation", ex);
            }
        }
        return bean;
    }
    
    @Override
    public <T> T getBean(final String name, final Class<T> requiredType) throws BeansException {
        final Object bean = this.getBean(name);
        if (requiredType != null && !requiredType.isAssignableFrom(bean.getClass())) {
            throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
        }
        return (T)bean;
    }
    
    @Override
    public <T> T getBean(final Class<T> requiredType) throws BeansException {
        final String[] beanNames = this.getBeanNamesForType(requiredType);
        if (beanNames.length == 1) {
            return this.getBean(beanNames[0], requiredType);
        }
        if (beanNames.length > 1) {
            throw new NoUniqueBeanDefinitionException(requiredType, beanNames);
        }
        throw new NoSuchBeanDefinitionException(requiredType);
    }
    
    @Override
    public Object getBean(final String name, final Object... args) throws BeansException {
        if (args != null) {
            throw new UnsupportedOperationException("StaticListableBeanFactory does not support explicit bean creation arguments)");
        }
        return this.getBean(name);
    }
    
    @Override
    public boolean containsBean(final String name) {
        return this.beans.containsKey(name);
    }
    
    @Override
    public boolean isSingleton(final String name) throws NoSuchBeanDefinitionException {
        final Object bean = this.getBean(name);
        return bean instanceof FactoryBean && ((FactoryBean)bean).isSingleton();
    }
    
    @Override
    public boolean isPrototype(final String name) throws NoSuchBeanDefinitionException {
        final Object bean = this.getBean(name);
        return (bean instanceof SmartFactoryBean && ((SmartFactoryBean)bean).isPrototype()) || (bean instanceof FactoryBean && !((FactoryBean)bean).isSingleton());
    }
    
    @Override
    public boolean isTypeMatch(final String name, final Class<?> targetType) throws NoSuchBeanDefinitionException {
        final Class<?> type = this.getType(name);
        return targetType == null || (type != null && targetType.isAssignableFrom(type));
    }
    
    @Override
    public Class<?> getType(final String name) throws NoSuchBeanDefinitionException {
        final String beanName = BeanFactoryUtils.transformedBeanName(name);
        final Object bean = this.beans.get(beanName);
        if (bean == null) {
            throw new NoSuchBeanDefinitionException(beanName, "Defined beans are [" + StringUtils.collectionToCommaDelimitedString(this.beans.keySet()) + "]");
        }
        if (bean instanceof FactoryBean && !BeanFactoryUtils.isFactoryDereference(name)) {
            return (Class<?>)((FactoryBean)bean).getObjectType();
        }
        return bean.getClass();
    }
    
    @Override
    public String[] getAliases(final String name) {
        return new String[0];
    }
    
    @Override
    public boolean containsBeanDefinition(final String name) {
        return this.beans.containsKey(name);
    }
    
    @Override
    public int getBeanDefinitionCount() {
        return this.beans.size();
    }
    
    @Override
    public String[] getBeanDefinitionNames() {
        return StringUtils.toStringArray(this.beans.keySet());
    }
    
    @Override
    public String[] getBeanNamesForType(final Class<?> type) {
        return this.getBeanNamesForType(type, true, true);
    }
    
    @Override
    public String[] getBeanNamesForType(final Class<?> type, final boolean includeNonSingletons, final boolean includeFactoryBeans) {
        final boolean isFactoryType = type != null && FactoryBean.class.isAssignableFrom(type);
        final List<String> matches = new ArrayList<String>();
        for (final String name : this.beans.keySet()) {
            final Object beanInstance = this.beans.get(name);
            if (beanInstance instanceof FactoryBean && !isFactoryType) {
                if (!includeFactoryBeans) {
                    continue;
                }
                final Class<?> objectType = (Class<?>)((FactoryBean)beanInstance).getObjectType();
                if (objectType == null || (type != null && !type.isAssignableFrom(objectType))) {
                    continue;
                }
                matches.add(name);
            }
            else {
                if (type != null && !type.isInstance(beanInstance)) {
                    continue;
                }
                matches.add(name);
            }
        }
        return StringUtils.toStringArray(matches);
    }
    
    @Override
    public <T> Map<String, T> getBeansOfType(final Class<T> type) throws BeansException {
        return this.getBeansOfType(type, true, true);
    }
    
    @Override
    public <T> Map<String, T> getBeansOfType(final Class<T> type, final boolean includeNonSingletons, final boolean includeFactoryBeans) throws BeansException {
        final boolean isFactoryType = type != null && FactoryBean.class.isAssignableFrom(type);
        final Map<String, T> matches = new HashMap<String, T>();
        for (final Map.Entry<String, Object> entry : this.beans.entrySet()) {
            String beanName = entry.getKey();
            final Object beanInstance = entry.getValue();
            if (beanInstance instanceof FactoryBean && !isFactoryType) {
                if (!includeFactoryBeans) {
                    continue;
                }
                final FactoryBean<?> factory = (FactoryBean<?>)beanInstance;
                final Class<?> objectType = factory.getObjectType();
                if ((!includeNonSingletons && !factory.isSingleton()) || objectType == null || (type != null && !type.isAssignableFrom(objectType))) {
                    continue;
                }
                matches.put(beanName, this.getBean(beanName, type));
            }
            else {
                if (type != null && !type.isInstance(beanInstance)) {
                    continue;
                }
                if (isFactoryType) {
                    beanName = "&" + beanName;
                }
                matches.put(beanName, (T)beanInstance);
            }
        }
        return matches;
    }
    
    @Override
    public String[] getBeanNamesForAnnotation(final Class<? extends Annotation> annotationType) {
        final List<String> results = new ArrayList<String>();
        for (final String beanName : this.beans.keySet()) {
            if (this.findAnnotationOnBean(beanName, annotationType) != null) {
                results.add(beanName);
            }
        }
        return results.toArray(new String[results.size()]);
    }
    
    @Override
    public Map<String, Object> getBeansWithAnnotation(final Class<? extends Annotation> annotationType) throws BeansException {
        final Map<String, Object> results = new LinkedHashMap<String, Object>();
        for (final String beanName : this.beans.keySet()) {
            if (this.findAnnotationOnBean(beanName, annotationType) != null) {
                results.put(beanName, this.getBean(beanName));
            }
        }
        return results;
    }
    
    @Override
    public <A extends Annotation> A findAnnotationOnBean(final String beanName, final Class<A> annotationType) throws NoSuchBeanDefinitionException {
        return AnnotationUtils.findAnnotation(this.getType(beanName), annotationType);
    }
}
