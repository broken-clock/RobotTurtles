// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.interceptor;

import org.springframework.beans.factory.NamedBean;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.aopalliance.aop.Advice;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.Advisor;
import org.springframework.aop.ProxyMethodInvocation;
import org.aopalliance.intercept.MethodInvocation;

public abstract class ExposeBeanNameAdvisors
{
    private static final String BEAN_NAME_ATTRIBUTE;
    
    public static String getBeanName() throws IllegalStateException {
        return getBeanName(ExposeInvocationInterceptor.currentInvocation());
    }
    
    public static String getBeanName(final MethodInvocation mi) throws IllegalStateException {
        if (!(mi instanceof ProxyMethodInvocation)) {
            throw new IllegalArgumentException("MethodInvocation is not a Spring ProxyMethodInvocation: " + mi);
        }
        final ProxyMethodInvocation pmi = (ProxyMethodInvocation)mi;
        final String beanName = (String)pmi.getUserAttribute(ExposeBeanNameAdvisors.BEAN_NAME_ATTRIBUTE);
        if (beanName == null) {
            throw new IllegalStateException("Cannot get bean name; not set on MethodInvocation: " + mi);
        }
        return beanName;
    }
    
    public static Advisor createAdvisorWithoutIntroduction(final String beanName) {
        return new DefaultPointcutAdvisor(new ExposeBeanNameInterceptor(beanName));
    }
    
    public static Advisor createAdvisorIntroducingNamedBean(final String beanName) {
        return new DefaultIntroductionAdvisor(new ExposeBeanNameIntroduction(beanName));
    }
    
    static {
        BEAN_NAME_ATTRIBUTE = ExposeBeanNameAdvisors.class.getName() + ".BEAN_NAME";
    }
    
    private static class ExposeBeanNameInterceptor implements MethodInterceptor
    {
        private final String beanName;
        
        public ExposeBeanNameInterceptor(final String beanName) {
            this.beanName = beanName;
        }
        
        @Override
        public Object invoke(final MethodInvocation mi) throws Throwable {
            if (!(mi instanceof ProxyMethodInvocation)) {
                throw new IllegalStateException("MethodInvocation is not a Spring ProxyMethodInvocation: " + mi);
            }
            final ProxyMethodInvocation pmi = (ProxyMethodInvocation)mi;
            pmi.setUserAttribute(ExposeBeanNameAdvisors.BEAN_NAME_ATTRIBUTE, this.beanName);
            return mi.proceed();
        }
    }
    
    private static class ExposeBeanNameIntroduction extends DelegatingIntroductionInterceptor implements NamedBean
    {
        private final String beanName;
        
        public ExposeBeanNameIntroduction(final String beanName) {
            this.beanName = beanName;
        }
        
        @Override
        public Object invoke(final MethodInvocation mi) throws Throwable {
            if (!(mi instanceof ProxyMethodInvocation)) {
                throw new IllegalStateException("MethodInvocation is not a Spring ProxyMethodInvocation: " + mi);
            }
            final ProxyMethodInvocation pmi = (ProxyMethodInvocation)mi;
            pmi.setUserAttribute(ExposeBeanNameAdvisors.BEAN_NAME_ATTRIBUTE, this.beanName);
            return super.invoke(mi);
        }
        
        @Override
        public String getBeanName() {
            return this.beanName;
        }
    }
}
