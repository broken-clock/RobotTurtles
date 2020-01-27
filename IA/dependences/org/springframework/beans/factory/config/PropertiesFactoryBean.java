// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import java.io.IOException;
import org.springframework.beans.factory.InitializingBean;
import java.util.Properties;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.support.PropertiesLoaderSupport;

public class PropertiesFactoryBean extends PropertiesLoaderSupport implements FactoryBean<Properties>, InitializingBean
{
    private boolean singleton;
    private Properties singletonInstance;
    
    public PropertiesFactoryBean() {
        this.singleton = true;
    }
    
    public final void setSingleton(final boolean singleton) {
        this.singleton = singleton;
    }
    
    @Override
    public final boolean isSingleton() {
        return this.singleton;
    }
    
    @Override
    public final void afterPropertiesSet() throws IOException {
        if (this.singleton) {
            this.singletonInstance = this.createProperties();
        }
    }
    
    @Override
    public final Properties getObject() throws IOException {
        if (this.singleton) {
            return this.singletonInstance;
        }
        return this.createProperties();
    }
    
    @Override
    public Class<Properties> getObjectType() {
        return Properties.class;
    }
    
    protected Properties createProperties() throws IOException {
        return this.mergeProperties();
    }
}
