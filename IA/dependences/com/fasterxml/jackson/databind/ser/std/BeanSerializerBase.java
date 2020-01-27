// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.jsonschema.JsonSerializableSchema;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import java.io.Closeable;
import com.fasterxml.jackson.databind.ser.impl.WritableObjectId;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import java.util.Arrays;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import java.util.Iterator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.impl.PropertyBasedObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import java.lang.reflect.Type;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.ser.impl.MapEntrySerializer;
import java.util.Map;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.util.ArrayList;
import java.util.Set;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.fasterxml.jackson.databind.ser.BeanSerializerBuilder;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.ser.AnyGetterWriter;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.jsonschema.SchemaAware;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import com.fasterxml.jackson.databind.ser.ResolvableSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

public abstract class BeanSerializerBase extends StdSerializer<Object> implements ContextualSerializer, ResolvableSerializer, JsonFormatVisitable, SchemaAware
{
    protected static final PropertyName NAME_FOR_OBJECT_REF;
    protected static final BeanPropertyWriter[] NO_PROPS;
    protected final JavaType _beanType;
    protected final BeanPropertyWriter[] _props;
    protected final BeanPropertyWriter[] _filteredProps;
    protected final AnyGetterWriter _anyGetterWriter;
    protected final Object _propertyFilterId;
    protected final AnnotatedMember _typeId;
    protected final ObjectIdWriter _objectIdWriter;
    protected final JsonFormat.Shape _serializationShape;
    
    protected BeanSerializerBase(final JavaType type, final BeanSerializerBuilder builder, final BeanPropertyWriter[] properties, final BeanPropertyWriter[] filteredProperties) {
        super(type);
        this._beanType = type;
        this._props = properties;
        this._filteredProps = filteredProperties;
        if (builder == null) {
            this._typeId = null;
            this._anyGetterWriter = null;
            this._propertyFilterId = null;
            this._objectIdWriter = null;
            this._serializationShape = null;
        }
        else {
            this._typeId = builder.getTypeId();
            this._anyGetterWriter = builder.getAnyGetter();
            this._propertyFilterId = builder.getFilterId();
            this._objectIdWriter = builder.getObjectIdWriter();
            final JsonFormat.Value format = builder.getBeanDescription().findExpectedFormat(null);
            this._serializationShape = ((format == null) ? null : format.getShape());
        }
    }
    
    public BeanSerializerBase(final BeanSerializerBase src, final BeanPropertyWriter[] properties, final BeanPropertyWriter[] filteredProperties) {
        super(src._handledType);
        this._beanType = src._beanType;
        this._props = properties;
        this._filteredProps = filteredProperties;
        this._typeId = src._typeId;
        this._anyGetterWriter = src._anyGetterWriter;
        this._objectIdWriter = src._objectIdWriter;
        this._propertyFilterId = src._propertyFilterId;
        this._serializationShape = src._serializationShape;
    }
    
    protected BeanSerializerBase(final BeanSerializerBase src, final ObjectIdWriter objectIdWriter) {
        this(src, objectIdWriter, src._propertyFilterId);
    }
    
    protected BeanSerializerBase(final BeanSerializerBase src, final ObjectIdWriter objectIdWriter, final Object filterId) {
        super(src._handledType);
        this._beanType = src._beanType;
        this._props = src._props;
        this._filteredProps = src._filteredProps;
        this._typeId = src._typeId;
        this._anyGetterWriter = src._anyGetterWriter;
        this._objectIdWriter = objectIdWriter;
        this._propertyFilterId = filterId;
        this._serializationShape = src._serializationShape;
    }
    
    @Deprecated
    protected BeanSerializerBase(final BeanSerializerBase src, final String[] toIgnore) {
        this(src, ArrayBuilders.arrayToSet(toIgnore));
    }
    
    protected BeanSerializerBase(final BeanSerializerBase src, final Set<String> toIgnore) {
        super(src._handledType);
        this._beanType = src._beanType;
        final BeanPropertyWriter[] propsIn = src._props;
        final BeanPropertyWriter[] fpropsIn = src._filteredProps;
        final int len = propsIn.length;
        final ArrayList<BeanPropertyWriter> propsOut = new ArrayList<BeanPropertyWriter>(len);
        final ArrayList<BeanPropertyWriter> fpropsOut = (fpropsIn == null) ? null : new ArrayList<BeanPropertyWriter>(len);
        for (int i = 0; i < len; ++i) {
            final BeanPropertyWriter bpw = propsIn[i];
            if (toIgnore == null || !toIgnore.contains(bpw.getName())) {
                propsOut.add(bpw);
                if (fpropsIn != null) {
                    fpropsOut.add(fpropsIn[i]);
                }
            }
        }
        this._props = propsOut.toArray(new BeanPropertyWriter[propsOut.size()]);
        this._filteredProps = (BeanPropertyWriter[])((fpropsOut == null) ? null : ((BeanPropertyWriter[])fpropsOut.toArray(new BeanPropertyWriter[fpropsOut.size()])));
        this._typeId = src._typeId;
        this._anyGetterWriter = src._anyGetterWriter;
        this._objectIdWriter = src._objectIdWriter;
        this._propertyFilterId = src._propertyFilterId;
        this._serializationShape = src._serializationShape;
    }
    
    public abstract BeanSerializerBase withObjectIdWriter(final ObjectIdWriter p0);
    
    protected abstract BeanSerializerBase withIgnorals(final Set<String> p0);
    
    @Deprecated
    protected BeanSerializerBase withIgnorals(final String[] toIgnore) {
        return this.withIgnorals(ArrayBuilders.arrayToSet(toIgnore));
    }
    
    protected abstract BeanSerializerBase asArraySerializer();
    
    @Override
    public abstract BeanSerializerBase withFilterId(final Object p0);
    
    protected BeanSerializerBase(final BeanSerializerBase src) {
        this(src, src._props, src._filteredProps);
    }
    
    protected BeanSerializerBase(final BeanSerializerBase src, final NameTransformer unwrapper) {
        this(src, rename(src._props, unwrapper), rename(src._filteredProps, unwrapper));
    }
    
    private static final BeanPropertyWriter[] rename(final BeanPropertyWriter[] props, final NameTransformer transformer) {
        if (props == null || props.length == 0 || transformer == null || transformer == NameTransformer.NOP) {
            return props;
        }
        final int len = props.length;
        final BeanPropertyWriter[] result = new BeanPropertyWriter[len];
        for (int i = 0; i < len; ++i) {
            final BeanPropertyWriter bpw = props[i];
            if (bpw != null) {
                result[i] = bpw.rename(transformer);
            }
        }
        return result;
    }
    
    @Override
    public void resolve(final SerializerProvider provider) throws JsonMappingException {
        final int filteredCount = (this._filteredProps == null) ? 0 : this._filteredProps.length;
        for (int i = 0, len = this._props.length; i < len; ++i) {
            final BeanPropertyWriter prop = this._props[i];
            if (!prop.willSuppressNulls() && !prop.hasNullSerializer()) {
                final JsonSerializer<Object> nullSer = provider.findNullValueSerializer(prop);
                if (nullSer != null) {
                    prop.assignNullSerializer(nullSer);
                    if (i < filteredCount) {
                        final BeanPropertyWriter w2 = this._filteredProps[i];
                        if (w2 != null) {
                            w2.assignNullSerializer(nullSer);
                        }
                    }
                }
            }
            if (!prop.hasSerializer()) {
                JsonSerializer<Object> ser = this.findConvertingSerializer(provider, prop);
                if (ser == null) {
                    JavaType type = prop.getSerializationType();
                    if (type == null) {
                        type = prop.getType();
                        if (!type.isFinal()) {
                            if (type.isContainerType() || type.containedTypeCount() > 0) {
                                prop.setNonTrivialBaseType(type);
                            }
                            continue;
                        }
                    }
                    ser = provider.findValueSerializer(type, prop);
                    if (type.isContainerType()) {
                        final TypeSerializer typeSer = type.getContentType().getTypeHandler();
                        if (typeSer != null && ser instanceof ContainerSerializer) {
                            final JsonSerializer<Object> ser2 = ser = (JsonSerializer<Object>)((ContainerSerializer)ser).withValueTypeSerializer(typeSer);
                        }
                    }
                }
                if (i < filteredCount) {
                    final BeanPropertyWriter w2 = this._filteredProps[i];
                    if (w2 != null) {
                        w2.assignSerializer(ser);
                        continue;
                    }
                }
                prop.assignSerializer(ser);
            }
        }
        if (this._anyGetterWriter != null) {
            this._anyGetterWriter.resolve(provider);
        }
    }
    
    protected JsonSerializer<Object> findConvertingSerializer(final SerializerProvider provider, final BeanPropertyWriter prop) throws JsonMappingException {
        final AnnotationIntrospector intr = provider.getAnnotationIntrospector();
        if (intr != null) {
            final AnnotatedMember m = prop.getMember();
            if (m != null) {
                final Object convDef = intr.findSerializationConverter(m);
                if (convDef != null) {
                    final Converter<Object, Object> conv = provider.converterInstance(prop.getMember(), convDef);
                    final JavaType delegateType = conv.getOutputType(provider.getTypeFactory());
                    final JsonSerializer<?> ser = delegateType.isJavaLangObject() ? null : provider.findValueSerializer(delegateType, prop);
                    return new StdDelegatingSerializer(conv, delegateType, ser);
                }
            }
        }
        return null;
    }
    
    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider provider, final BeanProperty property) throws JsonMappingException {
        final AnnotationIntrospector intr = provider.getAnnotationIntrospector();
        final AnnotatedMember accessor = (property == null || intr == null) ? null : property.getMember();
        final SerializationConfig config = provider.getConfig();
        final JsonFormat.Value format = this.findFormatOverrides(provider, property, this.handledType());
        JsonFormat.Shape shape = null;
        if (format != null && format.hasShape()) {
            shape = format.getShape();
            if (shape != JsonFormat.Shape.ANY && shape != this._serializationShape) {
                if (this._handledType.isEnum()) {
                    switch (shape) {
                        case STRING:
                        case NUMBER:
                        case NUMBER_INT: {
                            final BeanDescription desc = config.introspectClassAnnotations(this._beanType);
                            final JsonSerializer<?> ser = EnumSerializer.construct(this._beanType.getRawClass(), provider.getConfig(), desc, format);
                            return provider.handlePrimaryContextualization(ser, property);
                        }
                    }
                }
                else if (shape == JsonFormat.Shape.NATURAL) {
                    if (!this._beanType.isMapLikeType() || !Map.class.isAssignableFrom(this._handledType)) {
                        if (Map.Entry.class.isAssignableFrom(this._handledType)) {
                            final JavaType mapEntryType = this._beanType.findSuperType(Map.Entry.class);
                            final JavaType kt = mapEntryType.containedTypeOrUnknown(0);
                            final JavaType vt = mapEntryType.containedTypeOrUnknown(1);
                            final JsonSerializer<?> ser2 = new MapEntrySerializer(this._beanType, kt, vt, false, null, property);
                            return provider.handlePrimaryContextualization(ser2, property);
                        }
                    }
                }
            }
        }
        ObjectIdWriter oiw = this._objectIdWriter;
        Set<String> ignoredProps = null;
        Object newFilterId = null;
        if (accessor != null) {
            final JsonIgnoreProperties.Value ignorals = intr.findPropertyIgnorals(accessor);
            if (ignorals != null) {
                ignoredProps = ignorals.findIgnoredForSerialization();
            }
            ObjectIdInfo objectIdInfo = intr.findObjectIdInfo(accessor);
            if (objectIdInfo == null) {
                if (oiw != null) {
                    objectIdInfo = intr.findObjectReferenceInfo(accessor, null);
                    if (objectIdInfo != null) {
                        oiw = this._objectIdWriter.withAlwaysAsId(objectIdInfo.getAlwaysAsId());
                    }
                }
            }
            else {
                objectIdInfo = intr.findObjectReferenceInfo(accessor, objectIdInfo);
                final Class<?> implClass = objectIdInfo.getGeneratorType();
                final JavaType type = provider.constructType(implClass);
                JavaType idType = provider.getTypeFactory().findTypeParameters(type, ObjectIdGenerator.class)[0];
                if (implClass == ObjectIdGenerators.PropertyGenerator.class) {
                    final String propName = objectIdInfo.getPropertyName().getSimpleName();
                    BeanPropertyWriter idProp = null;
                    int i = 0;
                    final int len = this._props.length;
                    BeanPropertyWriter prop;
                    while (true) {
                        if (i == len) {
                            provider.reportBadDefinition(this._beanType, String.format("Invalid Object Id definition for %s: cannot find property with name '%s'", this.handledType().getName(), propName));
                        }
                        prop = this._props[i];
                        if (propName.equals(prop.getName())) {
                            break;
                        }
                        ++i;
                    }
                    idProp = prop;
                    if (i > 0) {
                        System.arraycopy(this._props, 0, this._props, 1, i);
                        this._props[0] = idProp;
                        if (this._filteredProps != null) {
                            final BeanPropertyWriter fp = this._filteredProps[i];
                            System.arraycopy(this._filteredProps, 0, this._filteredProps, 1, i);
                            this._filteredProps[0] = fp;
                        }
                    }
                    idType = idProp.getType();
                    final ObjectIdGenerator<?> gen = new PropertyBasedObjectIdGenerator(objectIdInfo, idProp);
                    oiw = ObjectIdWriter.construct(idType, null, gen, objectIdInfo.getAlwaysAsId());
                }
                else {
                    final ObjectIdGenerator<?> gen = provider.objectIdGeneratorInstance(accessor, objectIdInfo);
                    oiw = ObjectIdWriter.construct(idType, objectIdInfo.getPropertyName(), gen, objectIdInfo.getAlwaysAsId());
                }
            }
            final Object filterId = intr.findFilterId(accessor);
            if (filterId != null && (this._propertyFilterId == null || !filterId.equals(this._propertyFilterId))) {
                newFilterId = filterId;
            }
        }
        BeanSerializerBase contextual = this;
        if (oiw != null) {
            final JsonSerializer<?> ser3 = provider.findValueSerializer(oiw.idType, property);
            oiw = oiw.withSerializer(ser3);
            if (oiw != this._objectIdWriter) {
                contextual = contextual.withObjectIdWriter(oiw);
            }
        }
        if (ignoredProps != null && !ignoredProps.isEmpty()) {
            contextual = contextual.withIgnorals(ignoredProps);
        }
        if (newFilterId != null) {
            contextual = contextual.withFilterId(newFilterId);
        }
        if (shape == null) {
            shape = this._serializationShape;
        }
        if (shape == JsonFormat.Shape.ARRAY) {
            return contextual.asArraySerializer();
        }
        return contextual;
    }
    
    @Override
    public Iterator<PropertyWriter> properties() {
        return (Iterator<PropertyWriter>)Arrays.asList(this._props).iterator();
    }
    
    @Override
    public boolean usesObjectId() {
        return this._objectIdWriter != null;
    }
    
    @Override
    public abstract void serialize(final Object p0, final JsonGenerator p1, final SerializerProvider p2) throws IOException;
    
    @Override
    public void serializeWithType(final Object bean, final JsonGenerator gen, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
        if (this._objectIdWriter != null) {
            gen.setCurrentValue(bean);
            this._serializeWithObjectId(bean, gen, provider, typeSer);
            return;
        }
        gen.setCurrentValue(bean);
        final WritableTypeId typeIdDef = this._typeIdDef(typeSer, bean, JsonToken.START_OBJECT);
        typeSer.writeTypePrefix(gen, typeIdDef);
        if (this._propertyFilterId != null) {
            this.serializeFieldsFiltered(bean, gen, provider);
        }
        else {
            this.serializeFields(bean, gen, provider);
        }
        typeSer.writeTypeSuffix(gen, typeIdDef);
    }
    
    protected final void _serializeWithObjectId(final Object bean, final JsonGenerator gen, final SerializerProvider provider, final boolean startEndObject) throws IOException {
        final ObjectIdWriter w = this._objectIdWriter;
        final WritableObjectId objectId = provider.findObjectId(bean, w.generator);
        if (objectId.writeAsId(gen, provider, w)) {
            return;
        }
        final Object id = objectId.generateId(bean);
        if (w.alwaysAsId) {
            w.serializer.serialize(id, gen, provider);
            return;
        }
        if (startEndObject) {
            gen.writeStartObject(bean);
        }
        objectId.writeAsField(gen, provider, w);
        if (this._propertyFilterId != null) {
            this.serializeFieldsFiltered(bean, gen, provider);
        }
        else {
            this.serializeFields(bean, gen, provider);
        }
        if (startEndObject) {
            gen.writeEndObject();
        }
    }
    
    protected final void _serializeWithObjectId(final Object bean, final JsonGenerator gen, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
        final ObjectIdWriter w = this._objectIdWriter;
        final WritableObjectId objectId = provider.findObjectId(bean, w.generator);
        if (objectId.writeAsId(gen, provider, w)) {
            return;
        }
        final Object id = objectId.generateId(bean);
        if (w.alwaysAsId) {
            w.serializer.serialize(id, gen, provider);
            return;
        }
        this._serializeObjectId(bean, gen, provider, typeSer, objectId);
    }
    
    protected void _serializeObjectId(final Object bean, final JsonGenerator g, final SerializerProvider provider, final TypeSerializer typeSer, final WritableObjectId objectId) throws IOException {
        final ObjectIdWriter w = this._objectIdWriter;
        final WritableTypeId typeIdDef = this._typeIdDef(typeSer, bean, JsonToken.START_OBJECT);
        typeSer.writeTypePrefix(g, typeIdDef);
        objectId.writeAsField(g, provider, w);
        if (this._propertyFilterId != null) {
            this.serializeFieldsFiltered(bean, g, provider);
        }
        else {
            this.serializeFields(bean, g, provider);
        }
        typeSer.writeTypeSuffix(g, typeIdDef);
    }
    
    protected final WritableTypeId _typeIdDef(final TypeSerializer typeSer, final Object bean, final JsonToken valueShape) {
        if (this._typeId == null) {
            return typeSer.typeId(bean, valueShape);
        }
        Object typeId = this._typeId.getValue(bean);
        if (typeId == null) {
            typeId = "";
        }
        return typeSer.typeId(bean, valueShape, typeId);
    }
    
    @Deprecated
    protected final String _customTypeId(final Object bean) {
        final Object typeId = this._typeId.getValue(bean);
        if (typeId == null) {
            return "";
        }
        return (String)((typeId instanceof String) ? typeId : typeId.toString());
    }
    
    protected void serializeFields(final Object bean, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        BeanPropertyWriter[] props;
        if (this._filteredProps != null && provider.getActiveView() != null) {
            props = this._filteredProps;
        }
        else {
            props = this._props;
        }
        int i = 0;
        try {
            for (int len = props.length; i < len; ++i) {
                final BeanPropertyWriter prop = props[i];
                if (prop != null) {
                    prop.serializeAsField(bean, gen, provider);
                }
            }
            if (this._anyGetterWriter != null) {
                this._anyGetterWriter.getAndSerialize(bean, gen, provider);
            }
        }
        catch (Exception e) {
            final String name = (i == props.length) ? "[anySetter]" : props[i].getName();
            this.wrapAndThrow(provider, e, bean, name);
        }
        catch (StackOverflowError e2) {
            final JsonMappingException mapE = new JsonMappingException(gen, "Infinite recursion (StackOverflowError)", e2);
            final String name2 = (i == props.length) ? "[anySetter]" : props[i].getName();
            mapE.prependPath(new JsonMappingException.Reference(bean, name2));
            throw mapE;
        }
    }
    
    protected void serializeFieldsFiltered(final Object bean, final JsonGenerator gen, final SerializerProvider provider) throws IOException, JsonGenerationException {
        BeanPropertyWriter[] props;
        if (this._filteredProps != null && provider.getActiveView() != null) {
            props = this._filteredProps;
        }
        else {
            props = this._props;
        }
        final PropertyFilter filter = this.findPropertyFilter(provider, this._propertyFilterId, bean);
        if (filter == null) {
            this.serializeFields(bean, gen, provider);
            return;
        }
        int i = 0;
        try {
            for (int len = props.length; i < len; ++i) {
                final BeanPropertyWriter prop = props[i];
                if (prop != null) {
                    filter.serializeAsField(bean, gen, provider, prop);
                }
            }
            if (this._anyGetterWriter != null) {
                this._anyGetterWriter.getAndFilter(bean, gen, provider, filter);
            }
        }
        catch (Exception e) {
            final String name = (i == props.length) ? "[anySetter]" : props[i].getName();
            this.wrapAndThrow(provider, e, bean, name);
        }
        catch (StackOverflowError e2) {
            final JsonMappingException mapE = new JsonMappingException(gen, "Infinite recursion (StackOverflowError)", e2);
            final String name2 = (i == props.length) ? "[anySetter]" : props[i].getName();
            mapE.prependPath(new JsonMappingException.Reference(bean, name2));
            throw mapE;
        }
    }
    
    @Deprecated
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) throws JsonMappingException {
        final ObjectNode o = this.createSchemaNode("object", true);
        final JsonSerializableSchema ann = this._handledType.getAnnotation(JsonSerializableSchema.class);
        if (ann != null) {
            final String id = ann.id();
            if (id != null && id.length() > 0) {
                o.put("id", id);
            }
        }
        final ObjectNode propertiesNode = o.objectNode();
        PropertyFilter filter;
        if (this._propertyFilterId != null) {
            filter = this.findPropertyFilter(provider, this._propertyFilterId, null);
        }
        else {
            filter = null;
        }
        for (int i = 0; i < this._props.length; ++i) {
            final BeanPropertyWriter prop = this._props[i];
            if (filter == null) {
                prop.depositSchemaProperty(propertiesNode, provider);
            }
            else {
                filter.depositSchemaProperty(prop, propertiesNode, provider);
            }
        }
        o.set("properties", propertiesNode);
        return o;
    }
    
    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
        if (visitor == null) {
            return;
        }
        final JsonObjectFormatVisitor objectVisitor = visitor.expectObjectFormat(typeHint);
        if (objectVisitor == null) {
            return;
        }
        final SerializerProvider provider = visitor.getProvider();
        if (this._propertyFilterId != null) {
            final PropertyFilter filter = this.findPropertyFilter(visitor.getProvider(), this._propertyFilterId, null);
            for (int i = 0, end = this._props.length; i < end; ++i) {
                filter.depositSchemaProperty(this._props[i], objectVisitor, provider);
            }
        }
        else {
            final Class<?> view = (this._filteredProps == null || provider == null) ? null : provider.getActiveView();
            BeanPropertyWriter[] props;
            if (view != null) {
                props = this._filteredProps;
            }
            else {
                props = this._props;
            }
            for (int j = 0, end2 = props.length; j < end2; ++j) {
                final BeanPropertyWriter prop = props[j];
                if (prop != null) {
                    prop.depositSchemaProperty(objectVisitor, provider);
                }
            }
        }
    }
    
    static {
        NAME_FOR_OBJECT_REF = new PropertyName("#object-ref");
        NO_PROPS = new BeanPropertyWriter[0];
    }
}
