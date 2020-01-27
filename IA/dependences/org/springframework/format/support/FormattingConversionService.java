// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.support;

import org.springframework.core.convert.converter.ConditionalGenericConverter;
import java.text.ParseException;
import org.springframework.util.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import java.util.Collections;
import org.springframework.core.convert.TypeDescriptor;
import java.util.Iterator;
import java.util.Set;
import java.lang.annotation.Annotation;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.Printer;
import org.springframework.core.GenericTypeResolver;
import org.springframework.format.Formatter;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.convert.converter.GenericConverter;
import java.util.Map;
import org.springframework.util.StringValueResolver;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.format.FormatterRegistry;
import org.springframework.core.convert.support.GenericConversionService;

public class FormattingConversionService extends GenericConversionService implements FormatterRegistry, EmbeddedValueResolverAware
{
    private StringValueResolver embeddedValueResolver;
    private final Map<AnnotationConverterKey, GenericConverter> cachedPrinters;
    private final Map<AnnotationConverterKey, GenericConverter> cachedParsers;
    
    public FormattingConversionService() {
        this.cachedPrinters = new ConcurrentHashMap<AnnotationConverterKey, GenericConverter>(64);
        this.cachedParsers = new ConcurrentHashMap<AnnotationConverterKey, GenericConverter>(64);
    }
    
    @Override
    public void setEmbeddedValueResolver(final StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }
    
    @Override
    public void addFormatter(final Formatter<?> formatter) {
        final Class<?> fieldType = GenericTypeResolver.resolveTypeArgument(formatter.getClass(), Formatter.class);
        if (fieldType == null) {
            throw new IllegalArgumentException("Unable to extract parameterized field type argument from Formatter [" + formatter.getClass().getName() + "]; does the formatter parameterize the <T> generic type?");
        }
        this.addFormatterForFieldType(fieldType, formatter);
    }
    
    @Override
    public void addFormatterForFieldType(final Class<?> fieldType, final Formatter<?> formatter) {
        this.addConverter(new PrinterConverter(fieldType, formatter, this));
        this.addConverter(new ParserConverter(fieldType, formatter, this));
    }
    
    @Override
    public void addFormatterForFieldType(final Class<?> fieldType, final Printer<?> printer, final Parser<?> parser) {
        this.addConverter(new PrinterConverter(fieldType, printer, this));
        this.addConverter(new ParserConverter(fieldType, parser, this));
    }
    
    @Override
    public void addFormatterForFieldAnnotation(final AnnotationFormatterFactory annotationFormatterFactory) {
        final Class<? extends Annotation> annotationType = (Class<? extends Annotation>)GenericTypeResolver.resolveTypeArgument(annotationFormatterFactory.getClass(), AnnotationFormatterFactory.class);
        if (annotationType == null) {
            throw new IllegalArgumentException("Unable to extract parameterized Annotation type argument from AnnotationFormatterFactory [" + annotationFormatterFactory.getClass().getName() + "]; does the factory parameterize the <A extends Annotation> generic type?");
        }
        if (this.embeddedValueResolver != null && annotationFormatterFactory instanceof EmbeddedValueResolverAware) {
            ((EmbeddedValueResolverAware)annotationFormatterFactory).setEmbeddedValueResolver(this.embeddedValueResolver);
        }
        final Set<Class<?>> fieldTypes = (Set<Class<?>>)annotationFormatterFactory.getFieldTypes();
        for (final Class<?> fieldType : fieldTypes) {
            this.addConverter(new AnnotationPrinterConverter(annotationType, annotationFormatterFactory, fieldType));
            this.addConverter(new AnnotationParserConverter(annotationType, annotationFormatterFactory, fieldType));
        }
    }
    
    private static class PrinterConverter implements GenericConverter
    {
        private Class<?> fieldType;
        private TypeDescriptor printerObjectType;
        private Printer printer;
        private ConversionService conversionService;
        
        public PrinterConverter(final Class<?> fieldType, final Printer<?> printer, final ConversionService conversionService) {
            this.fieldType = fieldType;
            this.printerObjectType = TypeDescriptor.valueOf(this.resolvePrinterObjectType(printer));
            this.printer = printer;
            this.conversionService = conversionService;
        }
        
        @Override
        public Set<ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(new ConvertiblePair(this.fieldType, String.class));
        }
        
        @Override
        public Object convert(Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
            if (source == null) {
                return "";
            }
            if (!sourceType.isAssignableTo(this.printerObjectType)) {
                source = this.conversionService.convert(source, sourceType, this.printerObjectType);
            }
            return this.printer.print(source, LocaleContextHolder.getLocale());
        }
        
        private Class<?> resolvePrinterObjectType(final Printer<?> printer) {
            return GenericTypeResolver.resolveTypeArgument(printer.getClass(), Printer.class);
        }
        
        @Override
        public String toString() {
            return this.fieldType.getName() + " -> " + String.class.getName() + " : " + this.printer;
        }
    }
    
    private static class ParserConverter implements GenericConverter
    {
        private Class<?> fieldType;
        private Parser<?> parser;
        private ConversionService conversionService;
        
        public ParserConverter(final Class<?> fieldType, final Parser<?> parser, final ConversionService conversionService) {
            this.fieldType = fieldType;
            this.parser = parser;
            this.conversionService = conversionService;
        }
        
        @Override
        public Set<ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(new ConvertiblePair(String.class, this.fieldType));
        }
        
        @Override
        public Object convert(final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
            final String text = (String)source;
            if (!StringUtils.hasText(text)) {
                return null;
            }
            Object result;
            try {
                result = this.parser.parse(text, LocaleContextHolder.getLocale());
            }
            catch (ParseException ex) {
                throw new IllegalArgumentException("Unable to parse '" + text + "'", ex);
            }
            if (result == null) {
                throw new IllegalStateException("Parsers are not allowed to return null");
            }
            final TypeDescriptor resultType = TypeDescriptor.valueOf(result.getClass());
            if (!resultType.isAssignableTo(targetType)) {
                result = this.conversionService.convert(result, resultType, targetType);
            }
            return result;
        }
        
        @Override
        public String toString() {
            return String.class.getName() + " -> " + this.fieldType.getName() + ": " + this.parser;
        }
    }
    
    private class AnnotationPrinterConverter implements ConditionalGenericConverter
    {
        private Class<? extends Annotation> annotationType;
        private AnnotationFormatterFactory annotationFormatterFactory;
        private Class<?> fieldType;
        
        public AnnotationPrinterConverter(final Class<? extends Annotation> annotationType, final AnnotationFormatterFactory<?> annotationFormatterFactory, final Class<?> fieldType) {
            this.annotationType = annotationType;
            this.annotationFormatterFactory = annotationFormatterFactory;
            this.fieldType = fieldType;
        }
        
        @Override
        public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(new GenericConverter.ConvertiblePair(this.fieldType, String.class));
        }
        
        @Override
        public boolean matches(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
            return sourceType.hasAnnotation(this.annotationType);
        }
        
        @Override
        public Object convert(final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
            final AnnotationConverterKey converterKey = new AnnotationConverterKey(sourceType.getAnnotation(this.annotationType), sourceType.getObjectType());
            GenericConverter converter = FormattingConversionService.this.cachedPrinters.get(converterKey);
            if (converter == null) {
                final Printer<?> printer = this.annotationFormatterFactory.getPrinter(converterKey.getAnnotation(), converterKey.getFieldType());
                converter = new PrinterConverter(this.fieldType, printer, FormattingConversionService.this);
                FormattingConversionService.this.cachedPrinters.put(converterKey, converter);
            }
            return converter.convert(source, sourceType, targetType);
        }
        
        @Override
        public String toString() {
            return "@" + this.annotationType.getName() + " " + this.fieldType.getName() + " -> " + String.class.getName() + ": " + this.annotationFormatterFactory;
        }
    }
    
    private class AnnotationParserConverter implements ConditionalGenericConverter
    {
        private Class<? extends Annotation> annotationType;
        private AnnotationFormatterFactory annotationFormatterFactory;
        private Class<?> fieldType;
        
        public AnnotationParserConverter(final Class<? extends Annotation> annotationType, final AnnotationFormatterFactory<?> annotationFormatterFactory, final Class<?> fieldType) {
            this.annotationType = annotationType;
            this.annotationFormatterFactory = annotationFormatterFactory;
            this.fieldType = fieldType;
        }
        
        @Override
        public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(new GenericConverter.ConvertiblePair(String.class, this.fieldType));
        }
        
        @Override
        public boolean matches(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
            return targetType.hasAnnotation(this.annotationType);
        }
        
        @Override
        public Object convert(final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
            final AnnotationConverterKey converterKey = new AnnotationConverterKey(targetType.getAnnotation(this.annotationType), targetType.getObjectType());
            GenericConverter converter = FormattingConversionService.this.cachedParsers.get(converterKey);
            if (converter == null) {
                final Parser<?> parser = this.annotationFormatterFactory.getParser(converterKey.getAnnotation(), converterKey.getFieldType());
                converter = new ParserConverter(this.fieldType, parser, FormattingConversionService.this);
                FormattingConversionService.this.cachedParsers.put(converterKey, converter);
            }
            return converter.convert(source, sourceType, targetType);
        }
        
        @Override
        public String toString() {
            return String.class.getName() + " -> @" + this.annotationType.getName() + " " + this.fieldType.getName() + ": " + this.annotationFormatterFactory;
        }
    }
    
    private static class AnnotationConverterKey
    {
        private final Annotation annotation;
        private final Class<?> fieldType;
        
        public AnnotationConverterKey(final Annotation annotation, final Class<?> fieldType) {
            this.annotation = annotation;
            this.fieldType = fieldType;
        }
        
        public Annotation getAnnotation() {
            return this.annotation;
        }
        
        public Class<?> getFieldType() {
            return this.fieldType;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof AnnotationConverterKey)) {
                return false;
            }
            final AnnotationConverterKey key = (AnnotationConverterKey)o;
            return this.annotation.equals(key.annotation) && this.fieldType.equals(key.fieldType);
        }
        
        @Override
        public int hashCode() {
            return this.annotation.hashCode() + 29 * this.fieldType.hashCode();
        }
    }
}
