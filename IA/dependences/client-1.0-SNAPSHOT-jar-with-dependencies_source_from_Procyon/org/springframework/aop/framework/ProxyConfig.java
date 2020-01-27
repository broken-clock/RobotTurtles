// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.framework;

import org.springframework.util.Assert;
import java.io.Serializable;

public class ProxyConfig implements Serializable
{
    private static final long serialVersionUID = -8409359707199703185L;
    private boolean proxyTargetClass;
    private boolean optimize;
    boolean opaque;
    boolean exposeProxy;
    private boolean frozen;
    
    public ProxyConfig() {
        this.proxyTargetClass = false;
        this.optimize = false;
        this.opaque = false;
        this.exposeProxy = false;
        this.frozen = false;
    }
    
    public void setProxyTargetClass(final boolean proxyTargetClass) {
        this.proxyTargetClass = proxyTargetClass;
    }
    
    public boolean isProxyTargetClass() {
        return this.proxyTargetClass;
    }
    
    public void setOptimize(final boolean optimize) {
        this.optimize = optimize;
    }
    
    public boolean isOptimize() {
        return this.optimize;
    }
    
    public void setOpaque(final boolean opaque) {
        this.opaque = opaque;
    }
    
    public boolean isOpaque() {
        return this.opaque;
    }
    
    public void setExposeProxy(final boolean exposeProxy) {
        this.exposeProxy = exposeProxy;
    }
    
    public boolean isExposeProxy() {
        return this.exposeProxy;
    }
    
    public void setFrozen(final boolean frozen) {
        this.frozen = frozen;
    }
    
    public boolean isFrozen() {
        return this.frozen;
    }
    
    public void copyFrom(final ProxyConfig other) {
        Assert.notNull(other, "Other ProxyConfig object must not be null");
        this.proxyTargetClass = other.proxyTargetClass;
        this.optimize = other.optimize;
        this.exposeProxy = other.exposeProxy;
        this.frozen = other.frozen;
        this.opaque = other.opaque;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("proxyTargetClass=").append(this.proxyTargetClass).append("; ");
        sb.append("optimize=").append(this.optimize).append("; ");
        sb.append("opaque=").append(this.opaque).append("; ");
        sb.append("exposeProxy=").append(this.exposeProxy).append("; ");
        sb.append("frozen=").append(this.frozen);
        return sb.toString();
    }
}
