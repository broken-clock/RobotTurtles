// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.support;

import org.springframework.beans.factory.InitializingBean;

public abstract class UrlBasedRemoteAccessor extends RemoteAccessor implements InitializingBean
{
    private String serviceUrl;
    
    public void setServiceUrl(final String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }
    
    public String getServiceUrl() {
        return this.serviceUrl;
    }
    
    @Override
    public void afterPropertiesSet() {
        if (this.getServiceUrl() == null) {
            throw new IllegalArgumentException("Property 'serviceUrl' is required");
        }
    }
}
