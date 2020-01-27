// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.jaxws;

import javax.xml.ws.WebServiceProvider;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;

public class SimpleJaxWsServiceExporter extends AbstractJaxWsServiceExporter
{
    public static final String DEFAULT_BASE_ADDRESS = "http://localhost:8080/";
    private String baseAddress;
    
    public SimpleJaxWsServiceExporter() {
        this.baseAddress = "http://localhost:8080/";
    }
    
    public void setBaseAddress(final String baseAddress) {
        this.baseAddress = baseAddress;
    }
    
    @Override
    protected void publishEndpoint(final Endpoint endpoint, final WebService annotation) {
        endpoint.publish(this.calculateEndpointAddress(endpoint, annotation.serviceName()));
    }
    
    @Override
    protected void publishEndpoint(final Endpoint endpoint, final WebServiceProvider annotation) {
        endpoint.publish(this.calculateEndpointAddress(endpoint, annotation.serviceName()));
    }
    
    protected String calculateEndpointAddress(final Endpoint endpoint, final String serviceName) {
        String fullAddress = this.baseAddress + serviceName;
        if (endpoint.getClass().getName().startsWith("weblogic.")) {
            fullAddress += "/";
        }
        return fullAddress;
    }
}
