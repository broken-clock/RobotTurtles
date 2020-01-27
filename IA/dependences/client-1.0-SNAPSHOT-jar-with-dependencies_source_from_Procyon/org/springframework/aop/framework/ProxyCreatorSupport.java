// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.framework;

import java.util.Iterator;
import org.springframework.util.Assert;
import java.util.LinkedList;
import java.util.List;

public class ProxyCreatorSupport extends AdvisedSupport
{
    private AopProxyFactory aopProxyFactory;
    private List<AdvisedSupportListener> listeners;
    private boolean active;
    
    public ProxyCreatorSupport() {
        this.listeners = new LinkedList<AdvisedSupportListener>();
        this.active = false;
        this.aopProxyFactory = new DefaultAopProxyFactory();
    }
    
    public ProxyCreatorSupport(final AopProxyFactory aopProxyFactory) {
        this.listeners = new LinkedList<AdvisedSupportListener>();
        this.active = false;
        Assert.notNull(aopProxyFactory, "AopProxyFactory must not be null");
        this.aopProxyFactory = aopProxyFactory;
    }
    
    public void setAopProxyFactory(final AopProxyFactory aopProxyFactory) {
        Assert.notNull(aopProxyFactory, "AopProxyFactory must not be null");
        this.aopProxyFactory = aopProxyFactory;
    }
    
    public AopProxyFactory getAopProxyFactory() {
        return this.aopProxyFactory;
    }
    
    public void addListener(final AdvisedSupportListener listener) {
        Assert.notNull(listener, "AdvisedSupportListener must not be null");
        this.listeners.add(listener);
    }
    
    public void removeListener(final AdvisedSupportListener listener) {
        Assert.notNull(listener, "AdvisedSupportListener must not be null");
        this.listeners.remove(listener);
    }
    
    protected final synchronized AopProxy createAopProxy() {
        if (!this.active) {
            this.activate();
        }
        return this.getAopProxyFactory().createAopProxy(this);
    }
    
    private void activate() {
        this.active = true;
        for (final AdvisedSupportListener listener : this.listeners) {
            listener.activated(this);
        }
    }
    
    @Override
    protected void adviceChanged() {
        super.adviceChanged();
        synchronized (this) {
            if (this.active) {
                for (final AdvisedSupportListener listener : this.listeners) {
                    listener.adviceChanged(this);
                }
            }
        }
    }
    
    protected final synchronized boolean isActive() {
        return this.active;
    }
}
