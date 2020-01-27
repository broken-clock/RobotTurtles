// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.env;

import org.springframework.core.convert.ConversionException;
import org.springframework.util.ClassUtils;
import java.util.Iterator;

public class PropertySourcesPropertyResolver extends AbstractPropertyResolver
{
    private final PropertySources propertySources;
    
    public PropertySourcesPropertyResolver(final PropertySources propertySources) {
        this.propertySources = propertySources;
    }
    
    @Override
    public boolean containsProperty(final String key) {
        if (this.propertySources != null) {
            for (final PropertySource<?> propertySource : this.propertySources) {
                if (propertySource.containsProperty(key)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public String getProperty(final String key) {
        return this.getProperty(key, String.class, true);
    }
    
    @Override
    public <T> T getProperty(final String key, final Class<T> targetValueType) {
        return this.getProperty(key, targetValueType, true);
    }
    
    @Override
    protected String getPropertyAsRawString(final String key) {
        return this.getProperty(key, String.class, false);
    }
    
    protected <T> T getProperty(final String key, final Class<T> targetValueType, final boolean resolveNestedPlaceholders) {
        final boolean debugEnabled = this.logger.isDebugEnabled();
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(String.format("getProperty(\"%s\", %s)", key, targetValueType.getSimpleName()));
        }
        if (this.propertySources != null) {
            for (final PropertySource<?> propertySource : this.propertySources) {
                if (debugEnabled) {
                    this.logger.debug(String.format("Searching for key '%s' in [%s]", key, propertySource.getName()));
                }
                Object value;
                if ((value = propertySource.getProperty(key)) != null) {
                    final Class<?> valueType = value.getClass();
                    if (resolveNestedPlaceholders && value instanceof String) {
                        value = this.resolveNestedPlaceholders((String)value);
                    }
                    if (debugEnabled) {
                        this.logger.debug(String.format("Found key '%s' in [%s] with type [%s] and value '%s'", key, propertySource.getName(), valueType.getSimpleName(), value));
                    }
                    if (!this.conversionService.canConvert(valueType, targetValueType)) {
                        throw new IllegalArgumentException(String.format("Cannot convert value [%s] from source type [%s] to target type [%s]", value, valueType.getSimpleName(), targetValueType.getSimpleName()));
                    }
                    return this.conversionService.convert(value, targetValueType);
                }
            }
        }
        if (debugEnabled) {
            this.logger.debug(String.format("Could not find key '%s' in any property source. Returning [null]", key));
        }
        return null;
    }
    
    @Override
    public <T> Class<T> getPropertyAsClass(final String key, final Class<T> targetValueType) {
        final boolean debugEnabled = this.logger.isDebugEnabled();
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(String.format("getPropertyAsClass(\"%s\", %s)", key, targetValueType.getSimpleName()));
        }
        if (this.propertySources != null) {
            for (final PropertySource<?> propertySource : this.propertySources) {
                if (debugEnabled) {
                    this.logger.debug(String.format("Searching for key '%s' in [%s]", key, propertySource.getName()));
                }
                final Object value = propertySource.getProperty(key);
                if (value != null) {
                    if (debugEnabled) {
                        this.logger.debug(String.format("Found key '%s' in [%s] with value '%s'", key, propertySource.getName(), value));
                    }
                    Class<?> clazz = null;
                    Label_0242: {
                        if (value instanceof String) {
                            try {
                                clazz = ClassUtils.forName((String)value, null);
                                break Label_0242;
                            }
                            catch (Exception ex) {
                                throw new ClassConversionException((String)value, targetValueType, ex);
                            }
                        }
                        if (value instanceof Class) {
                            clazz = (Class<?>)value;
                        }
                        else {
                            clazz = value.getClass();
                        }
                    }
                    if (!targetValueType.isAssignableFrom(clazz)) {
                        throw new ClassConversionException(clazz, targetValueType);
                    }
                    final Class<T> targetClass = (Class<T>)clazz;
                    return targetClass;
                }
            }
        }
        if (debugEnabled) {
            this.logger.debug(String.format("Could not find key '%s' in any property source. Returning [null]", key));
        }
        return null;
    }
    
    private static class ClassConversionException extends ConversionException
    {
        public ClassConversionException(final Class<?> actual, final Class<?> expected) {
            super(String.format("Actual type %s is not assignable to expected type %s", actual.getName(), expected.getName()));
        }
        
        public ClassConversionException(final String actual, final Class<?> expected, final Exception ex) {
            super(String.format("Could not find/load class %s during attempt to convert to %s", actual, expected.getName()), ex);
        }
    }
}
