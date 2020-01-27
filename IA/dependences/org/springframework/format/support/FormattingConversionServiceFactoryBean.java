// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.support;

import java.util.Iterator;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.support.ConversionServiceFactory;
import org.springframework.util.StringValueResolver;
import org.springframework.format.FormatterRegistrar;
import java.util.Set;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.beans.factory.FactoryBean;

public class FormattingConversionServiceFactoryBean implements FactoryBean<FormattingConversionService>, EmbeddedValueResolverAware, InitializingBean
{
    private Set<?> converters;
    private Set<?> formatters;
    private Set<FormatterRegistrar> formatterRegistrars;
    private boolean registerDefaultFormatters;
    private StringValueResolver embeddedValueResolver;
    private FormattingConversionService conversionService;
    
    public FormattingConversionServiceFactoryBean() {
        this.registerDefaultFormatters = true;
    }
    
    public void setConverters(final Set<?> converters) {
        this.converters = converters;
    }
    
    public void setFormatters(final Set<?> formatters) {
        this.formatters = formatters;
    }
    
    public void setFormatterRegistrars(final Set<FormatterRegistrar> formatterRegistrars) {
        this.formatterRegistrars = formatterRegistrars;
    }
    
    public void setRegisterDefaultFormatters(final boolean registerDefaultFormatters) {
        this.registerDefaultFormatters = registerDefaultFormatters;
    }
    
    @Override
    public void setEmbeddedValueResolver(final StringValueResolver embeddedValueResolver) {
        this.embeddedValueResolver = embeddedValueResolver;
    }
    
    @Override
    public void afterPropertiesSet() {
        this.conversionService = new DefaultFormattingConversionService(this.embeddedValueResolver, this.registerDefaultFormatters);
        ConversionServiceFactory.registerConverters(this.converters, this.conversionService);
        this.registerFormatters();
    }
    
    private void registerFormatters() {
        if (this.formatters != null) {
            for (final Object formatter : this.formatters) {
                if (formatter instanceof Formatter) {
                    this.conversionService.addFormatter((Formatter<?>)formatter);
                }
                else {
                    if (!(formatter instanceof AnnotationFormatterFactory)) {
                        throw new IllegalArgumentException("Custom formatters must be implementations of Formatter or AnnotationFormatterFactory");
                    }
                    this.conversionService.addFormatterForFieldAnnotation((AnnotationFormatterFactory)formatter);
                }
            }
        }
        if (this.formatterRegistrars != null) {
            for (final FormatterRegistrar registrar : this.formatterRegistrars) {
                registrar.registerFormatters(this.conversionService);
            }
        }
        this.installFormatters(this.conversionService);
    }
    
    @Deprecated
    protected void installFormatters(final FormatterRegistry registry) {
    }
    
    @Override
    public FormattingConversionService getObject() {
        return this.conversionService;
    }
    
    @Override
    public Class<? extends FormattingConversionService> getObjectType() {
        return FormattingConversionService.class;
    }
    
    @Override
    public boolean isSingleton() {
        return true;
    }
}
