// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.target;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.springframework.aop.TargetSource;

public abstract class AbstractLazyCreationTargetSource implements TargetSource
{
    protected final Log logger;
    private Object lazyTarget;
    
    public AbstractLazyCreationTargetSource() {
        this.logger = LogFactory.getLog(this.getClass());
    }
    
    public synchronized boolean isInitialized() {
        return this.lazyTarget != null;
    }
    
    @Override
    public synchronized Class<?> getTargetClass() {
        return (this.lazyTarget != null) ? this.lazyTarget.getClass() : null;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public synchronized Object getTarget() throws Exception {
        if (this.lazyTarget == null) {
            this.logger.debug("Initializing lazy target object");
            this.lazyTarget = this.createObject();
        }
        return this.lazyTarget;
    }
    
    @Override
    public void releaseTarget(final Object target) throws Exception {
    }
    
    protected abstract Object createObject() throws Exception;
}
