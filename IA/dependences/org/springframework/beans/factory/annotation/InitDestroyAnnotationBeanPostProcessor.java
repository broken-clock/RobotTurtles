// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.annotation;

import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import org.springframework.beans.BeansException;
import java.lang.reflect.InvocationTargetException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.support.RootBeanDefinition;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.LogFactory;
import java.util.Map;
import java.lang.annotation.Annotation;
import org.apache.commons.logging.Log;
import java.io.Serializable;
import org.springframework.core.PriorityOrdered;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;

public class InitDestroyAnnotationBeanPostProcessor implements DestructionAwareBeanPostProcessor, MergedBeanDefinitionPostProcessor, PriorityOrdered, Serializable
{
    protected transient Log logger;
    private Class<? extends Annotation> initAnnotationType;
    private Class<? extends Annotation> destroyAnnotationType;
    private int order;
    private final transient Map<Class<?>, LifecycleMetadata> lifecycleMetadataCache;
    
    public InitDestroyAnnotationBeanPostProcessor() {
        this.logger = LogFactory.getLog(this.getClass());
        this.order = Integer.MAX_VALUE;
        this.lifecycleMetadataCache = new ConcurrentHashMap<Class<?>, LifecycleMetadata>(64);
    }
    
    public void setInitAnnotationType(final Class<? extends Annotation> initAnnotationType) {
        this.initAnnotationType = initAnnotationType;
    }
    
    public void setDestroyAnnotationType(final Class<? extends Annotation> destroyAnnotationType) {
        this.destroyAnnotationType = destroyAnnotationType;
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
        if (beanType != null) {
            final LifecycleMetadata metadata = this.findLifecycleMetadata(beanType);
            metadata.checkConfigMembers(beanDefinition);
        }
    }
    
    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        final LifecycleMetadata metadata = this.findLifecycleMetadata(bean.getClass());
        try {
            metadata.invokeInitMethods(bean, beanName);
        }
        catch (InvocationTargetException ex) {
            throw new BeanCreationException(beanName, "Invocation of init method failed", ex.getTargetException());
        }
        catch (Throwable ex2) {
            throw new BeanCreationException(beanName, "Couldn't invoke init method", ex2);
        }
        return bean;
    }
    
    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        return bean;
    }
    
    @Override
    public void postProcessBeforeDestruction(final Object bean, final String beanName) throws BeansException {
        final LifecycleMetadata metadata = this.findLifecycleMetadata(bean.getClass());
        try {
            metadata.invokeDestroyMethods(bean, beanName);
        }
        catch (InvocationTargetException ex) {
            final String msg = "Invocation of destroy method failed on bean with name '" + beanName + "'";
            if (this.logger.isDebugEnabled()) {
                this.logger.warn(msg, ex.getTargetException());
            }
            else {
                this.logger.warn(msg + ": " + ex.getTargetException());
            }
        }
        catch (Throwable ex2) {
            this.logger.error("Couldn't invoke destroy method on bean with name '" + beanName + "'", ex2);
        }
    }
    
    private LifecycleMetadata findLifecycleMetadata(final Class<?> clazz) {
        if (this.lifecycleMetadataCache == null) {
            return this.buildLifecycleMetadata(clazz);
        }
        LifecycleMetadata metadata = this.lifecycleMetadataCache.get(clazz);
        if (metadata == null) {
            synchronized (this.lifecycleMetadataCache) {
                metadata = this.lifecycleMetadataCache.get(clazz);
                if (metadata == null) {
                    metadata = this.buildLifecycleMetadata(clazz);
                    this.lifecycleMetadataCache.put(clazz, metadata);
                }
                return metadata;
            }
        }
        return metadata;
    }
    
    private LifecycleMetadata buildLifecycleMetadata(final Class<?> clazz) {
        final boolean debug = this.logger.isDebugEnabled();
        final LinkedList<LifecycleElement> initMethods = new LinkedList<LifecycleElement>();
        final LinkedList<LifecycleElement> destroyMethods = new LinkedList<LifecycleElement>();
        Class<?> targetClass = clazz;
        do {
            final LinkedList<LifecycleElement> currInitMethods = new LinkedList<LifecycleElement>();
            final LinkedList<LifecycleElement> currDestroyMethods = new LinkedList<LifecycleElement>();
            for (final Method method : targetClass.getDeclaredMethods()) {
                if (this.initAnnotationType != null && method.getAnnotation(this.initAnnotationType) != null) {
                    final LifecycleElement element = new LifecycleElement(method);
                    currInitMethods.add(element);
                    if (debug) {
                        this.logger.debug("Found init method on class [" + clazz.getName() + "]: " + method);
                    }
                }
                if (this.destroyAnnotationType != null && method.getAnnotation(this.destroyAnnotationType) != null) {
                    currDestroyMethods.add(new LifecycleElement(method));
                    if (debug) {
                        this.logger.debug("Found destroy method on class [" + clazz.getName() + "]: " + method);
                    }
                }
            }
            initMethods.addAll(0, currInitMethods);
            destroyMethods.addAll(currDestroyMethods);
            targetClass = targetClass.getSuperclass();
        } while (targetClass != null && targetClass != Object.class);
        return new LifecycleMetadata(clazz, initMethods, destroyMethods);
    }
    
    private void readObject(final ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.logger = LogFactory.getLog(this.getClass());
    }
    
    private class LifecycleMetadata
    {
        private final Class<?> targetClass;
        private final Collection<LifecycleElement> initMethods;
        private final Collection<LifecycleElement> destroyMethods;
        private volatile Set<LifecycleElement> checkedInitMethods;
        private volatile Set<LifecycleElement> checkedDestroyMethods;
        
        public LifecycleMetadata(final Class<?> targetClass, final Collection<LifecycleElement> initMethods, final Collection<LifecycleElement> destroyMethods) {
            this.targetClass = targetClass;
            this.initMethods = initMethods;
            this.destroyMethods = destroyMethods;
        }
        
        public void checkConfigMembers(final RootBeanDefinition beanDefinition) {
            final Set<LifecycleElement> checkedInitMethods = new LinkedHashSet<LifecycleElement>(this.initMethods.size());
            for (final LifecycleElement element : this.initMethods) {
                final String methodIdentifier = element.getIdentifier();
                if (!beanDefinition.isExternallyManagedInitMethod(methodIdentifier)) {
                    beanDefinition.registerExternallyManagedInitMethod(methodIdentifier);
                    checkedInitMethods.add(element);
                    if (!InitDestroyAnnotationBeanPostProcessor.this.logger.isDebugEnabled()) {
                        continue;
                    }
                    InitDestroyAnnotationBeanPostProcessor.this.logger.debug("Registered init method on class [" + this.targetClass.getName() + "]: " + element);
                }
            }
            final Set<LifecycleElement> checkedDestroyMethods = new LinkedHashSet<LifecycleElement>(this.destroyMethods.size());
            for (final LifecycleElement element2 : this.destroyMethods) {
                final String methodIdentifier2 = element2.getIdentifier();
                if (!beanDefinition.isExternallyManagedDestroyMethod(methodIdentifier2)) {
                    beanDefinition.registerExternallyManagedDestroyMethod(methodIdentifier2);
                    checkedDestroyMethods.add(element2);
                    if (!InitDestroyAnnotationBeanPostProcessor.this.logger.isDebugEnabled()) {
                        continue;
                    }
                    InitDestroyAnnotationBeanPostProcessor.this.logger.debug("Registered destroy method on class [" + this.targetClass.getName() + "]: " + element2);
                }
            }
            this.checkedInitMethods = checkedInitMethods;
            this.checkedDestroyMethods = checkedDestroyMethods;
        }
        
        public void invokeInitMethods(final Object target, final String beanName) throws Throwable {
            final Collection<LifecycleElement> initMethodsToIterate = (this.checkedInitMethods != null) ? this.checkedInitMethods : this.initMethods;
            if (!initMethodsToIterate.isEmpty()) {
                final boolean debug = InitDestroyAnnotationBeanPostProcessor.this.logger.isDebugEnabled();
                for (final LifecycleElement element : initMethodsToIterate) {
                    if (debug) {
                        InitDestroyAnnotationBeanPostProcessor.this.logger.debug("Invoking init method on bean '" + beanName + "': " + element.getMethod());
                    }
                    element.invoke(target);
                }
            }
        }
        
        public void invokeDestroyMethods(final Object target, final String beanName) throws Throwable {
            final Collection<LifecycleElement> destroyMethodsToIterate = (this.checkedDestroyMethods != null) ? this.checkedDestroyMethods : this.destroyMethods;
            if (!destroyMethodsToIterate.isEmpty()) {
                final boolean debug = InitDestroyAnnotationBeanPostProcessor.this.logger.isDebugEnabled();
                for (final LifecycleElement element : destroyMethodsToIterate) {
                    if (debug) {
                        InitDestroyAnnotationBeanPostProcessor.this.logger.debug("Invoking destroy method on bean '" + beanName + "': " + element.getMethod());
                    }
                    element.invoke(target);
                }
            }
        }
    }
    
    private static class LifecycleElement
    {
        private final Method method;
        private final String identifier;
        
        public LifecycleElement(final Method method) {
            if (method.getParameterTypes().length != 0) {
                throw new IllegalStateException("Lifecycle method annotation requires a no-arg method: " + method);
            }
            this.method = method;
            this.identifier = (Modifier.isPrivate(method.getModifiers()) ? (method.getDeclaringClass() + "." + method.getName()) : method.getName());
        }
        
        public Method getMethod() {
            return this.method;
        }
        
        public String getIdentifier() {
            return this.identifier;
        }
        
        public void invoke(final Object target) throws Throwable {
            ReflectionUtils.makeAccessible(this.method);
            this.method.invoke(target, (Object[])null);
        }
        
        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof LifecycleElement)) {
                return false;
            }
            final LifecycleElement otherElement = (LifecycleElement)other;
            return this.identifier.equals(otherElement.identifier);
        }
        
        @Override
        public int hashCode() {
            return this.identifier.hashCode();
        }
    }
}
