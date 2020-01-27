// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.std;

import java.io.IOException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.lang.reflect.Member;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.util.EnumResolver;
import com.fasterxml.jackson.databind.util.CompactStringObjectMap;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

@JacksonStdImpl
public class EnumDeserializer extends StdScalarDeserializer<Object> implements ContextualDeserializer
{
    private static final long serialVersionUID = 1L;
    protected Object[] _enumsByIndex;
    private final Enum<?> _enumDefaultValue;
    protected final CompactStringObjectMap _lookupByName;
    protected CompactStringObjectMap _lookupByToString;
    protected final Boolean _caseInsensitive;
    
    public EnumDeserializer(final EnumResolver byNameResolver, final Boolean caseInsensitive) {
        super(byNameResolver.getEnumClass());
        this._lookupByName = byNameResolver.constructLookup();
        this._enumsByIndex = byNameResolver.getRawEnums();
        this._enumDefaultValue = byNameResolver.getDefaultValue();
        this._caseInsensitive = caseInsensitive;
    }
    
    protected EnumDeserializer(final EnumDeserializer base, final Boolean caseInsensitive) {
        super(base);
        this._lookupByName = base._lookupByName;
        this._enumsByIndex = base._enumsByIndex;
        this._enumDefaultValue = base._enumDefaultValue;
        this._caseInsensitive = caseInsensitive;
    }
    
    @Deprecated
    public EnumDeserializer(final EnumResolver byNameResolver) {
        this(byNameResolver, null);
    }
    
    @Deprecated
    public static JsonDeserializer<?> deserializerForCreator(final DeserializationConfig config, final Class<?> enumClass, final AnnotatedMethod factory) {
        return deserializerForCreator(config, enumClass, factory, null, null);
    }
    
    public static JsonDeserializer<?> deserializerForCreator(final DeserializationConfig config, final Class<?> enumClass, final AnnotatedMethod factory, final ValueInstantiator valueInstantiator, final SettableBeanProperty[] creatorProps) {
        if (config.canOverrideAccessModifiers()) {
            ClassUtil.checkAndFixAccess(factory.getMember(), config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
        }
        return new FactoryBasedEnumDeserializer(enumClass, factory, factory.getParameterType(0), valueInstantiator, creatorProps);
    }
    
    public static JsonDeserializer<?> deserializerForNoArgsCreator(final DeserializationConfig config, final Class<?> enumClass, final AnnotatedMethod factory) {
        if (config.canOverrideAccessModifiers()) {
            ClassUtil.checkAndFixAccess(factory.getMember(), config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
        }
        return new FactoryBasedEnumDeserializer(enumClass, factory);
    }
    
    public EnumDeserializer withResolved(final Boolean caseInsensitive) {
        if (this._caseInsensitive == caseInsensitive) {
            return this;
        }
        return new EnumDeserializer(this, caseInsensitive);
    }
    
    @Override
    public JsonDeserializer<?> createContextual(final DeserializationContext ctxt, final BeanProperty property) throws JsonMappingException {
        Boolean caseInsensitive = this.findFormatFeature(ctxt, property, this.handledType(), JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
        if (caseInsensitive == null) {
            caseInsensitive = this._caseInsensitive;
        }
        return this.withResolved(caseInsensitive);
    }
    
    @Override
    public boolean isCachable() {
        return true;
    }
    
    @Override
    public Object deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final JsonToken curr = p.getCurrentToken();
        if (curr == JsonToken.VALUE_STRING || curr == JsonToken.FIELD_NAME) {
            final CompactStringObjectMap lookup = ctxt.isEnabled(DeserializationFeature.READ_ENUMS_USING_TO_STRING) ? this._getToStringLookup(ctxt) : this._lookupByName;
            final String name = p.getText();
            final Object result = lookup.find(name);
            if (result == null) {
                return this._deserializeAltString(p, ctxt, lookup, name);
            }
            return result;
        }
        else {
            if (curr != JsonToken.VALUE_NUMBER_INT) {
                return this._deserializeOther(p, ctxt);
            }
            final int index = p.getIntValue();
            if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS)) {
                return ctxt.handleWeirdNumberValue(this._enumClass(), index, "not allowed to deserialize Enum value out of number: disable DeserializationConfig.DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS to allow", new Object[0]);
            }
            if (index >= 0 && index < this._enumsByIndex.length) {
                return this._enumsByIndex[index];
            }
            if (this._enumDefaultValue != null && ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)) {
                return this._enumDefaultValue;
            }
            if (!ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
                return ctxt.handleWeirdNumberValue(this._enumClass(), index, "index value outside legal index range [0..%s]", this._enumsByIndex.length - 1);
            }
            return null;
        }
    }
    
    private final Object _deserializeAltString(final JsonParser p, final DeserializationContext ctxt, final CompactStringObjectMap lookup, String name) throws IOException {
        name = name.trim();
        if (name.length() == 0) {
            if (ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
                return this.getEmptyValue(ctxt);
            }
        }
        else if (Boolean.TRUE.equals(this._caseInsensitive)) {
            final Object match = lookup.findCaseInsensitive(name);
            if (match != null) {
                return match;
            }
        }
        else if (!ctxt.isEnabled(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS)) {
            final char c = name.charAt(0);
            if (c >= '0' && c <= '9') {
                try {
                    final int index = Integer.parseInt(name);
                    if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
                        return ctxt.handleWeirdStringValue(this._enumClass(), name, "value looks like quoted Enum index, but `MapperFeature.ALLOW_COERCION_OF_SCALARS` prevents use", new Object[0]);
                    }
                    if (index >= 0 && index < this._enumsByIndex.length) {
                        return this._enumsByIndex[index];
                    }
                }
                catch (NumberFormatException ex) {}
            }
        }
        if (this._enumDefaultValue != null && ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)) {
            return this._enumDefaultValue;
        }
        if (!ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
            return ctxt.handleWeirdStringValue(this._enumClass(), name, "value not one of declared Enum instance names: %s", lookup.keys());
        }
        return null;
    }
    
    protected Object _deserializeOther(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.START_ARRAY)) {
            return this._deserializeFromArray(p, ctxt);
        }
        return ctxt.handleUnexpectedToken(this._enumClass(), p);
    }
    
    protected Class<?> _enumClass() {
        return this.handledType();
    }
    
    protected CompactStringObjectMap _getToStringLookup(final DeserializationContext ctxt) {
        CompactStringObjectMap lookup = this._lookupByToString;
        if (lookup == null) {
            synchronized (this) {
                lookup = EnumResolver.constructUnsafeUsingToString(this._enumClass(), ctxt.getAnnotationIntrospector()).constructLookup();
            }
            this._lookupByToString = lookup;
        }
        return lookup;
    }
}
