// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.client.support;

import java.net.SocketAddress;
import java.net.InetSocketAddress;
import org.springframework.util.Assert;
import org.springframework.beans.factory.InitializingBean;
import java.net.Proxy;
import org.springframework.beans.factory.FactoryBean;

public class ProxyFactoryBean implements FactoryBean<Proxy>, InitializingBean
{
    private Proxy.Type type;
    private String hostname;
    private int port;
    private Proxy proxy;
    
    public ProxyFactoryBean() {
        this.type = Proxy.Type.HTTP;
        this.port = -1;
    }
    
    public void setType(final Proxy.Type type) {
        this.type = type;
    }
    
    public void setHostname(final String hostname) {
        this.hostname = hostname;
    }
    
    public void setPort(final int port) {
        this.port = port;
    }
    
    @Override
    public void afterPropertiesSet() throws IllegalArgumentException {
        Assert.notNull(this.type, "'type' must not be null");
        Assert.hasLength(this.hostname, "'hostname' must not be empty");
        Assert.isTrue(this.port >= 0 && this.port <= 65535, "'port' out of range: " + this.port);
        final SocketAddress socketAddress = new InetSocketAddress(this.hostname, this.port);
        this.proxy = new Proxy(this.type, socketAddress);
    }
    
    @Override
    public Proxy getObject() {
        return this.proxy;
    }
    
    @Override
    public Class<?> getObjectType() {
        return Proxy.class;
    }
    
    @Override
    public boolean isSingleton() {
        return true;
    }
}
