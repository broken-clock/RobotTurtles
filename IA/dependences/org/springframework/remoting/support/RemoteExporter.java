// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.support;

import org.springframework.util.ClassUtils;
import org.springframework.aop.framework.adapter.AdvisorAdapterRegistry;
import org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;
import org.aopalliance.aop.Advice;
import org.springframework.aop.framework.ProxyFactory;

public abstract class RemoteExporter extends RemotingSupport
{
    private Object service;
    private Class<?> serviceInterface;
    private Boolean registerTraceInterceptor;
    private Object[] interceptors;
    
    public void setService(final Object service) {
        this.service = service;
    }
    
    public Object getService() {
        return this.service;
    }
    
    public void setServiceInterface(final Class<?> serviceInterface) {
        if (serviceInterface != null && !serviceInterface.isInterface()) {
            throw new IllegalArgumentException("'serviceInterface' must be an interface");
        }
        this.serviceInterface = serviceInterface;
    }
    
    public Class<?> getServiceInterface() {
        return this.serviceInterface;
    }
    
    public void setRegisterTraceInterceptor(final boolean registerTraceInterceptor) {
        this.registerTraceInterceptor = registerTraceInterceptor;
    }
    
    public void setInterceptors(final Object[] interceptors) {
        this.interceptors = interceptors;
    }
    
    protected void checkService() throws IllegalArgumentException {
        if (this.getService() == null) {
            throw new IllegalArgumentException("Property 'service' is required");
        }
    }
    
    protected void checkServiceInterface() throws IllegalArgumentException {
        final Class<?> serviceInterface = this.getServiceInterface();
        final Object service = this.getService();
        if (serviceInterface == null) {
            throw new IllegalArgumentException("Property 'serviceInterface' is required");
        }
        if (service instanceof String) {
            throw new IllegalArgumentException("Service [" + service + "] is a String " + "rather than an actual service reference: Have you accidentally specified " + "the service bean name as value instead of as reference?");
        }
        if (!serviceInterface.isInstance(service)) {
            throw new IllegalArgumentException("Service interface [" + serviceInterface.getName() + "] needs to be implemented by service [" + service + "] of class [" + service.getClass().getName() + "]");
        }
    }
    
    protected Object getProxyForService() {
        this.checkService();
        this.checkServiceInterface();
        final ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.addInterface(this.getServiceInterface());
        Label_0066: {
            if (this.registerTraceInterceptor != null) {
                if (!this.registerTraceInterceptor) {
                    break Label_0066;
                }
            }
            else if (this.interceptors != null) {
                break Label_0066;
            }
            proxyFactory.addAdvice(new RemoteInvocationTraceInterceptor(this.getExporterName()));
        }
        if (this.interceptors != null) {
            final AdvisorAdapterRegistry adapterRegistry = GlobalAdvisorAdapterRegistry.getInstance();
            for (int i = 0; i < this.interceptors.length; ++i) {
                proxyFactory.addAdvisor(adapterRegistry.wrap(this.interceptors[i]));
            }
        }
        proxyFactory.setTarget(this.getService());
        proxyFactory.setOpaque(true);
        return proxyFactory.getProxy(this.getBeanClassLoader());
    }
    
    protected String getExporterName() {
        return ClassUtils.getShortName(this.getClass());
    }
}
