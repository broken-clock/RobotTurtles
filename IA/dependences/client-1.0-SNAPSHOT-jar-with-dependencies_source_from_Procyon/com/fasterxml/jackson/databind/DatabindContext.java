// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.reflect.Type;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.TimeZone;
import java.util.Locale;
import com.fasterxml.jackson.databind.cfg.MapperConfig;

public abstract class DatabindContext
{
    private static final int MAX_ERROR_STR_LEN = 500;
    
    public abstract MapperConfig<?> getConfig();
    
    public abstract AnnotationIntrospector getAnnotationIntrospector();
    
    public abstract boolean isEnabled(final MapperFeature p0);
    
    public abstract boolean canOverrideAccessModifiers();
    
    public abstract Class<?> getActiveView();
    
    public abstract Locale getLocale();
    
    public abstract TimeZone getTimeZone();
    
    public abstract JsonFormat.Value getDefaultPropertyFormat(final Class<?> p0);
    
    public abstract Object getAttribute(final Object p0);
    
    public abstract DatabindContext setAttribute(final Object p0, final Object p1);
    
    public JavaType constructType(final Type type) {
        if (type == null) {
            return null;
        }
        return this.getTypeFactory().constructType(type);
    }
    
    public JavaType constructSpecializedType(final JavaType baseType, final Class<?> subclass) {
        if (baseType.getRawClass() == subclass) {
            return baseType;
        }
        return this.getConfig().constructSpecializedType(baseType, subclass);
    }
    
    public JavaType resolveSubType(final JavaType baseType, final String subClass) throws JsonMappingException {
        if (subClass.indexOf(60) > 0) {
            final JavaType t = this.getTypeFactory().constructFromCanonical(subClass);
            if (t.isTypeOrSubTypeOf(baseType.getRawClass())) {
                return t;
            }
        }
        else {
            Class<?> cls;
            try {
                cls = this.getTypeFactory().findClass(subClass);
            }
            catch (ClassNotFoundException e2) {
                return null;
            }
            catch (Exception e) {
                throw this.invalidTypeIdException(baseType, subClass, String.format("problem: (%s) %s", e.getClass().getName(), ClassUtil.exceptionMessage(e)));
            }
            if (baseType.isTypeOrSuperTypeOf(cls)) {
                return this.getTypeFactory().constructSpecializedType(baseType, cls);
            }
        }
        throw this.invalidTypeIdException(baseType, subClass, "Not a subtype");
    }
    
    protected abstract JsonMappingException invalidTypeIdException(final JavaType p0, final String p1, final String p2);
    
    public abstract TypeFactory getTypeFactory();
    
    public ObjectIdGenerator<?> objectIdGeneratorInstance(final Annotated annotated, final ObjectIdInfo objectIdInfo) throws JsonMappingException {
        final Class<?> implClass = objectIdInfo.getGeneratorType();
        final MapperConfig<?> config = this.getConfig();
        final HandlerInstantiator hi = config.getHandlerInstantiator();
        ObjectIdGenerator<?> gen = (hi == null) ? null : hi.objectIdGeneratorInstance(config, annotated, implClass);
        if (gen == null) {
            gen = ClassUtil.createInstance(implClass, config.canOverrideAccessModifiers());
        }
        return gen.forScope(objectIdInfo.getScope());
    }
    
    public ObjectIdResolver objectIdResolverInstance(final Annotated annotated, final ObjectIdInfo objectIdInfo) {
        final Class<? extends ObjectIdResolver> implClass = objectIdInfo.getResolverType();
        final MapperConfig<?> config = this.getConfig();
        final HandlerInstantiator hi = config.getHandlerInstantiator();
        ObjectIdResolver resolver = (hi == null) ? null : hi.resolverIdGeneratorInstance(config, annotated, implClass);
        if (resolver == null) {
            resolver = ClassUtil.createInstance(implClass, config.canOverrideAccessModifiers());
        }
        return resolver;
    }
    
    public Converter<Object, Object> converterInstance(final Annotated annotated, final Object converterDef) throws JsonMappingException {
        if (converterDef == null) {
            return null;
        }
        if (converterDef instanceof Converter) {
            return (Converter<Object, Object>)converterDef;
        }
        if (!(converterDef instanceof Class)) {
            throw new IllegalStateException("AnnotationIntrospector returned Converter definition of type " + converterDef.getClass().getName() + "; expected type Converter or Class<Converter> instead");
        }
        final Class<?> converterClass = (Class<?>)converterDef;
        if (converterClass == Converter.None.class || ClassUtil.isBogusClass(converterClass)) {
            return null;
        }
        if (!Converter.class.isAssignableFrom(converterClass)) {
            throw new IllegalStateException("AnnotationIntrospector returned Class " + converterClass.getName() + "; expected Class<Converter>");
        }
        final MapperConfig<?> config = this.getConfig();
        final HandlerInstantiator hi = config.getHandlerInstantiator();
        Converter<?, ?> conv = (hi == null) ? null : hi.converterInstance(config, annotated, converterClass);
        if (conv == null) {
            conv = ClassUtil.createInstance(converterClass, config.canOverrideAccessModifiers());
        }
        return (Converter<Object, Object>)conv;
    }
    
    public abstract <T> T reportBadDefinition(final JavaType p0, final String p1) throws JsonMappingException;
    
    public <T> T reportBadDefinition(final Class<?> type, final String msg) throws JsonMappingException {
        return this.reportBadDefinition(this.constructType(type), msg);
    }
    
    protected final String _format(final String msg, final Object... msgArgs) {
        if (msgArgs.length > 0) {
            return String.format(msg, msgArgs);
        }
        return msg;
    }
    
    protected final String _truncate(final String desc) {
        if (desc == null) {
            return "";
        }
        if (desc.length() <= 500) {
            return desc;
        }
        return desc.substring(0, 500) + "]...[" + desc.substring(desc.length() - 500);
    }
    
    protected String _quotedString(final String desc) {
        if (desc == null) {
            return "[N/A]";
        }
        return String.format("\"%s\"", this._truncate(desc));
    }
    
    protected String _colonConcat(final String msgBase, final String extra) {
        if (extra == null) {
            return msgBase;
        }
        return msgBase + ": " + extra;
    }
    
    protected String _desc(final String desc) {
        if (desc == null) {
            return "[N/A]";
        }
        return this._truncate(desc);
    }
}
