// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.framework;

import org.springframework.aop.support.AopUtils;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.util.ClassUtils;
import java.util.Map;
import org.springframework.aop.Advisor;
import org.springframework.core.Ordered;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanPostProcessor;

public abstract class AbstractAdvisingBeanPostProcessor extends ProxyConfig implements BeanPostProcessor, BeanClassLoaderAware, Ordered
{
    protected Advisor advisor;
    protected boolean beforeExistingAdvisors;
    private ClassLoader beanClassLoader;
    private int order;
    private final Map<Class<?>, Boolean> eligibleBeans;
    
    public AbstractAdvisingBeanPostProcessor() {
        this.beforeExistingAdvisors = false;
        this.beanClassLoader = ClassUtils.getDefaultClassLoader();
        this.order = Integer.MAX_VALUE;
        this.eligibleBeans = new ConcurrentHashMap<Class<?>, Boolean>(64);
    }
    
    public void setBeforeExistingAdvisors(final boolean beforeExistingAdvisors) {
        this.beforeExistingAdvisors = beforeExistingAdvisors;
    }
    
    @Override
    public void setBeanClassLoader(final ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }
    
    public void setOrder(final int order) {
        this.order = order;
    }
    
    @Override
    public int getOrder() {
        return this.order;
    }
    
    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) {
        return bean;
    }
    
    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) {
        if (bean instanceof AopInfrastructureBean) {
            return bean;
        }
        if (!this.isEligible(bean, beanName)) {
            return bean;
        }
        if (bean instanceof Advised) {
            final Advised advised = (Advised)bean;
            if (this.beforeExistingAdvisors) {
                advised.addAdvisor(0, this.advisor);
            }
            else {
                advised.addAdvisor(this.advisor);
            }
            return bean;
        }
        final ProxyFactory proxyFactory = new ProxyFactory(bean);
        proxyFactory.copyFrom(this);
        proxyFactory.addAdvisor(this.advisor);
        return proxyFactory.getProxy(this.beanClassLoader);
    }
    
    protected boolean isEligible(final Object bean, final String beanName) {
        final Class<?> targetClass = AopUtils.getTargetClass(bean);
        Boolean eligible = this.eligibleBeans.get(targetClass);
        if (eligible != null) {
            return eligible;
        }
        eligible = AopUtils.canApply(this.advisor, targetClass);
        this.eligibleBeans.put(targetClass, eligible);
        return eligible;
    }
}
