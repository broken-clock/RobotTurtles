// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.annotation;

import org.springframework.beans.MutablePropertyValues;
import java.lang.reflect.InvocationTargetException;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.beans.PropertyDescriptor;
import org.springframework.beans.PropertyValues;
import java.lang.reflect.Member;
import java.util.Iterator;
import java.util.LinkedHashSet;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.apache.commons.logging.LogFactory;
import java.util.Set;
import java.util.Collection;
import org.apache.commons.logging.Log;

public class InjectionMetadata
{
    private final Log logger;
    private final Class<?> targetClass;
    private final Collection<InjectedElement> injectedElements;
    private volatile Set<InjectedElement> checkedElements;
    
    public InjectionMetadata(final Class<?> targetClass, final Collection<InjectedElement> elements) {
        this.logger = LogFactory.getLog(InjectionMetadata.class);
        this.targetClass = targetClass;
        this.injectedElements = elements;
    }
    
    public void checkConfigMembers(final RootBeanDefinition beanDefinition) {
        final Set<InjectedElement> checkedElements = new LinkedHashSet<InjectedElement>(this.injectedElements.size());
        for (final InjectedElement element : this.injectedElements) {
            final Member member = element.getMember();
            if (!beanDefinition.isExternallyManagedConfigMember(member)) {
                beanDefinition.registerExternallyManagedConfigMember(member);
                checkedElements.add(element);
                if (!this.logger.isDebugEnabled()) {
                    continue;
                }
                this.logger.debug("Registered injected element on class [" + this.targetClass.getName() + "]: " + element);
            }
        }
        this.checkedElements = checkedElements;
    }
    
    public void inject(final Object target, final String beanName, final PropertyValues pvs) throws Throwable {
        final Collection<InjectedElement> elementsToIterate = (this.checkedElements != null) ? this.checkedElements : this.injectedElements;
        if (!elementsToIterate.isEmpty()) {
            final boolean debug = this.logger.isDebugEnabled();
            for (final InjectedElement element : elementsToIterate) {
                if (debug) {
                    this.logger.debug("Processing injected method of bean '" + beanName + "': " + element);
                }
                element.inject(target, beanName, pvs);
            }
        }
    }
    
    public static boolean needsRefresh(final InjectionMetadata metadata, final Class<?> clazz) {
        return metadata == null || !metadata.targetClass.equals(clazz);
    }
    
    public abstract static class InjectedElement
    {
        protected final Member member;
        protected final boolean isField;
        protected final PropertyDescriptor pd;
        protected volatile Boolean skip;
        
        protected InjectedElement(final Member member, final PropertyDescriptor pd) {
            this.member = member;
            this.isField = (member instanceof Field);
            this.pd = pd;
        }
        
        public final Member getMember() {
            return this.member;
        }
        
        protected final Class<?> getResourceType() {
            if (this.isField) {
                return ((Field)this.member).getType();
            }
            if (this.pd != null) {
                return this.pd.getPropertyType();
            }
            return ((Method)this.member).getParameterTypes()[0];
        }
        
        protected final void checkResourceType(final Class<?> resourceType) {
            if (this.isField) {
                final Class<?> fieldType = ((Field)this.member).getType();
                if (!resourceType.isAssignableFrom(fieldType) && !fieldType.isAssignableFrom(resourceType)) {
                    throw new IllegalStateException("Specified field type [" + fieldType + "] is incompatible with resource type [" + resourceType.getName() + "]");
                }
            }
            else {
                final Class<?> paramType = (this.pd != null) ? this.pd.getPropertyType() : ((Method)this.member).getParameterTypes()[0];
                if (!resourceType.isAssignableFrom(paramType) && !paramType.isAssignableFrom(resourceType)) {
                    throw new IllegalStateException("Specified parameter type [" + paramType + "] is incompatible with resource type [" + resourceType.getName() + "]");
                }
            }
        }
        
        protected void inject(final Object target, final String requestingBeanName, final PropertyValues pvs) throws Throwable {
            if (this.isField) {
                final Field field = (Field)this.member;
                ReflectionUtils.makeAccessible(field);
                field.set(target, this.getResourceToInject(target, requestingBeanName));
            }
            else {
                if (this.checkPropertySkipping(pvs)) {
                    return;
                }
                try {
                    final Method method = (Method)this.member;
                    ReflectionUtils.makeAccessible(method);
                    method.invoke(target, this.getResourceToInject(target, requestingBeanName));
                }
                catch (InvocationTargetException ex) {
                    throw ex.getTargetException();
                }
            }
        }
        
        protected boolean checkPropertySkipping(final PropertyValues pvs) {
            if (this.skip != null) {
                return this.skip;
            }
            if (pvs == null) {
                this.skip = false;
                return false;
            }
            synchronized (pvs) {
                if (this.skip != null) {
                    return this.skip;
                }
                if (this.pd != null) {
                    if (pvs.contains(this.pd.getName())) {
                        this.skip = true;
                        return true;
                    }
                    if (pvs instanceof MutablePropertyValues) {
                        ((MutablePropertyValues)pvs).registerProcessedProperty(this.pd.getName());
                    }
                }
                this.skip = false;
                return false;
            }
        }
        
        protected Object getResourceToInject(final Object target, final String requestingBeanName) {
            return null;
        }
        
        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof InjectedElement)) {
                return false;
            }
            final InjectedElement otherElement = (InjectedElement)other;
            return this.member.equals(otherElement.member);
        }
        
        @Override
        public int hashCode() {
            return this.member.getClass().hashCode() * 29 + this.member.getName().hashCode();
        }
        
        @Override
        public String toString() {
            return this.getClass().getSimpleName() + " for " + this.member;
        }
    }
}
