// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.converter;

import org.springframework.util.Assert;
import org.springframework.core.convert.TypeDescriptor;
import java.util.Set;

public interface GenericConverter
{
    Set<ConvertiblePair> getConvertibleTypes();
    
    Object convert(final Object p0, final TypeDescriptor p1, final TypeDescriptor p2);
    
    public static final class ConvertiblePair
    {
        private final Class<?> sourceType;
        private final Class<?> targetType;
        
        public ConvertiblePair(final Class<?> sourceType, final Class<?> targetType) {
            Assert.notNull(sourceType, "Source type must not be null");
            Assert.notNull(targetType, "Target type must not be null");
            this.sourceType = sourceType;
            this.targetType = targetType;
        }
        
        public Class<?> getSourceType() {
            return this.sourceType;
        }
        
        public Class<?> getTargetType() {
            return this.targetType;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || obj.getClass() != ConvertiblePair.class) {
                return false;
            }
            final ConvertiblePair other = (ConvertiblePair)obj;
            return this.sourceType.equals(other.sourceType) && this.targetType.equals(other.targetType);
        }
        
        @Override
        public int hashCode() {
            return this.sourceType.hashCode() * 31 + this.targetType.hashCode();
        }
        
        @Override
        public String toString() {
            return this.sourceType.getName() + " -> " + this.targetType.getName();
        }
    }
}
