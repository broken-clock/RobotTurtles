// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.support;

import java.util.Collections;
import java.util.HashSet;
import java.nio.ByteBuffer;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.GenericConverter;
import java.util.Set;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

final class ByteBufferConverter implements ConditionalGenericConverter
{
    private static final TypeDescriptor BYTE_BUFFER_TYPE;
    private static final TypeDescriptor BYTE_ARRAY_TYPE;
    private static final Set<GenericConverter.ConvertiblePair> CONVERTIBLE_PAIRS;
    private ConversionService conversionService;
    
    public ByteBufferConverter(final ConversionService conversionService) {
        this.conversionService = conversionService;
    }
    
    @Override
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return ByteBufferConverter.CONVERTIBLE_PAIRS;
    }
    
    @Override
    public boolean matches(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        if (sourceType.isAssignableTo(ByteBufferConverter.BYTE_BUFFER_TYPE)) {
            return this.matchesFromByteBuffer(targetType);
        }
        return targetType.isAssignableTo(ByteBufferConverter.BYTE_BUFFER_TYPE) && this.matchesToByteBuffer(sourceType);
    }
    
    private boolean matchesFromByteBuffer(final TypeDescriptor targetType) {
        return targetType.isAssignableTo(ByteBufferConverter.BYTE_ARRAY_TYPE) || this.conversionService.canConvert(ByteBufferConverter.BYTE_ARRAY_TYPE, targetType);
    }
    
    private boolean matchesToByteBuffer(final TypeDescriptor sourceType) {
        return sourceType.isAssignableTo(ByteBufferConverter.BYTE_ARRAY_TYPE) || this.conversionService.canConvert(sourceType, ByteBufferConverter.BYTE_ARRAY_TYPE);
    }
    
    @Override
    public Object convert(final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        if (sourceType.isAssignableTo(ByteBufferConverter.BYTE_BUFFER_TYPE)) {
            return this.convertFromByteBuffer((ByteBuffer)source, targetType);
        }
        if (targetType.isAssignableTo(ByteBufferConverter.BYTE_BUFFER_TYPE)) {
            return this.convertToByteBuffer(source, sourceType);
        }
        throw new IllegalStateException("Unexpected source/target types");
    }
    
    private Object convertFromByteBuffer(final ByteBuffer source, final TypeDescriptor targetType) {
        final byte[] bytes = new byte[source.remaining()];
        source.get(bytes);
        if (targetType.isAssignableTo(ByteBufferConverter.BYTE_ARRAY_TYPE)) {
            return bytes;
        }
        return this.conversionService.convert(bytes, ByteBufferConverter.BYTE_ARRAY_TYPE, targetType);
    }
    
    private Object convertToByteBuffer(final Object source, final TypeDescriptor sourceType) {
        final byte[] bytes = (byte[])((source instanceof byte[]) ? source : this.conversionService.convert(source, sourceType, ByteBufferConverter.BYTE_ARRAY_TYPE));
        final ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
        byteBuffer.put(bytes);
        byteBuffer.rewind();
        return byteBuffer;
    }
    
    static {
        BYTE_BUFFER_TYPE = TypeDescriptor.valueOf(ByteBuffer.class);
        BYTE_ARRAY_TYPE = TypeDescriptor.valueOf(byte[].class);
        final Set<GenericConverter.ConvertiblePair> convertiblePairs = new HashSet<GenericConverter.ConvertiblePair>();
        convertiblePairs.add(new GenericConverter.ConvertiblePair(ByteBuffer.class, Object.class));
        convertiblePairs.add(new GenericConverter.ConvertiblePair(Object.class, ByteBuffer.class));
        CONVERTIBLE_PAIRS = Collections.unmodifiableSet((Set<? extends GenericConverter.ConvertiblePair>)convertiblePairs);
    }
}
