// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.target;

import org.springframework.beans.BeansException;

public class PrototypeTargetSource extends AbstractPrototypeBasedTargetSource
{
    @Override
    public Object getTarget() throws BeansException {
        return this.newPrototypeInstance();
    }
    
    @Override
    public void releaseTarget(final Object target) {
        this.destroyPrototypeInstance(target);
    }
    
    @Override
    public String toString() {
        return "PrototypeTargetSource for target bean with name '" + this.getTargetBeanName() + "'";
    }
}
