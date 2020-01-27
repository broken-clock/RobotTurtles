// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.datetime.standard;

import org.springframework.beans.factory.InitializingBean;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.FactoryBean;

public class DateTimeFormatterFactoryBean extends DateTimeFormatterFactory implements FactoryBean<DateTimeFormatter>, InitializingBean
{
    private DateTimeFormatter dateTimeFormatter;
    
    @Override
    public void afterPropertiesSet() {
        this.dateTimeFormatter = this.createDateTimeFormatter();
    }
    
    @Override
    public DateTimeFormatter getObject() {
        return this.dateTimeFormatter;
    }
    
    @Override
    public Class<?> getObjectType() {
        return DateTimeFormatter.class;
    }
    
    @Override
    public boolean isSingleton() {
        return true;
    }
}
