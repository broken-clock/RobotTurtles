// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;
import java.util.Iterator;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import java.util.Map;
import org.springframework.core.Ordered;
import org.springframework.beans.factory.BeanClassLoaderAware;

public class CustomScopeConfigurer implements BeanFactoryPostProcessor, BeanClassLoaderAware, Ordered
{
    private Map<String, Object> scopes;
    private int order;
    private ClassLoader beanClassLoader;
    
    public CustomScopeConfigurer() {
        this.order = Integer.MAX_VALUE;
        this.beanClassLoader = ClassUtils.getDefaultClassLoader();
    }
    
    public void setScopes(final Map<String, Object> scopes) {
        this.scopes = scopes;
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
    
    @Override
    public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (this.scopes != null) {
            for (final Map.Entry<String, Object> entry : this.scopes.entrySet()) {
                final String scopeKey = entry.getKey();
                final Object value = entry.getValue();
                if (value instanceof Scope) {
                    beanFactory.registerScope(scopeKey, (Scope)value);
                }
                else if (value instanceof Class) {
                    final Class<?> scopeClass = (Class<?>)value;
                    Assert.isAssignable(Scope.class, scopeClass);
                    beanFactory.registerScope(scopeKey, BeanUtils.instantiateClass(scopeClass));
                }
                else {
                    if (!(value instanceof String)) {
                        throw new IllegalArgumentException("Mapped value [" + value + "] for scope key [" + scopeKey + "] is not an instance of required type [" + Scope.class.getName() + "] or a corresponding Class or String value indicating a Scope implementation");
                    }
                    final Class<?> scopeClass = ClassUtils.resolveClassName((String)value, this.beanClassLoader);
                    Assert.isAssignable(Scope.class, scopeClass);
                    beanFactory.registerScope(scopeKey, BeanUtils.instantiateClass(scopeClass));
                }
            }
        }
    }
}
