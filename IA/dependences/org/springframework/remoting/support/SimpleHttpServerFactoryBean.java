// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.support;

import java.io.IOException;
import com.sun.net.httpserver.HttpContext;
import java.util.Iterator;
import java.util.Collection;
import java.net.InetSocketAddress;
import org.apache.commons.logging.LogFactory;
import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.Filter;
import java.util.List;
import com.sun.net.httpserver.HttpHandler;
import java.util.Map;
import java.util.concurrent.Executor;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import com.sun.net.httpserver.HttpServer;
import org.springframework.beans.factory.FactoryBean;

public class SimpleHttpServerFactoryBean implements FactoryBean<HttpServer>, InitializingBean, DisposableBean
{
    protected final Log logger;
    private int port;
    private String hostname;
    private int backlog;
    private int shutdownDelay;
    private Executor executor;
    private Map<String, HttpHandler> contexts;
    private List<Filter> filters;
    private Authenticator authenticator;
    private HttpServer server;
    
    public SimpleHttpServerFactoryBean() {
        this.logger = LogFactory.getLog(this.getClass());
        this.port = 8080;
        this.backlog = -1;
        this.shutdownDelay = 0;
    }
    
    public void setPort(final int port) {
        this.port = port;
    }
    
    public void setHostname(final String hostname) {
        this.hostname = hostname;
    }
    
    public void setBacklog(final int backlog) {
        this.backlog = backlog;
    }
    
    public void setShutdownDelay(final int shutdownDelay) {
        this.shutdownDelay = shutdownDelay;
    }
    
    public void setExecutor(final Executor executor) {
        this.executor = executor;
    }
    
    public void setContexts(final Map<String, HttpHandler> contexts) {
        this.contexts = contexts;
    }
    
    public void setFilters(final List<Filter> filters) {
        this.filters = filters;
    }
    
    public void setAuthenticator(final Authenticator authenticator) {
        this.authenticator = authenticator;
    }
    
    @Override
    public void afterPropertiesSet() throws IOException {
        final InetSocketAddress address = (this.hostname != null) ? new InetSocketAddress(this.hostname, this.port) : new InetSocketAddress(this.port);
        this.server = HttpServer.create(address, this.backlog);
        if (this.executor != null) {
            this.server.setExecutor(this.executor);
        }
        if (this.contexts != null) {
            for (final String key : this.contexts.keySet()) {
                final HttpContext httpContext = this.server.createContext(key, this.contexts.get(key));
                if (this.filters != null) {
                    httpContext.getFilters().addAll(this.filters);
                }
                if (this.authenticator != null) {
                    httpContext.setAuthenticator(this.authenticator);
                }
            }
        }
        if (this.logger.isInfoEnabled()) {
            this.logger.info("Starting HttpServer at address " + address);
        }
        this.server.start();
    }
    
    @Override
    public HttpServer getObject() {
        return this.server;
    }
    
    @Override
    public Class<? extends HttpServer> getObjectType() {
        return (this.server != null) ? this.server.getClass() : HttpServer.class;
    }
    
    @Override
    public boolean isSingleton() {
        return true;
    }
    
    @Override
    public void destroy() {
        this.logger.info("Stopping HttpServer");
        this.server.stop(this.shutdownDelay);
    }
}
