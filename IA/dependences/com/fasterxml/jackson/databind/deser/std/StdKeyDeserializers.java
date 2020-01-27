// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.databind.JsonMappingException;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import com.fasterxml.jackson.databind.BeanDescription;
import java.lang.reflect.Member;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.util.EnumResolver;
import java.io.Serializable;
import com.fasterxml.jackson.databind.deser.KeyDeserializers;

public class StdKeyDeserializers implements KeyDeserializers, Serializable
{
    private static final long serialVersionUID = 1L;
    
    public static KeyDeserializer constructEnumKeyDeserializer(final EnumResolver enumResolver) {
        return new StdKeyDeserializer.EnumKD(enumResolver, null);
    }
    
    public static KeyDeserializer constructEnumKeyDeserializer(final EnumResolver enumResolver, final AnnotatedMethod factory) {
        return new StdKeyDeserializer.EnumKD(enumResolver, factory);
    }
    
    public static KeyDeserializer constructDelegatingKeyDeserializer(final DeserializationConfig config, final JavaType type, final JsonDeserializer<?> deser) {
        return new StdKeyDeserializer.DelegatingKD(type.getRawClass(), deser);
    }
    
    public static KeyDeserializer findStringBasedKeyDeserializer(final DeserializationConfig config, final JavaType type) {
        final BeanDescription beanDesc = config.introspect(type);
        final Constructor<?> ctor = beanDesc.findSingleArgConstructor(String.class);
        if (ctor != null) {
            if (config.canOverrideAccessModifiers()) {
                ClassUtil.checkAndFixAccess(ctor, config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
            }
            return new StdKeyDeserializer.StringCtorKeyDeserializer(ctor);
        }
        final Method m = beanDesc.findFactoryMethod(String.class);
        if (m != null) {
            if (config.canOverrideAccessModifiers()) {
                ClassUtil.checkAndFixAccess(m, config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
            }
            return new StdKeyDeserializer.StringFactoryKeyDeserializer(m);
        }
        return null;
    }
    
    @Override
    public KeyDeserializer findKeyDeserializer(final JavaType type, final DeserializationConfig config, final BeanDescription beanDesc) throws JsonMappingException {
        Class<?> raw = type.getRawClass();
        if (raw.isPrimitive()) {
            raw = ClassUtil.wrapperType(raw);
        }
        return StdKeyDeserializer.forType(raw);
    }
}
