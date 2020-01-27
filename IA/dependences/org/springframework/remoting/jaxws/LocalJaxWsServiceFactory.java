// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.jaxws;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.io.IOException;
import org.springframework.util.Assert;
import org.springframework.core.io.Resource;
import javax.xml.ws.handler.HandlerResolver;
import java.util.concurrent.Executor;
import javax.xml.ws.WebServiceFeature;
import java.net.URL;

public class LocalJaxWsServiceFactory
{
    private URL wsdlDocumentUrl;
    private String namespaceUri;
    private String serviceName;
    private WebServiceFeature[] serviceFeatures;
    private Executor executor;
    private HandlerResolver handlerResolver;
    
    public void setWsdlDocumentUrl(final URL wsdlDocumentUrl) {
        this.wsdlDocumentUrl = wsdlDocumentUrl;
    }
    
    public void setWsdlDocumentResource(final Resource wsdlDocumentResource) throws IOException {
        Assert.notNull(wsdlDocumentResource, "WSDL Resource must not be null.");
        this.wsdlDocumentUrl = wsdlDocumentResource.getURL();
    }
    
    public URL getWsdlDocumentUrl() {
        return this.wsdlDocumentUrl;
    }
    
    public void setNamespaceUri(final String namespaceUri) {
        this.namespaceUri = ((namespaceUri != null) ? namespaceUri.trim() : null);
    }
    
    public String getNamespaceUri() {
        return this.namespaceUri;
    }
    
    public void setServiceName(final String serviceName) {
        this.serviceName = serviceName;
    }
    
    public String getServiceName() {
        return this.serviceName;
    }
    
    public void setServiceFeatures(final WebServiceFeature... serviceFeatures) {
        this.serviceFeatures = serviceFeatures;
    }
    
    public void setExecutor(final Executor executor) {
        this.executor = executor;
    }
    
    public void setHandlerResolver(final HandlerResolver handlerResolver) {
        this.handlerResolver = handlerResolver;
    }
    
    public Service createJaxWsService() {
        Assert.notNull(this.serviceName, "No service name specified");
        Service service;
        if (this.serviceFeatures != null) {
            service = ((this.wsdlDocumentUrl != null) ? Service.create(this.wsdlDocumentUrl, this.getQName(this.serviceName), this.serviceFeatures) : Service.create(this.getQName(this.serviceName), this.serviceFeatures));
        }
        else {
            service = ((this.wsdlDocumentUrl != null) ? Service.create(this.wsdlDocumentUrl, this.getQName(this.serviceName)) : Service.create(this.getQName(this.serviceName)));
        }
        if (this.executor != null) {
            service.setExecutor(this.executor);
        }
        if (this.handlerResolver != null) {
            service.setHandlerResolver(this.handlerResolver);
        }
        return service;
    }
    
    protected QName getQName(final String name) {
        return (this.getNamespaceUri() != null) ? new QName(this.getNamespaceUri(), name) : new QName(name);
    }
}
