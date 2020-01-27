// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.support;

import java.util.Collection;
import org.springframework.util.StringUtils;
import java.util.LinkedList;
import java.lang.reflect.Array;
import org.springframework.util.ClassUtils;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import org.springframework.util.ObjectUtils;
import org.springframework.core.convert.converter.ConditionalConverter;
import java.util.Collections;
import java.util.Set;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;
import org.springframework.core.convert.converter.Converter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import org.springframework.core.convert.converter.GenericConverter;

public class GenericConversionService implements ConfigurableConversionService
{
    private static final GenericConverter NO_OP_CONVERTER;
    private static final GenericConverter NO_MATCH;
    private final Converters converters;
    private final Map<ConverterCacheKey, GenericConverter> converterCache;
    
    public GenericConversionService() {
        this.converters = new Converters();
        this.converterCache = new ConcurrentHashMap<ConverterCacheKey, GenericConverter>(64);
    }
    
    @Override
    public void addConverter(final Converter<?, ?> converter) {
        final ResolvableType[] typeInfo = this.getRequiredTypeInfo(converter, Converter.class);
        Assert.notNull(typeInfo, "Unable to the determine sourceType <S> and targetType <T> which your Converter<S, T> converts between; declare these generic types.");
        this.addConverter(new ConverterAdapter(converter, typeInfo[0], typeInfo[1]));
    }
    
    @Override
    public void addConverter(final Class<?> sourceType, final Class<?> targetType, final Converter<?, ?> converter) {
        this.addConverter(new ConverterAdapter(converter, ResolvableType.forClass(sourceType), ResolvableType.forClass(targetType)));
    }
    
    @Override
    public void addConverter(final GenericConverter converter) {
        this.converters.add(converter);
        this.invalidateCache();
    }
    
    @Override
    public void addConverterFactory(final ConverterFactory<?, ?> converterFactory) {
        final ResolvableType[] typeInfo = this.getRequiredTypeInfo(converterFactory, ConverterFactory.class);
        Assert.notNull("Unable to the determine sourceType <S> and targetRangeType R which your ConverterFactory<S, R> converts between; declare these generic types.");
        this.addConverter(new ConverterFactoryAdapter(converterFactory, new GenericConverter.ConvertiblePair(typeInfo[0].resolve(), typeInfo[1].resolve())));
    }
    
    @Override
    public void removeConvertible(final Class<?> sourceType, final Class<?> targetType) {
        this.converters.remove(sourceType, targetType);
        this.invalidateCache();
    }
    
    @Override
    public boolean canConvert(final Class<?> sourceType, final Class<?> targetType) {
        Assert.notNull(targetType, "targetType to convert to cannot be null");
        return this.canConvert((sourceType != null) ? TypeDescriptor.valueOf(sourceType) : null, TypeDescriptor.valueOf(targetType));
    }
    
    @Override
    public boolean canConvert(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        Assert.notNull(targetType, "targetType to convert to cannot be null");
        if (sourceType == null) {
            return true;
        }
        final GenericConverter converter = this.getConverter(sourceType, targetType);
        return converter != null;
    }
    
    public boolean canBypassConvert(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        Assert.notNull(targetType, "The targetType to convert to cannot be null");
        if (sourceType == null) {
            return true;
        }
        final GenericConverter converter = this.getConverter(sourceType, targetType);
        return converter == GenericConversionService.NO_OP_CONVERTER;
    }
    
    @Override
    public <T> T convert(final Object source, final Class<T> targetType) {
        Assert.notNull(targetType, "The targetType to convert to cannot be null");
        return (T)this.convert(source, TypeDescriptor.forObject(source), TypeDescriptor.valueOf(targetType));
    }
    
    @Override
    public Object convert(final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        Assert.notNull(targetType, "The targetType to convert to cannot be null");
        if (sourceType == null) {
            Assert.isTrue(source == null, "The source must be [null] if sourceType == [null]");
            return this.handleResult(sourceType, targetType, this.convertNullSource(sourceType, targetType));
        }
        if (source != null && !sourceType.getObjectType().isInstance(source)) {
            throw new IllegalArgumentException("The source to convert from must be an instance of " + sourceType + "; instead it was a " + source.getClass().getName());
        }
        final GenericConverter converter = this.getConverter(sourceType, targetType);
        if (converter != null) {
            final Object result = ConversionUtils.invokeConverter(converter, source, sourceType, targetType);
            return this.handleResult(sourceType, targetType, result);
        }
        return this.handleConverterNotFound(source, sourceType, targetType);
    }
    
    public Object convert(final Object source, final TypeDescriptor targetType) {
        return this.convert(source, TypeDescriptor.forObject(source), targetType);
    }
    
    @Override
    public String toString() {
        return this.converters.toString();
    }
    
    protected Object convertNullSource(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        return null;
    }
    
    protected GenericConverter getConverter(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        final ConverterCacheKey key = new ConverterCacheKey(sourceType, targetType);
        GenericConverter converter = this.converterCache.get(key);
        if (converter != null) {
            return (converter != GenericConversionService.NO_MATCH) ? converter : null;
        }
        converter = this.converters.find(sourceType, targetType);
        if (converter == null) {
            converter = this.getDefaultConverter(sourceType, targetType);
        }
        if (converter != null) {
            this.converterCache.put(key, converter);
            return converter;
        }
        this.converterCache.put(key, GenericConversionService.NO_MATCH);
        return null;
    }
    
    protected GenericConverter getDefaultConverter(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        return sourceType.isAssignableTo(targetType) ? GenericConversionService.NO_OP_CONVERTER : null;
    }
    
    private ResolvableType[] getRequiredTypeInfo(final Object converter, final Class<?> genericIfc) {
        final ResolvableType resolvableType = ResolvableType.forClass(converter.getClass()).as(genericIfc);
        final ResolvableType[] generics = resolvableType.getGenerics();
        if (generics.length < 2) {
            return null;
        }
        final Class<?> sourceType = generics[0].resolve();
        final Class<?> targetType = generics[1].resolve();
        if (sourceType == null || targetType == null) {
            return null;
        }
        return generics;
    }
    
    private void invalidateCache() {
        this.converterCache.clear();
    }
    
    private Object handleConverterNotFound(final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        if (source == null) {
            this.assertNotPrimitiveTargetType(sourceType, targetType);
            return source;
        }
        if (sourceType.isAssignableTo(targetType) && targetType.getObjectType().isInstance(source)) {
            return source;
        }
        throw new ConverterNotFoundException(sourceType, targetType);
    }
    
    private Object handleResult(final TypeDescriptor sourceType, final TypeDescriptor targetType, final Object result) {
        if (result == null) {
            this.assertNotPrimitiveTargetType(sourceType, targetType);
        }
        return result;
    }
    
    private void assertNotPrimitiveTargetType(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        if (targetType.isPrimitive()) {
            throw new ConversionFailedException(sourceType, targetType, null, new IllegalArgumentException("A null value cannot be assigned to a primitive type"));
        }
    }
    
    static {
        NO_OP_CONVERTER = new NoOpConverter("NO_OP");
        NO_MATCH = new NoOpConverter("NO_MATCH");
    }
    
    private final class ConverterAdapter implements ConditionalGenericConverter
    {
        private final Converter<Object, Object> converter;
        private final GenericConverter.ConvertiblePair typeInfo;
        private final ResolvableType targetType;
        
        public ConverterAdapter(final Converter<?, ?> converter, final ResolvableType sourceType, final ResolvableType targetType) {
            this.converter = (Converter<Object, Object>)converter;
            this.typeInfo = new GenericConverter.ConvertiblePair(sourceType.resolve(Object.class), targetType.resolve(Object.class));
            this.targetType = targetType;
        }
        
        @Override
        public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(this.typeInfo);
        }
        
        @Override
        public boolean matches(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
            if (!this.typeInfo.getTargetType().equals(targetType.getObjectType())) {
                return false;
            }
            final ResolvableType rt = targetType.getResolvableType();
            return (rt.getType() instanceof Class || rt.isAssignableFrom(this.targetType) || this.targetType.hasUnresolvableGenerics()) && (!(this.converter instanceof ConditionalConverter) || ((ConditionalConverter)this.converter).matches(sourceType, targetType));
        }
        
        @Override
        public Object convert(final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
            if (source == null) {
                return GenericConversionService.this.convertNullSource(sourceType, targetType);
            }
            return this.converter.convert(source);
        }
        
        @Override
        public String toString() {
            return this.typeInfo + " : " + this.converter;
        }
    }
    
    private final class ConverterFactoryAdapter implements ConditionalGenericConverter
    {
        private final ConverterFactory<Object, Object> converterFactory;
        private final GenericConverter.ConvertiblePair typeInfo;
        
        public ConverterFactoryAdapter(final ConverterFactory<?, ?> converterFactory, final GenericConverter.ConvertiblePair typeInfo) {
            this.converterFactory = (ConverterFactory<Object, Object>)converterFactory;
            this.typeInfo = typeInfo;
        }
        
        @Override
        public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(this.typeInfo);
        }
        
        @Override
        public boolean matches(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
            boolean matches = true;
            if (this.converterFactory instanceof ConditionalConverter) {
                matches = ((ConditionalConverter)this.converterFactory).matches(sourceType, targetType);
            }
            if (matches) {
                final Converter<?, ?> converter = this.converterFactory.getConverter(targetType.getType());
                if (converter instanceof ConditionalConverter) {
                    matches = ((ConditionalConverter)converter).matches(sourceType, targetType);
                }
            }
            return matches;
        }
        
        @Override
        public Object convert(final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
            if (source == null) {
                return GenericConversionService.this.convertNullSource(sourceType, targetType);
            }
            return this.converterFactory.getConverter(targetType.getObjectType()).convert(source);
        }
        
        @Override
        public String toString() {
            return this.typeInfo + " : " + this.converterFactory;
        }
    }
    
    private static final class ConverterCacheKey
    {
        private final TypeDescriptor sourceType;
        private final TypeDescriptor targetType;
        
        public ConverterCacheKey(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
            this.sourceType = sourceType;
            this.targetType = targetType;
        }
        
        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ConverterCacheKey)) {
                return false;
            }
            final ConverterCacheKey otherKey = (ConverterCacheKey)other;
            return ObjectUtils.nullSafeEquals(this.sourceType, otherKey.sourceType) && ObjectUtils.nullSafeEquals(this.targetType, otherKey.targetType);
        }
        
        @Override
        public int hashCode() {
            return ObjectUtils.nullSafeHashCode(this.sourceType) * 29 + ObjectUtils.nullSafeHashCode(this.targetType);
        }
        
        @Override
        public String toString() {
            return "ConverterCacheKey [sourceType = " + this.sourceType + ", targetType = " + this.targetType + "]";
        }
    }
    
    private static class Converters
    {
        private final Set<GenericConverter> globalConverters;
        private final Map<GenericConverter.ConvertiblePair, ConvertersForPair> converters;
        
        private Converters() {
            this.globalConverters = new LinkedHashSet<GenericConverter>();
            this.converters = new LinkedHashMap<GenericConverter.ConvertiblePair, ConvertersForPair>(36);
        }
        
        public void add(final GenericConverter converter) {
            final Set<GenericConverter.ConvertiblePair> convertibleTypes = converter.getConvertibleTypes();
            if (convertibleTypes == null) {
                Assert.state(converter instanceof ConditionalConverter, "Only conditional converters may return null convertible types");
                this.globalConverters.add(converter);
            }
            else {
                for (final GenericConverter.ConvertiblePair convertiblePair : convertibleTypes) {
                    final ConvertersForPair convertersForPair = this.getMatchableConverters(convertiblePair);
                    convertersForPair.add(converter);
                }
            }
        }
        
        private ConvertersForPair getMatchableConverters(final GenericConverter.ConvertiblePair convertiblePair) {
            ConvertersForPair convertersForPair = this.converters.get(convertiblePair);
            if (convertersForPair == null) {
                convertersForPair = new ConvertersForPair();
                this.converters.put(convertiblePair, convertersForPair);
            }
            return convertersForPair;
        }
        
        public void remove(final Class<?> sourceType, final Class<?> targetType) {
            this.converters.remove(new GenericConverter.ConvertiblePair(sourceType, targetType));
        }
        
        public GenericConverter find(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
            final List<Class<?>> sourceCandidates = this.getClassHierarchy(sourceType.getType());
            final List<Class<?>> targetCandidates = this.getClassHierarchy(targetType.getType());
            for (final Class<?> sourceCandidate : sourceCandidates) {
                for (final Class<?> targetCandidate : targetCandidates) {
                    final GenericConverter.ConvertiblePair convertiblePair = new GenericConverter.ConvertiblePair(sourceCandidate, targetCandidate);
                    final GenericConverter converter = this.getRegisteredConverter(sourceType, targetType, convertiblePair);
                    if (converter != null) {
                        return converter;
                    }
                }
            }
            return null;
        }
        
        private GenericConverter getRegisteredConverter(final TypeDescriptor sourceType, final TypeDescriptor targetType, final GenericConverter.ConvertiblePair convertiblePair) {
            final ConvertersForPair convertersForPair = this.converters.get(convertiblePair);
            if (convertersForPair != null) {
                final GenericConverter converter = convertersForPair.getConverter(sourceType, targetType);
                if (converter != null) {
                    return converter;
                }
            }
            for (final GenericConverter globalConverter : this.globalConverters) {
                if (((ConditionalConverter)globalConverter).matches(sourceType, targetType)) {
                    return globalConverter;
                }
            }
            return null;
        }
        
        private List<Class<?>> getClassHierarchy(final Class<?> type) {
            final List<Class<?>> hierarchy = new ArrayList<Class<?>>(20);
            final Set<Class<?>> visited = new HashSet<Class<?>>(20);
            this.addToClassHierarchy(0, ClassUtils.resolvePrimitiveIfNecessary(type), false, hierarchy, visited);
            final boolean array = type.isArray();
            for (int i = 0; i < hierarchy.size(); ++i) {
                Class<?> candidate = hierarchy.get(i);
                candidate = (array ? candidate.getComponentType() : ClassUtils.resolvePrimitiveIfNecessary(candidate));
                final Class<?> superclass = candidate.getSuperclass();
                if (candidate.getSuperclass() != null && superclass != Object.class) {
                    this.addToClassHierarchy(i + 1, candidate.getSuperclass(), array, hierarchy, visited);
                }
                for (final Class<?> implementedInterface : candidate.getInterfaces()) {
                    this.addToClassHierarchy(hierarchy.size(), implementedInterface, array, hierarchy, visited);
                }
            }
            this.addToClassHierarchy(hierarchy.size(), Object.class, array, hierarchy, visited);
            this.addToClassHierarchy(hierarchy.size(), Object.class, false, hierarchy, visited);
            return hierarchy;
        }
        
        private void addToClassHierarchy(final int index, Class<?> type, final boolean asArray, final List<Class<?>> hierarchy, final Set<Class<?>> visited) {
            if (asArray) {
                type = Array.newInstance(type, 0).getClass();
            }
            if (visited.add(type)) {
                hierarchy.add(index, type);
            }
        }
        
        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("ConversionService converters =\n");
            for (final String converterString : this.getConverterStrings()) {
                builder.append('\t').append(converterString).append('\n');
            }
            return builder.toString();
        }
        
        private List<String> getConverterStrings() {
            final List<String> converterStrings = new ArrayList<String>();
            for (final ConvertersForPair convertersForPair : this.converters.values()) {
                converterStrings.add(convertersForPair.toString());
            }
            Collections.sort(converterStrings);
            return converterStrings;
        }
    }
    
    private static class ConvertersForPair
    {
        private final LinkedList<GenericConverter> converters;
        
        private ConvertersForPair() {
            this.converters = new LinkedList<GenericConverter>();
        }
        
        public void add(final GenericConverter converter) {
            this.converters.addFirst(converter);
        }
        
        public GenericConverter getConverter(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
            for (final GenericConverter converter : this.converters) {
                if (!(converter instanceof ConditionalGenericConverter) || ((ConditionalGenericConverter)converter).matches(sourceType, targetType)) {
                    return converter;
                }
            }
            return null;
        }
        
        @Override
        public String toString() {
            return StringUtils.collectionToCommaDelimitedString(this.converters);
        }
    }
    
    private static class NoOpConverter implements GenericConverter
    {
        private final String name;
        
        public NoOpConverter(final String name) {
            this.name = name;
        }
        
        @Override
        public Set<ConvertiblePair> getConvertibleTypes() {
            return null;
        }
        
        @Override
        public Object convert(final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
            return source;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
}
