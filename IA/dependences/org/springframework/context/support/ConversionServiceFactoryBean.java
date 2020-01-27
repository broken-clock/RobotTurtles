// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.support;

import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.support.ConversionServiceFactory;
import org.springframework.core.convert.support.GenericConversionService;
import java.util.Set;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.beans.factory.FactoryBean;

public class ConversionServiceFactoryBean implements FactoryBean<ConversionService>, InitializingBean
{
    private Set<?> converters;
    private GenericConversionService conversionService;
    
    public void setConverters(final Set<?> converters) {
        this.converters = converters;
    }
    
    @Override
    public void afterPropertiesSet() {
        this.conversionService = this.createConversionService();
        ConversionServiceFactory.registerConverters(this.converters, this.conversionService);
    }
    
    protected GenericConversionService createConversionService() {
        return new DefaultConversionService();
    }
    
    @Override
    public ConversionService getObject() {
        return this.conversionService;
    }
    
    @Override
    public Class<? extends ConversionService> getObjectType() {
        return GenericConversionService.class;
    }
    
    @Override
    public boolean isSingleton() {
        return true;
    }
}
