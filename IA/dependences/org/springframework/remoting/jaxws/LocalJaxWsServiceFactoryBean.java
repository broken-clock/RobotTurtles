// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.jaxws;

import org.springframework.beans.factory.InitializingBean;
import javax.xml.ws.Service;
import org.springframework.beans.factory.FactoryBean;

public class LocalJaxWsServiceFactoryBean extends LocalJaxWsServiceFactory implements FactoryBean<Service>, InitializingBean
{
    private Service service;
    
    @Override
    public void afterPropertiesSet() {
        this.service = this.createJaxWsService();
    }
    
    @Override
    public Service getObject() {
        return this.service;
    }
    
    @Override
    public Class<? extends Service> getObjectType() {
        return (this.service != null) ? this.service.getClass() : Service.class;
    }
    
    @Override
    public boolean isSingleton() {
        return true;
    }
}
