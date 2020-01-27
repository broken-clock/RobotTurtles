// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.jaxws;

import java.util.Collection;
import com.sun.net.httpserver.HttpContext;
import javax.xml.ws.WebServiceProvider;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.net.InetSocketAddress;
import org.apache.commons.logging.LogFactory;
import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.Filter;
import java.util.List;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.logging.Log;

public class SimpleHttpServerJaxWsServiceExporter extends AbstractJaxWsServiceExporter
{
    protected final Log logger;
    private HttpServer server;
    private int port;
    private String hostname;
    private int backlog;
    private int shutdownDelay;
    private String basePath;
    private List<Filter> filters;
    private Authenticator authenticator;
    private boolean localServer;
    
    public SimpleHttpServerJaxWsServiceExporter() {
        this.logger = LogFactory.getLog(this.getClass());
        this.port = 8080;
        this.backlog = -1;
        this.shutdownDelay = 0;
        this.basePath = "/";
        this.localServer = false;
    }
    
    public void setServer(final HttpServer server) {
        this.server = server;
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
    
    public void setBasePath(final String basePath) {
        this.basePath = basePath;
    }
    
    public void setFilters(final List<Filter> filters) {
        this.filters = filters;
    }
    
    public void setAuthenticator(final Authenticator authenticator) {
        this.authenticator = authenticator;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.server == null) {
            final InetSocketAddress address = (this.hostname != null) ? new InetSocketAddress(this.hostname, this.port) : new InetSocketAddress(this.port);
            this.server = HttpServer.create(address, this.backlog);
            if (this.logger.isInfoEnabled()) {
                this.logger.info("Starting HttpServer at address " + address);
            }
            this.server.start();
            this.localServer = true;
        }
        super.afterPropertiesSet();
    }
    
    @Override
    protected void publishEndpoint(final Endpoint endpoint, final WebService annotation) {
        endpoint.publish((Object)this.buildHttpContext(endpoint, annotation.serviceName()));
    }
    
    @Override
    protected void publishEndpoint(final Endpoint endpoint, final WebServiceProvider annotation) {
        endpoint.publish((Object)this.buildHttpContext(endpoint, annotation.serviceName()));
    }
    
    protected HttpContext buildHttpContext(final Endpoint endpoint, final String serviceName) {
        final String fullPath = this.calculateEndpointPath(endpoint, serviceName);
        final HttpContext httpContext = this.server.createContext(fullPath);
        if (this.filters != null) {
            httpContext.getFilters().addAll(this.filters);
        }
        if (this.authenticator != null) {
            httpContext.setAuthenticator(this.authenticator);
        }
        return httpContext;
    }
    
    protected String calculateEndpointPath(final Endpoint endpoint, final String serviceName) {
        return this.basePath + serviceName;
    }
    
    @Override
    public void destroy() {
        super.destroy();
        if (this.localServer) {
            this.logger.info("Stopping HttpServer");
            this.server.stop(this.shutdownDelay);
        }
    }
}
