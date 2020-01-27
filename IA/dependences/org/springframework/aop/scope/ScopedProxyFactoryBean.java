// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.scope;

import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.aopalliance.aop.Advice;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.springframework.util.ClassUtils;
import java.lang.reflect.Modifier;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.aop.target.SimpleBeanTargetSource;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.aop.framework.ProxyConfig;

public class ScopedProxyFactoryBean extends ProxyConfig implements FactoryBean<Object>, BeanFactoryAware
{
    private final SimpleBeanTargetSource scopedTargetSource;
    private String targetBeanName;
    private Object proxy;
    
    public ScopedProxyFactoryBean() {
        this.scopedTargetSource = new SimpleBeanTargetSource();
        this.setProxyTargetClass(true);
    }
    
    public void setTargetBeanName(final String targetBeanName) {
        this.targetBeanName = targetBeanName;
        this.scopedTargetSource.setTargetBeanName(targetBeanName);
    }
    
    @Override
    public void setBeanFactory(final BeanFactory beanFactory) {
        if (!(beanFactory instanceof ConfigurableBeanFactory)) {
            throw new IllegalStateException("Not running in a ConfigurableBeanFactory: " + beanFactory);
        }
        final ConfigurableBeanFactory cbf = (ConfigurableBeanFactory)beanFactory;
        this.scopedTargetSource.setBeanFactory(beanFactory);
        final ProxyFactory pf = new ProxyFactory();
        pf.copyFrom(this);
        pf.setTargetSource(this.scopedTargetSource);
        final Class<?> beanType = beanFactory.getType(this.targetBeanName);
        if (beanType == null) {
            throw new IllegalStateException("Cannot create scoped proxy for bean '" + this.targetBeanName + "': Target type could not be determined at the time of proxy creation.");
        }
        if (!this.isProxyTargetClass() || beanType.isInterface() || Modifier.isPrivate(beanType.getModifiers())) {
            pf.setInterfaces(ClassUtils.getAllInterfacesForClass(beanType, cbf.getBeanClassLoader()));
        }
        final ScopedObject scopedObject = new DefaultScopedObject(cbf, this.scopedTargetSource.getTargetBeanName());
        pf.addAdvice(new DelegatingIntroductionInterceptor(scopedObject));
        pf.addInterface(AopInfrastructureBean.class);
        this.proxy = pf.getProxy(cbf.getBeanClassLoader());
    }
    
    @Override
    public Object getObject() {
        if (this.proxy == null) {
            throw new FactoryBeanNotInitializedException();
        }
        return this.proxy;
    }
    
    @Override
    public Class<?> getObjectType() {
        if (this.proxy != null) {
            return this.proxy.getClass();
        }
        if (this.scopedTargetSource != null) {
            return this.scopedTargetSource.getTargetClass();
        }
        return null;
    }
    
    @Override
    public boolean isSingleton() {
        return true;
    }
}
