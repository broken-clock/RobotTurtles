// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.apache.commons.logging.LogFactory;
import java.lang.reflect.Method;
import org.apache.commons.logging.Log;
import org.springframework.cglib.proxy.CallbackFilter;
import org.springframework.cglib.core.NamingPolicy;
import org.springframework.cglib.core.SpringNamingPolicy;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.NoOp;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Factory;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import java.lang.reflect.Constructor;
import org.springframework.beans.factory.BeanFactory;

public class CglibSubclassingInstantiationStrategy extends SimpleInstantiationStrategy
{
    private static final int PASSTHROUGH = 0;
    private static final int LOOKUP_OVERRIDE = 1;
    private static final int METHOD_REPLACER = 2;
    
    @Override
    protected Object instantiateWithMethodInjection(final RootBeanDefinition beanDefinition, final String beanName, final BeanFactory owner) {
        return this.instantiateWithMethodInjection(beanDefinition, beanName, owner, null, null);
    }
    
    @Override
    protected Object instantiateWithMethodInjection(final RootBeanDefinition beanDefinition, final String beanName, final BeanFactory owner, final Constructor<?> ctor, final Object[] args) {
        return new CglibSubclassCreator(beanDefinition, owner).instantiate(ctor, args);
    }
    
    private static class CglibSubclassCreator
    {
        private static final Class<?>[] CALLBACK_TYPES;
        private final RootBeanDefinition beanDefinition;
        private final BeanFactory owner;
        
        CglibSubclassCreator(final RootBeanDefinition beanDefinition, final BeanFactory owner) {
            this.beanDefinition = beanDefinition;
            this.owner = owner;
        }
        
        Object instantiate(final Constructor<?> ctor, final Object[] args) {
            final Class<?> subclass = this.createEnhancedSubclass(this.beanDefinition);
            Object instance;
            if (ctor == null) {
                instance = BeanUtils.instantiate(subclass);
            }
            else {
                try {
                    final Constructor<?> enhancedSubclassConstructor = subclass.getConstructor(ctor.getParameterTypes());
                    instance = enhancedSubclassConstructor.newInstance(args);
                }
                catch (Exception e) {
                    throw new BeanInstantiationException(this.beanDefinition.getBeanClass(), String.format("Failed to invoke construcor for CGLIB enhanced subclass [%s]", subclass.getName()), e);
                }
            }
            final Factory factory = (Factory)instance;
            factory.setCallbacks(new Callback[] { NoOp.INSTANCE, new LookupOverrideMethodInterceptor(this.beanDefinition, this.owner), new ReplaceOverrideMethodInterceptor(this.beanDefinition, this.owner) });
            return instance;
        }
        
        private Class<?> createEnhancedSubclass(final RootBeanDefinition beanDefinition) {
            final Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(beanDefinition.getBeanClass());
            enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
            enhancer.setCallbackFilter(new MethodOverrideCallbackFilter(beanDefinition));
            enhancer.setCallbackTypes(CglibSubclassCreator.CALLBACK_TYPES);
            return (Class<?>)enhancer.createClass();
        }
        
        static {
            CALLBACK_TYPES = new Class[] { NoOp.class, LookupOverrideMethodInterceptor.class, ReplaceOverrideMethodInterceptor.class };
        }
    }
    
    private static class CglibIdentitySupport
    {
        private final RootBeanDefinition beanDefinition;
        
        CglibIdentitySupport(final RootBeanDefinition beanDefinition) {
            this.beanDefinition = beanDefinition;
        }
        
        RootBeanDefinition getBeanDefinition() {
            return this.beanDefinition;
        }
        
        @Override
        public boolean equals(final Object other) {
            return other.getClass().equals(this.getClass()) && ((CglibIdentitySupport)other).getBeanDefinition().equals(this.getBeanDefinition());
        }
        
        @Override
        public int hashCode() {
            return this.beanDefinition.hashCode();
        }
    }
    
    private static class MethodOverrideCallbackFilter extends CglibIdentitySupport implements CallbackFilter
    {
        private static final Log logger;
        
        MethodOverrideCallbackFilter(final RootBeanDefinition beanDefinition) {
            super(beanDefinition);
        }
        
        @Override
        public int accept(final Method method) {
            final MethodOverride methodOverride = this.getBeanDefinition().getMethodOverrides().getOverride(method);
            if (MethodOverrideCallbackFilter.logger.isTraceEnabled()) {
                MethodOverrideCallbackFilter.logger.trace("Override for '" + method.getName() + "' is [" + methodOverride + "]");
            }
            if (methodOverride == null) {
                return 0;
            }
            if (methodOverride instanceof LookupOverride) {
                return 1;
            }
            if (methodOverride instanceof ReplaceOverride) {
                return 2;
            }
            throw new UnsupportedOperationException("Unexpected MethodOverride subclass: " + methodOverride.getClass().getName());
        }
        
        static {
            logger = LogFactory.getLog(MethodOverrideCallbackFilter.class);
        }
    }
    
    private static class LookupOverrideMethodInterceptor extends CglibIdentitySupport implements MethodInterceptor
    {
        private final BeanFactory owner;
        
        LookupOverrideMethodInterceptor(final RootBeanDefinition beanDefinition, final BeanFactory owner) {
            super(beanDefinition);
            this.owner = owner;
        }
        
        @Override
        public Object intercept(final Object obj, final Method method, final Object[] args, final MethodProxy mp) throws Throwable {
            final LookupOverride lo = (LookupOverride)this.getBeanDefinition().getMethodOverrides().getOverride(method);
            return this.owner.getBean(lo.getBeanName());
        }
    }
    
    private static class ReplaceOverrideMethodInterceptor extends CglibIdentitySupport implements MethodInterceptor
    {
        private final BeanFactory owner;
        
        ReplaceOverrideMethodInterceptor(final RootBeanDefinition beanDefinition, final BeanFactory owner) {
            super(beanDefinition);
            this.owner = owner;
        }
        
        @Override
        public Object intercept(final Object obj, final Method method, final Object[] args, final MethodProxy mp) throws Throwable {
            final ReplaceOverride ro = (ReplaceOverride)this.getBeanDefinition().getMethodOverrides().getOverride(method);
            final MethodReplacer mr = this.owner.getBean(ro.getMethodReplacerBeanName(), MethodReplacer.class);
            return mr.reimplement(obj, method, args);
        }
    }
}
