// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.converter;

import java.util.Map;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.Assert;
import org.springframework.util.comparator.ComparableComparator;
import java.util.Comparator;

public class ConvertingComparator<S, T> implements Comparator<S>
{
    private Comparator<T> comparator;
    private Converter<S, T> converter;
    
    public ConvertingComparator(final Converter<S, T> converter) {
        this(ComparableComparator.INSTANCE, converter);
    }
    
    public ConvertingComparator(final Comparator<T> comparator, final Converter<S, T> converter) {
        Assert.notNull(comparator, "Comparator must not be null");
        Assert.notNull(converter, "Converter must not be null");
        this.comparator = comparator;
        this.converter = converter;
    }
    
    public ConvertingComparator(final Comparator<T> comparator, final ConversionService conversionService, final Class<? extends T> targetType) {
        this(comparator, (Converter)new ConversionServiceConverter(conversionService, targetType));
    }
    
    @Override
    public int compare(final S o1, final S o2) {
        final T c1 = this.converter.convert(o1);
        final T c2 = this.converter.convert(o2);
        return this.comparator.compare(c1, c2);
    }
    
    public static <K, V> ConvertingComparator<Map.Entry<K, V>, K> mapEntryKeys(final Comparator<K> comparator) {
        return new ConvertingComparator<Map.Entry<K, V>, K>(comparator, new Converter<Map.Entry<K, V>, K>() {
            @Override
            public K convert(final Map.Entry<K, V> source) {
                return source.getKey();
            }
        });
    }
    
    public static <K, V> ConvertingComparator<Map.Entry<K, V>, V> mapEntryValues(final Comparator<V> comparator) {
        return new ConvertingComparator<Map.Entry<K, V>, V>(comparator, new Converter<Map.Entry<K, V>, V>() {
            @Override
            public V convert(final Map.Entry<K, V> source) {
                return source.getValue();
            }
        });
    }
    
    private static class ConversionServiceConverter<S, T> implements Converter<S, T>
    {
        private final ConversionService conversionService;
        private final Class<? extends T> targetType;
        
        public ConversionServiceConverter(final ConversionService conversionService, final Class<? extends T> targetType) {
            Assert.notNull(conversionService, "ConversionService must not be null");
            Assert.notNull(targetType, "TargetType must not be null");
            this.conversionService = conversionService;
            this.targetType = targetType;
        }
        
        @Override
        public T convert(final S source) {
            return this.conversionService.convert(source, (Class<T>)this.targetType);
        }
    }
}
