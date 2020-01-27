// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.support;

import org.springframework.util.ClassUtils;
import java.util.UUID;
import java.util.Locale;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConverterRegistry;

public class DefaultConversionService extends GenericConversionService
{
    private static final boolean jsr310Available;
    
    public DefaultConversionService() {
        addDefaultConverters(this);
    }
    
    public static void addDefaultConverters(final ConverterRegistry converterRegistry) {
        addScalarConverters(converterRegistry);
        addCollectionConverters(converterRegistry);
        converterRegistry.addConverter(new ByteBufferConverter((ConversionService)converterRegistry));
        if (DefaultConversionService.jsr310Available) {
            Jsr310ConverterRegistrar.registerZoneIdConverters(converterRegistry);
        }
        converterRegistry.addConverter(new ObjectToObjectConverter());
        converterRegistry.addConverter(new IdToEntityConverter((ConversionService)converterRegistry));
        converterRegistry.addConverter(new FallbackObjectToStringConverter());
    }
    
    private static void addScalarConverters(final ConverterRegistry converterRegistry) {
        converterRegistry.addConverterFactory(new NumberToNumberConverterFactory());
        converterRegistry.addConverterFactory(new StringToNumberConverterFactory());
        converterRegistry.addConverter(Number.class, String.class, new ObjectToStringConverter());
        converterRegistry.addConverter(new StringToCharacterConverter());
        converterRegistry.addConverter(Character.class, String.class, new ObjectToStringConverter());
        converterRegistry.addConverter(new NumberToCharacterConverter());
        converterRegistry.addConverterFactory(new CharacterToNumberFactory());
        converterRegistry.addConverter(new StringToBooleanConverter());
        converterRegistry.addConverter(Boolean.class, String.class, new ObjectToStringConverter());
        converterRegistry.addConverterFactory(new StringToEnumConverterFactory());
        converterRegistry.addConverter(Enum.class, String.class, new EnumToStringConverter((ConversionService)converterRegistry));
        converterRegistry.addConverter(new StringToLocaleConverter());
        converterRegistry.addConverter(Locale.class, String.class, new ObjectToStringConverter());
        converterRegistry.addConverter(new StringToPropertiesConverter());
        converterRegistry.addConverter(new PropertiesToStringConverter());
        converterRegistry.addConverter(new StringToUUIDConverter());
        converterRegistry.addConverter(UUID.class, String.class, new ObjectToStringConverter());
    }
    
    private static void addCollectionConverters(final ConverterRegistry converterRegistry) {
        final ConversionService conversionService = (ConversionService)converterRegistry;
        converterRegistry.addConverter(new ArrayToCollectionConverter(conversionService));
        converterRegistry.addConverter(new CollectionToArrayConverter(conversionService));
        converterRegistry.addConverter(new ArrayToArrayConverter(conversionService));
        converterRegistry.addConverter(new CollectionToCollectionConverter(conversionService));
        converterRegistry.addConverter(new MapToMapConverter(conversionService));
        converterRegistry.addConverter(new ArrayToStringConverter(conversionService));
        converterRegistry.addConverter(new StringToArrayConverter(conversionService));
        converterRegistry.addConverter(new ArrayToObjectConverter(conversionService));
        converterRegistry.addConverter(new ObjectToArrayConverter(conversionService));
        converterRegistry.addConverter(new CollectionToStringConverter(conversionService));
        converterRegistry.addConverter(new StringToCollectionConverter(conversionService));
        converterRegistry.addConverter(new CollectionToObjectConverter(conversionService));
        converterRegistry.addConverter(new ObjectToCollectionConverter(conversionService));
    }
    
    static {
        jsr310Available = ClassUtils.isPresent("java.time.ZoneId", DefaultConversionService.class.getClassLoader());
    }
    
    private static final class Jsr310ConverterRegistrar
    {
        public static void registerZoneIdConverters(final ConverterRegistry converterRegistry) {
            converterRegistry.addConverter(new ZoneIdToTimeZoneConverter());
            converterRegistry.addConverter(new ZonedDateTimeToCalendarConverter());
        }
    }
}
