// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.databind.deser.impl.PropertyBasedObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.BeanDescription;
import java.util.Map;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.JavaType;
import java.io.Serializable;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class AbstractDeserializer extends JsonDeserializer<Object> implements ContextualDeserializer, Serializable
{
    private static final long serialVersionUID = 1L;
    protected final JavaType _baseType;
    protected final ObjectIdReader _objectIdReader;
    protected final Map<String, SettableBeanProperty> _backRefProperties;
    protected transient Map<String, SettableBeanProperty> _properties;
    protected final boolean _acceptString;
    protected final boolean _acceptBoolean;
    protected final boolean _acceptInt;
    protected final boolean _acceptDouble;
    
    public AbstractDeserializer(final BeanDeserializerBuilder builder, final BeanDescription beanDesc, final Map<String, SettableBeanProperty> backRefProps, final Map<String, SettableBeanProperty> props) {
        this._baseType = beanDesc.getType();
        this._objectIdReader = builder.getObjectIdReader();
        this._backRefProperties = backRefProps;
        this._properties = props;
        final Class<?> cls = this._baseType.getRawClass();
        this._acceptString = cls.isAssignableFrom(String.class);
        this._acceptBoolean = (cls == Boolean.TYPE || cls.isAssignableFrom(Boolean.class));
        this._acceptInt = (cls == Integer.TYPE || cls.isAssignableFrom(Integer.class));
        this._acceptDouble = (cls == Double.TYPE || cls.isAssignableFrom(Double.class));
    }
    
    @Deprecated
    public AbstractDeserializer(final BeanDeserializerBuilder builder, final BeanDescription beanDesc, final Map<String, SettableBeanProperty> backRefProps) {
        this(builder, beanDesc, backRefProps, null);
    }
    
    protected AbstractDeserializer(final BeanDescription beanDesc) {
        this._baseType = beanDesc.getType();
        this._objectIdReader = null;
        this._backRefProperties = null;
        final Class<?> cls = this._baseType.getRawClass();
        this._acceptString = cls.isAssignableFrom(String.class);
        this._acceptBoolean = (cls == Boolean.TYPE || cls.isAssignableFrom(Boolean.class));
        this._acceptInt = (cls == Integer.TYPE || cls.isAssignableFrom(Integer.class));
        this._acceptDouble = (cls == Double.TYPE || cls.isAssignableFrom(Double.class));
    }
    
    protected AbstractDeserializer(final AbstractDeserializer base, final ObjectIdReader objectIdReader, final Map<String, SettableBeanProperty> props) {
        this._baseType = base._baseType;
        this._backRefProperties = base._backRefProperties;
        this._acceptString = base._acceptString;
        this._acceptBoolean = base._acceptBoolean;
        this._acceptInt = base._acceptInt;
        this._acceptDouble = base._acceptDouble;
        this._objectIdReader = objectIdReader;
        this._properties = props;
    }
    
    public static AbstractDeserializer constructForNonPOJO(final BeanDescription beanDesc) {
        return new AbstractDeserializer(beanDesc);
    }
    
    @Override
    public JsonDeserializer<?> createContextual(final DeserializationContext ctxt, final BeanProperty property) throws JsonMappingException {
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (property != null && intr != null) {
            final AnnotatedMember accessor = property.getMember();
            if (accessor != null) {
                ObjectIdInfo objectIdInfo = intr.findObjectIdInfo(accessor);
                if (objectIdInfo != null) {
                    SettableBeanProperty idProp = null;
                    ObjectIdResolver resolver = ctxt.objectIdResolverInstance(accessor, objectIdInfo);
                    objectIdInfo = intr.findObjectReferenceInfo(accessor, objectIdInfo);
                    final Class<?> implClass = objectIdInfo.getGeneratorType();
                    JavaType idType;
                    ObjectIdGenerator<?> idGen;
                    if (implClass == ObjectIdGenerators.PropertyGenerator.class) {
                        final PropertyName propName = objectIdInfo.getPropertyName();
                        idProp = ((this._properties == null) ? null : this._properties.get(propName.getSimpleName()));
                        if (idProp == null) {
                            ctxt.reportBadDefinition(this._baseType, String.format("Invalid Object Id definition for %s: cannot find property with name '%s'", this.handledType().getName(), propName));
                        }
                        idType = idProp.getType();
                        idGen = new PropertyBasedObjectIdGenerator(objectIdInfo.getScope());
                    }
                    else {
                        resolver = ctxt.objectIdResolverInstance(accessor, objectIdInfo);
                        final JavaType type = ctxt.constructType(implClass);
                        idType = ctxt.getTypeFactory().findTypeParameters(type, ObjectIdGenerator.class)[0];
                        idGen = ctxt.objectIdGeneratorInstance(accessor, objectIdInfo);
                    }
                    final JsonDeserializer<?> deser = ctxt.findRootValueDeserializer(idType);
                    final ObjectIdReader oir = ObjectIdReader.construct(idType, objectIdInfo.getPropertyName(), idGen, deser, idProp, resolver);
                    return new AbstractDeserializer(this, oir, null);
                }
            }
        }
        if (this._properties == null) {
            return this;
        }
        return new AbstractDeserializer(this, this._objectIdReader, null);
    }
    
    @Override
    public Class<?> handledType() {
        return this._baseType.getRawClass();
    }
    
    @Override
    public boolean isCachable() {
        return true;
    }
    
    @Override
    public Boolean supportsUpdate(final DeserializationConfig config) {
        return null;
    }
    
    @Override
    public ObjectIdReader getObjectIdReader() {
        return this._objectIdReader;
    }
    
    @Override
    public SettableBeanProperty findBackReference(final String logicalName) {
        return (this._backRefProperties == null) ? null : this._backRefProperties.get(logicalName);
    }
    
    @Override
    public Object deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
        if (this._objectIdReader != null) {
            JsonToken t = p.getCurrentToken();
            if (t != null) {
                if (t.isScalarValue()) {
                    return this._deserializeFromObjectId(p, ctxt);
                }
                if (t == JsonToken.START_OBJECT) {
                    t = p.nextToken();
                }
                if (t == JsonToken.FIELD_NAME && this._objectIdReader.maySerializeAsObject() && this._objectIdReader.isValidReferencePropertyName(p.getCurrentName(), p)) {
                    return this._deserializeFromObjectId(p, ctxt);
                }
            }
        }
        final Object result = this._deserializeIfNatural(p, ctxt);
        if (result != null) {
            return result;
        }
        return typeDeserializer.deserializeTypedFromObject(p, ctxt);
    }
    
    @Override
    public Object deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final ValueInstantiator bogus = new ValueInstantiator.Base(this._baseType);
        return ctxt.handleMissingInstantiator(this._baseType.getRawClass(), bogus, p, "abstract types either need to be mapped to concrete types, have custom deserializer, or contain additional type information", new Object[0]);
    }
    
    protected Object _deserializeIfNatural(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        switch (p.getCurrentTokenId()) {
            case 6: {
                if (this._acceptString) {
                    return p.getText();
                }
                break;
            }
            case 7: {
                if (this._acceptInt) {
                    return p.getIntValue();
                }
                break;
            }
            case 8: {
                if (this._acceptDouble) {
                    return p.getDoubleValue();
                }
                break;
            }
            case 9: {
                if (this._acceptBoolean) {
                    return Boolean.TRUE;
                }
                break;
            }
            case 10: {
                if (this._acceptBoolean) {
                    return Boolean.FALSE;
                }
                break;
            }
        }
        return null;
    }
    
    protected Object _deserializeFromObjectId(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final Object id = this._objectIdReader.readObjectReference(p, ctxt);
        final ReadableObjectId roid = ctxt.findObjectId(id, this._objectIdReader.generator, this._objectIdReader.resolver);
        final Object pojo = roid.resolve();
        if (pojo == null) {
            throw new UnresolvedForwardReference(p, "Could not resolve Object Id [" + id + "] -- unresolved forward-reference?", p.getCurrentLocation(), roid);
        }
        return pojo;
    }
}
