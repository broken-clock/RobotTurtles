// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.target;

import org.springframework.aop.DynamicIntroductionAdvice;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.DisposableBean;

public abstract class AbstractPoolingTargetSource extends AbstractPrototypeBasedTargetSource implements PoolingConfig, DisposableBean
{
    private int maxSize;
    
    public AbstractPoolingTargetSource() {
        this.maxSize = -1;
    }
    
    public void setMaxSize(final int maxSize) {
        this.maxSize = maxSize;
    }
    
    @Override
    public int getMaxSize() {
        return this.maxSize;
    }
    
    @Override
    public final void setBeanFactory(final BeanFactory beanFactory) throws BeansException {
        super.setBeanFactory(beanFactory);
        try {
            this.createPool();
        }
        catch (Throwable ex) {
            throw new BeanInitializationException("Could not create instance pool for TargetSource", ex);
        }
    }
    
    protected abstract void createPool() throws Exception;
    
    @Override
    public abstract Object getTarget() throws Exception;
    
    @Override
    public abstract void releaseTarget(final Object p0) throws Exception;
    
    public DefaultIntroductionAdvisor getPoolingConfigMixin() {
        final DelegatingIntroductionInterceptor dii = new DelegatingIntroductionInterceptor(this);
        return new DefaultIntroductionAdvisor(dii, PoolingConfig.class);
    }
}
