// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.FactoryBean;

@Deprecated
public class CommonsLogFactoryBean implements FactoryBean<Log>, InitializingBean
{
    private Log log;
    
    public void setLogName(final String logName) {
        this.log = LogFactory.getLog(logName);
    }
    
    @Override
    public void afterPropertiesSet() {
        if (this.log == null) {
            throw new IllegalArgumentException("'logName' is required");
        }
    }
    
    @Override
    public Log getObject() {
        return this.log;
    }
    
    @Override
    public Class<? extends Log> getObjectType() {
        return (this.log != null) ? this.log.getClass() : Log.class;
    }
    
    @Override
    public boolean isSingleton() {
        return true;
    }
}
