// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.target;

import org.springframework.aop.DynamicIntroductionAdvice;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.springframework.aop.IntroductionAdvisor;
import java.util.Iterator;
import org.springframework.beans.BeansException;
import java.util.HashSet;
import org.springframework.core.NamedThreadLocal;
import java.util.Set;
import org.springframework.beans.factory.DisposableBean;

public class ThreadLocalTargetSource extends AbstractPrototypeBasedTargetSource implements ThreadLocalTargetSourceStats, DisposableBean
{
    private final ThreadLocal<Object> targetInThread;
    private final Set<Object> targetSet;
    private int invocationCount;
    private int hitCount;
    
    public ThreadLocalTargetSource() {
        this.targetInThread = new NamedThreadLocal<Object>("Thread-local instance of bean '" + this.getTargetBeanName() + "'");
        this.targetSet = new HashSet<Object>();
    }
    
    @Override
    public Object getTarget() throws BeansException {
        ++this.invocationCount;
        Object target = this.targetInThread.get();
        if (target == null) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("No target for prototype '" + this.getTargetBeanName() + "' bound to thread: " + "creating one and binding it to thread '" + Thread.currentThread().getName() + "'");
            }
            target = this.newPrototypeInstance();
            this.targetInThread.set(target);
            synchronized (this.targetSet) {
                this.targetSet.add(target);
            }
        }
        else {
            ++this.hitCount;
        }
        return target;
    }
    
    @Override
    public void destroy() {
        this.logger.debug("Destroying ThreadLocalTargetSource bindings");
        synchronized (this.targetSet) {
            for (final Object target : this.targetSet) {
                this.destroyPrototypeInstance(target);
            }
            this.targetSet.clear();
        }
        this.targetInThread.remove();
    }
    
    @Override
    public int getInvocationCount() {
        return this.invocationCount;
    }
    
    @Override
    public int getHitCount() {
        return this.hitCount;
    }
    
    @Override
    public int getObjectCount() {
        synchronized (this.targetSet) {
            return this.targetSet.size();
        }
    }
    
    public IntroductionAdvisor getStatsMixin() {
        final DelegatingIntroductionInterceptor dii = new DelegatingIntroductionInterceptor(this);
        return new DefaultIntroductionAdvisor(dii, ThreadLocalTargetSourceStats.class);
    }
}
