// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.ArrayList;
import java.lang.reflect.Array;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.ObjectUtils;
import java.util.Iterator;
import org.springframework.beans.factory.config.TypedStringValue;
import java.util.Properties;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.BeanCreationException;
import java.util.List;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.RuntimeBeanNameReference;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.config.BeanDefinition;

class BeanDefinitionValueResolver
{
    private final AbstractBeanFactory beanFactory;
    private final String beanName;
    private final BeanDefinition beanDefinition;
    private final TypeConverter typeConverter;
    
    public BeanDefinitionValueResolver(final AbstractBeanFactory beanFactory, final String beanName, final BeanDefinition beanDefinition, final TypeConverter typeConverter) {
        this.beanFactory = beanFactory;
        this.beanName = beanName;
        this.beanDefinition = beanDefinition;
        this.typeConverter = typeConverter;
    }
    
    public Object resolveValueIfNecessary(final Object argName, final Object value) {
        if (value instanceof RuntimeBeanReference) {
            final RuntimeBeanReference ref = (RuntimeBeanReference)value;
            return this.resolveReference(argName, ref);
        }
        if (value instanceof RuntimeBeanNameReference) {
            String refName = ((RuntimeBeanNameReference)value).getBeanName();
            refName = String.valueOf(this.evaluate(refName));
            if (!this.beanFactory.containsBean(refName)) {
                throw new BeanDefinitionStoreException("Invalid bean name '" + refName + "' in bean reference for " + argName);
            }
            return refName;
        }
        else {
            if (value instanceof BeanDefinitionHolder) {
                final BeanDefinitionHolder bdHolder = (BeanDefinitionHolder)value;
                return this.resolveInnerBean(argName, bdHolder.getBeanName(), bdHolder.getBeanDefinition());
            }
            if (value instanceof BeanDefinition) {
                final BeanDefinition bd = (BeanDefinition)value;
                return this.resolveInnerBean(argName, "(inner bean)", bd);
            }
            if (value instanceof ManagedArray) {
                final ManagedArray array = (ManagedArray)value;
                Class<?> elementType = array.resolvedElementType;
                if (elementType == null) {
                    final String elementTypeName = array.getElementTypeName();
                    if (StringUtils.hasText(elementTypeName)) {
                        try {
                            elementType = ClassUtils.forName(elementTypeName, this.beanFactory.getBeanClassLoader());
                            array.resolvedElementType = elementType;
                            return this.resolveManagedArray(argName, (List<?>)value, elementType);
                        }
                        catch (Throwable ex) {
                            throw new BeanCreationException(this.beanDefinition.getResourceDescription(), this.beanName, "Error resolving array type for " + argName, ex);
                        }
                    }
                    elementType = Object.class;
                }
                return this.resolveManagedArray(argName, (List<?>)value, elementType);
            }
            if (value instanceof ManagedList) {
                return this.resolveManagedList(argName, (List<?>)value);
            }
            if (value instanceof ManagedSet) {
                return this.resolveManagedSet(argName, (Set<?>)value);
            }
            if (value instanceof ManagedMap) {
                return this.resolveManagedMap(argName, (Map<?, ?>)value);
            }
            if (value instanceof ManagedProperties) {
                final Properties original = (Properties)value;
                final Properties copy = new Properties();
                for (final Map.Entry<Object, Object> propEntry : original.entrySet()) {
                    Object propKey = propEntry.getKey();
                    Object propValue = propEntry.getValue();
                    if (propKey instanceof TypedStringValue) {
                        propKey = this.evaluate((TypedStringValue)propKey);
                    }
                    if (propValue instanceof TypedStringValue) {
                        propValue = this.evaluate((TypedStringValue)propValue);
                    }
                    copy.put(propKey, propValue);
                }
                return copy;
            }
            if (value instanceof TypedStringValue) {
                final TypedStringValue typedStringValue = (TypedStringValue)value;
                final Object valueObject = this.evaluate(typedStringValue);
                try {
                    final Class<?> resolvedTargetType = this.resolveTargetType(typedStringValue);
                    if (resolvedTargetType != null) {
                        return this.typeConverter.convertIfNecessary(valueObject, resolvedTargetType);
                    }
                    return valueObject;
                }
                catch (Throwable ex2) {
                    throw new BeanCreationException(this.beanDefinition.getResourceDescription(), this.beanName, "Error converting typed String value for " + argName, ex2);
                }
            }
            return this.evaluate(value);
        }
    }
    
    protected Object evaluate(final TypedStringValue value) {
        final Object result = this.beanFactory.evaluateBeanDefinitionString(value.getValue(), this.beanDefinition);
        if (!ObjectUtils.nullSafeEquals(result, value.getValue())) {
            value.setDynamic();
        }
        return result;
    }
    
    protected Object evaluate(final Object value) {
        if (value instanceof String) {
            return this.beanFactory.evaluateBeanDefinitionString((String)value, this.beanDefinition);
        }
        return value;
    }
    
    protected Class<?> resolveTargetType(final TypedStringValue value) throws ClassNotFoundException {
        if (value.hasTargetType()) {
            return value.getTargetType();
        }
        return value.resolveTargetType(this.beanFactory.getBeanClassLoader());
    }
    
    private Object resolveInnerBean(final Object argName, final String innerBeanName, final BeanDefinition innerBd) {
        RootBeanDefinition mbd = null;
        try {
            mbd = this.beanFactory.getMergedBeanDefinition(innerBeanName, innerBd, this.beanDefinition);
            final String actualInnerBeanName = this.adaptInnerBeanName(innerBeanName);
            this.beanFactory.registerContainedBean(actualInnerBeanName, this.beanName);
            final String[] dependsOn = mbd.getDependsOn();
            if (dependsOn != null) {
                for (final String dependsOnBean : dependsOn) {
                    this.beanFactory.getBean(dependsOnBean);
                    this.beanFactory.registerDependentBean(dependsOnBean, actualInnerBeanName);
                }
            }
            final Object innerBean = this.beanFactory.createBean(actualInnerBeanName, mbd, null);
            if (innerBean instanceof FactoryBean) {
                final boolean synthetic = mbd.isSynthetic();
                return this.beanFactory.getObjectFromFactoryBean((FactoryBean<?>)innerBean, actualInnerBeanName, !synthetic);
            }
            return innerBean;
        }
        catch (BeansException ex) {
            throw new BeanCreationException(this.beanDefinition.getResourceDescription(), this.beanName, "Cannot create inner bean '" + innerBeanName + "' " + ((mbd != null && mbd.getBeanClassName() != null) ? ("of type [" + mbd.getBeanClassName() + "] ") : "") + "while setting " + argName, ex);
        }
    }
    
    private String adaptInnerBeanName(final String innerBeanName) {
        String actualInnerBeanName = innerBeanName;
        for (int counter = 0; this.beanFactory.isBeanNameInUse(actualInnerBeanName); actualInnerBeanName = innerBeanName + "#" + counter) {
            ++counter;
        }
        return actualInnerBeanName;
    }
    
    private Object resolveReference(final Object argName, final RuntimeBeanReference ref) {
        try {
            String refName = ref.getBeanName();
            refName = String.valueOf(this.evaluate(refName));
            if (!ref.isToParent()) {
                final Object bean = this.beanFactory.getBean(refName);
                this.beanFactory.registerDependentBean(refName, this.beanName);
                return bean;
            }
            if (this.beanFactory.getParentBeanFactory() == null) {
                throw new BeanCreationException(this.beanDefinition.getResourceDescription(), this.beanName, "Can't resolve reference to bean '" + refName + "' in parent factory: no parent factory available");
            }
            return this.beanFactory.getParentBeanFactory().getBean(refName);
        }
        catch (BeansException ex) {
            throw new BeanCreationException(this.beanDefinition.getResourceDescription(), this.beanName, "Cannot resolve reference to bean '" + ref.getBeanName() + "' while setting " + argName, ex);
        }
    }
    
    private Object resolveManagedArray(final Object argName, final List<?> ml, final Class<?> elementType) {
        final Object resolved = Array.newInstance(elementType, ml.size());
        for (int i = 0; i < ml.size(); ++i) {
            Array.set(resolved, i, this.resolveValueIfNecessary(new KeyedArgName(argName, i), ml.get(i)));
        }
        return resolved;
    }
    
    private List<?> resolveManagedList(final Object argName, final List<?> ml) {
        final List<Object> resolved = new ArrayList<Object>(ml.size());
        for (int i = 0; i < ml.size(); ++i) {
            resolved.add(this.resolveValueIfNecessary(new KeyedArgName(argName, i), ml.get(i)));
        }
        return resolved;
    }
    
    private Set<?> resolveManagedSet(final Object argName, final Set<?> ms) {
        final Set<Object> resolved = new LinkedHashSet<Object>(ms.size());
        int i = 0;
        for (final Object m : ms) {
            resolved.add(this.resolveValueIfNecessary(new KeyedArgName(argName, i), m));
            ++i;
        }
        return resolved;
    }
    
    private Map<?, ?> resolveManagedMap(final Object argName, final Map<?, ?> mm) {
        final Map<Object, Object> resolved = new LinkedHashMap<Object, Object>(mm.size());
        for (final Map.Entry<?, ?> entry : mm.entrySet()) {
            final Object resolvedKey = this.resolveValueIfNecessary(argName, entry.getKey());
            final Object resolvedValue = this.resolveValueIfNecessary(new KeyedArgName(argName, entry.getKey()), entry.getValue());
            resolved.put(resolvedKey, resolvedValue);
        }
        return resolved;
    }
    
    private static class KeyedArgName
    {
        private final Object argName;
        private final Object key;
        
        public KeyedArgName(final Object argName, final Object key) {
            this.argName = argName;
            this.key = key;
        }
        
        @Override
        public String toString() {
            return this.argName + " with key " + "[" + this.key + "]";
        }
    }
}
