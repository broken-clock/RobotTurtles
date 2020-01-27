// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.databind.type.TypeFactory;
import java.util.Collections;
import java.util.Arrays;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
import java.util.Set;
import java.util.List;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.DeserializationContext;

public abstract class JavaUtilCollectionsDeserializers
{
    private static final int TYPE_SINGLETON_SET = 1;
    private static final int TYPE_SINGLETON_LIST = 2;
    private static final int TYPE_SINGLETON_MAP = 3;
    private static final int TYPE_UNMODIFIABLE_SET = 4;
    private static final int TYPE_UNMODIFIABLE_LIST = 5;
    private static final int TYPE_UNMODIFIABLE_MAP = 6;
    public static final int TYPE_AS_LIST = 7;
    private static final Class<?> CLASS_AS_ARRAYS_LIST;
    private static final Class<?> CLASS_SINGLETON_SET;
    private static final Class<?> CLASS_SINGLETON_LIST;
    private static final Class<?> CLASS_SINGLETON_MAP;
    private static final Class<?> CLASS_UNMODIFIABLE_SET;
    private static final Class<?> CLASS_UNMODIFIABLE_LIST;
    private static final Class<?> CLASS_UNMODIFIABLE_MAP;
    
    public static JsonDeserializer<?> findForCollection(final DeserializationContext ctxt, final JavaType type) throws JsonMappingException {
        JavaUtilCollectionsConverter conv;
        if (type.hasRawClass(JavaUtilCollectionsDeserializers.CLASS_AS_ARRAYS_LIST)) {
            conv = converter(7, type, List.class);
        }
        else if (type.hasRawClass(JavaUtilCollectionsDeserializers.CLASS_SINGLETON_LIST)) {
            conv = converter(2, type, List.class);
        }
        else if (type.hasRawClass(JavaUtilCollectionsDeserializers.CLASS_SINGLETON_SET)) {
            conv = converter(1, type, Set.class);
        }
        else if (type.hasRawClass(JavaUtilCollectionsDeserializers.CLASS_UNMODIFIABLE_LIST)) {
            conv = converter(5, type, List.class);
        }
        else {
            if (!type.hasRawClass(JavaUtilCollectionsDeserializers.CLASS_UNMODIFIABLE_SET)) {
                return null;
            }
            conv = converter(4, type, Set.class);
        }
        return new StdDelegatingDeserializer<Object>(conv);
    }
    
    public static JsonDeserializer<?> findForMap(final DeserializationContext ctxt, final JavaType type) throws JsonMappingException {
        JavaUtilCollectionsConverter conv;
        if (type.hasRawClass(JavaUtilCollectionsDeserializers.CLASS_SINGLETON_MAP)) {
            conv = converter(3, type, Map.class);
        }
        else {
            if (!type.hasRawClass(JavaUtilCollectionsDeserializers.CLASS_UNMODIFIABLE_MAP)) {
                return null;
            }
            conv = converter(6, type, Map.class);
        }
        return new StdDelegatingDeserializer<Object>(conv);
    }
    
    static JavaUtilCollectionsConverter converter(final int kind, final JavaType concreteType, final Class<?> rawSuper) {
        return new JavaUtilCollectionsConverter(kind, concreteType.findSuperType(rawSuper));
    }
    
    static {
        CLASS_AS_ARRAYS_LIST = Arrays.asList(null, null).getClass();
        final Set<?> set = Collections.singleton((Object)Boolean.TRUE);
        CLASS_SINGLETON_SET = set.getClass();
        CLASS_UNMODIFIABLE_SET = Collections.unmodifiableSet(set).getClass();
        final List<?> list = Collections.singletonList((Object)Boolean.TRUE);
        CLASS_SINGLETON_LIST = list.getClass();
        CLASS_UNMODIFIABLE_LIST = Collections.unmodifiableList(list).getClass();
        final Map<?, ?> map = Collections.singletonMap((Object)"a", (Object)"b");
        CLASS_SINGLETON_MAP = map.getClass();
        CLASS_UNMODIFIABLE_MAP = Collections.unmodifiableMap(map).getClass();
    }
    
    private static class JavaUtilCollectionsConverter implements Converter<Object, Object>
    {
        private final JavaType _inputType;
        private final int _kind;
        
        private JavaUtilCollectionsConverter(final int kind, final JavaType inputType) {
            this._inputType = inputType;
            this._kind = kind;
        }
        
        @Override
        public Object convert(final Object value) {
            if (value == null) {
                return null;
            }
            switch (this._kind) {
                case 1: {
                    final Set<?> set = (Set<?>)value;
                    this._checkSingleton(set.size());
                    return Collections.singleton(set.iterator().next());
                }
                case 2: {
                    final List<?> list = (List<?>)value;
                    this._checkSingleton(list.size());
                    return Collections.singletonList(list.get(0));
                }
                case 3: {
                    final Map<?, ?> map = (Map<?, ?>)value;
                    this._checkSingleton(map.size());
                    final Map.Entry<?, ?> entry = map.entrySet().iterator().next();
                    return Collections.singletonMap(entry.getKey(), entry.getValue());
                }
                case 4: {
                    return Collections.unmodifiableSet((Set<?>)value);
                }
                case 5: {
                    return Collections.unmodifiableList((List<?>)value);
                }
                case 6: {
                    return Collections.unmodifiableMap((Map<?, ?>)value);
                }
                default: {
                    return value;
                }
            }
        }
        
        @Override
        public JavaType getInputType(final TypeFactory typeFactory) {
            return this._inputType;
        }
        
        @Override
        public JavaType getOutputType(final TypeFactory typeFactory) {
            return this._inputType;
        }
        
        private void _checkSingleton(final int size) {
            if (size != 1) {
                throw new IllegalArgumentException("Can not deserialize Singleton container from " + size + " entries");
            }
        }
    }
}
