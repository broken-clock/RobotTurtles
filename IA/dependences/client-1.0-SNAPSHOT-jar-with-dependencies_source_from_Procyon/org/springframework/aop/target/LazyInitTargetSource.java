// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.target;

import org.springframework.beans.BeansException;

public class LazyInitTargetSource extends AbstractBeanFactoryBasedTargetSource
{
    private Object target;
    
    @Override
    public synchronized Object getTarget() throws BeansException {
        if (this.target == null) {
            this.postProcessTargetObject(this.target = this.getBeanFactory().getBean(this.getTargetBeanName()));
        }
        return this.target;
    }
    
    protected void postProcessTargetObject(final Object targetObject) {
    }
}
