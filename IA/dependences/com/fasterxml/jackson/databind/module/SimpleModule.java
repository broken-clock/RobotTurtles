// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.module;

import com.fasterxml.jackson.databind.deser.ValueInstantiators;
import com.fasterxml.jackson.databind.AbstractTypeResolver;
import com.fasterxml.jackson.databind.deser.KeyDeserializers;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import java.util.Iterator;
import java.util.Collection;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import java.util.List;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.util.Map;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import java.util.LinkedHashSet;
import java.util.HashMap;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.core.Version;
import java.io.Serializable;
import com.fasterxml.jackson.databind.Module;

public class SimpleModule extends Module implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final String _name;
    protected final Version _version;
    protected SimpleSerializers _serializers;
    protected SimpleDeserializers _deserializers;
    protected SimpleSerializers _keySerializers;
    protected SimpleKeyDeserializers _keyDeserializers;
    protected SimpleAbstractTypeResolver _abstractTypes;
    protected SimpleValueInstantiators _valueInstantiators;
    protected BeanDeserializerModifier _deserializerModifier;
    protected BeanSerializerModifier _serializerModifier;
    protected HashMap<Class<?>, Class<?>> _mixins;
    protected LinkedHashSet<NamedType> _subtypes;
    protected PropertyNamingStrategy _namingStrategy;
    
    public SimpleModule() {
        this._serializers = null;
        this._deserializers = null;
        this._keySerializers = null;
        this._keyDeserializers = null;
        this._abstractTypes = null;
        this._valueInstantiators = null;
        this._deserializerModifier = null;
        this._serializerModifier = null;
        this._mixins = null;
        this._subtypes = null;
        this._namingStrategy = null;
        this._name = ((this.getClass() == SimpleModule.class) ? ("SimpleModule-" + System.identityHashCode(this)) : this.getClass().getName());
        this._version = Version.unknownVersion();
    }
    
    public SimpleModule(final String name) {
        this(name, Version.unknownVersion());
    }
    
    public SimpleModule(final Version version) {
        this._serializers = null;
        this._deserializers = null;
        this._keySerializers = null;
        this._keyDeserializers = null;
        this._abstractTypes = null;
        this._valueInstantiators = null;
        this._deserializerModifier = null;
        this._serializerModifier = null;
        this._mixins = null;
        this._subtypes = null;
        this._namingStrategy = null;
        this._name = version.getArtifactId();
        this._version = version;
    }
    
    public SimpleModule(final String name, final Version version) {
        this._serializers = null;
        this._deserializers = null;
        this._keySerializers = null;
        this._keyDeserializers = null;
        this._abstractTypes = null;
        this._valueInstantiators = null;
        this._deserializerModifier = null;
        this._serializerModifier = null;
        this._mixins = null;
        this._subtypes = null;
        this._namingStrategy = null;
        this._name = name;
        this._version = version;
    }
    
    public SimpleModule(final String name, final Version version, final Map<Class<?>, JsonDeserializer<?>> deserializers) {
        this(name, version, deserializers, null);
    }
    
    public SimpleModule(final String name, final Version version, final List<JsonSerializer<?>> serializers) {
        this(name, version, null, serializers);
    }
    
    public SimpleModule(final String name, final Version version, final Map<Class<?>, JsonDeserializer<?>> deserializers, final List<JsonSerializer<?>> serializers) {
        this._serializers = null;
        this._deserializers = null;
        this._keySerializers = null;
        this._keyDeserializers = null;
        this._abstractTypes = null;
        this._valueInstantiators = null;
        this._deserializerModifier = null;
        this._serializerModifier = null;
        this._mixins = null;
        this._subtypes = null;
        this._namingStrategy = null;
        this._name = name;
        this._version = version;
        if (deserializers != null) {
            this._deserializers = new SimpleDeserializers(deserializers);
        }
        if (serializers != null) {
            this._serializers = new SimpleSerializers(serializers);
        }
    }
    
    @Override
    public Object getTypeId() {
        if (this.getClass() == SimpleModule.class) {
            return null;
        }
        return super.getTypeId();
    }
    
    public void setSerializers(final SimpleSerializers s) {
        this._serializers = s;
    }
    
    public void setDeserializers(final SimpleDeserializers d) {
        this._deserializers = d;
    }
    
    public void setKeySerializers(final SimpleSerializers ks) {
        this._keySerializers = ks;
    }
    
    public void setKeyDeserializers(final SimpleKeyDeserializers kd) {
        this._keyDeserializers = kd;
    }
    
    public void setAbstractTypes(final SimpleAbstractTypeResolver atr) {
        this._abstractTypes = atr;
    }
    
    public void setValueInstantiators(final SimpleValueInstantiators svi) {
        this._valueInstantiators = svi;
    }
    
    public SimpleModule setDeserializerModifier(final BeanDeserializerModifier mod) {
        this._deserializerModifier = mod;
        return this;
    }
    
    public SimpleModule setSerializerModifier(final BeanSerializerModifier mod) {
        this._serializerModifier = mod;
        return this;
    }
    
    protected SimpleModule setNamingStrategy(final PropertyNamingStrategy naming) {
        this._namingStrategy = naming;
        return this;
    }
    
    public SimpleModule addSerializer(final JsonSerializer<?> ser) {
        this._checkNotNull(ser, "serializer");
        if (this._serializers == null) {
            this._serializers = new SimpleSerializers();
        }
        this._serializers.addSerializer(ser);
        return this;
    }
    
    public <T> SimpleModule addSerializer(final Class<? extends T> type, final JsonSerializer<T> ser) {
        this._checkNotNull(type, "type to register serializer for");
        this._checkNotNull(ser, "serializer");
        if (this._serializers == null) {
            this._serializers = new SimpleSerializers();
        }
        this._serializers.addSerializer(type, ser);
        return this;
    }
    
    public <T> SimpleModule addKeySerializer(final Class<? extends T> type, final JsonSerializer<T> ser) {
        this._checkNotNull(type, "type to register key serializer for");
        this._checkNotNull(ser, "key serializer");
        if (this._keySerializers == null) {
            this._keySerializers = new SimpleSerializers();
        }
        this._keySerializers.addSerializer(type, ser);
        return this;
    }
    
    public <T> SimpleModule addDeserializer(final Class<T> type, final JsonDeserializer<? extends T> deser) {
        this._checkNotNull(type, "type to register deserializer for");
        this._checkNotNull(deser, "deserializer");
        if (this._deserializers == null) {
            this._deserializers = new SimpleDeserializers();
        }
        this._deserializers.addDeserializer(type, deser);
        return this;
    }
    
    public SimpleModule addKeyDeserializer(final Class<?> type, final KeyDeserializer deser) {
        this._checkNotNull(type, "type to register key deserializer for");
        this._checkNotNull(deser, "key deserializer");
        if (this._keyDeserializers == null) {
            this._keyDeserializers = new SimpleKeyDeserializers();
        }
        this._keyDeserializers.addDeserializer(type, deser);
        return this;
    }
    
    public <T> SimpleModule addAbstractTypeMapping(final Class<T> superType, final Class<? extends T> subType) {
        this._checkNotNull(superType, "abstract type to map");
        this._checkNotNull(subType, "concrete type to map to");
        if (this._abstractTypes == null) {
            this._abstractTypes = new SimpleAbstractTypeResolver();
        }
        this._abstractTypes = this._abstractTypes.addMapping(superType, subType);
        return this;
    }
    
    public SimpleModule registerSubtypes(final Class<?>... subtypes) {
        if (this._subtypes == null) {
            this._subtypes = new LinkedHashSet<NamedType>();
        }
        for (final Class<?> subtype : subtypes) {
            this._checkNotNull(subtype, "subtype to register");
            this._subtypes.add(new NamedType(subtype));
        }
        return this;
    }
    
    public SimpleModule registerSubtypes(final NamedType... subtypes) {
        if (this._subtypes == null) {
            this._subtypes = new LinkedHashSet<NamedType>();
        }
        for (final NamedType subtype : subtypes) {
            this._checkNotNull(subtype, "subtype to register");
            this._subtypes.add(subtype);
        }
        return this;
    }
    
    public SimpleModule registerSubtypes(final Collection<Class<?>> subtypes) {
        if (this._subtypes == null) {
            this._subtypes = new LinkedHashSet<NamedType>();
        }
        for (final Class<?> subtype : subtypes) {
            this._checkNotNull(subtype, "subtype to register");
            this._subtypes.add(new NamedType(subtype));
        }
        return this;
    }
    
    public SimpleModule addValueInstantiator(final Class<?> beanType, final ValueInstantiator inst) {
        this._checkNotNull(beanType, "class to register value instantiator for");
        this._checkNotNull(inst, "value instantiator");
        if (this._valueInstantiators == null) {
            this._valueInstantiators = new SimpleValueInstantiators();
        }
        this._valueInstantiators = this._valueInstantiators.addValueInstantiator(beanType, inst);
        return this;
    }
    
    public SimpleModule setMixInAnnotation(final Class<?> targetType, final Class<?> mixinClass) {
        this._checkNotNull(targetType, "target type");
        this._checkNotNull(mixinClass, "mixin class");
        if (this._mixins == null) {
            this._mixins = new HashMap<Class<?>, Class<?>>();
        }
        this._mixins.put(targetType, mixinClass);
        return this;
    }
    
    @Override
    public String getModuleName() {
        return this._name;
    }
    
    @Override
    public void setupModule(final SetupContext context) {
        if (this._serializers != null) {
            context.addSerializers(this._serializers);
        }
        if (this._deserializers != null) {
            context.addDeserializers(this._deserializers);
        }
        if (this._keySerializers != null) {
            context.addKeySerializers(this._keySerializers);
        }
        if (this._keyDeserializers != null) {
            context.addKeyDeserializers(this._keyDeserializers);
        }
        if (this._abstractTypes != null) {
            context.addAbstractTypeResolver(this._abstractTypes);
        }
        if (this._valueInstantiators != null) {
            context.addValueInstantiators(this._valueInstantiators);
        }
        if (this._deserializerModifier != null) {
            context.addBeanDeserializerModifier(this._deserializerModifier);
        }
        if (this._serializerModifier != null) {
            context.addBeanSerializerModifier(this._serializerModifier);
        }
        if (this._subtypes != null && this._subtypes.size() > 0) {
            context.registerSubtypes((NamedType[])this._subtypes.toArray(new NamedType[this._subtypes.size()]));
        }
        if (this._namingStrategy != null) {
            context.setNamingStrategy(this._namingStrategy);
        }
        if (this._mixins != null) {
            for (final Map.Entry<Class<?>, Class<?>> entry : this._mixins.entrySet()) {
                context.setMixInAnnotations(entry.getKey(), entry.getValue());
            }
        }
    }
    
    @Override
    public Version version() {
        return this._version;
    }
    
    protected void _checkNotNull(final Object thingy, final String type) {
        if (thingy == null) {
            throw new IllegalArgumentException(String.format("Cannot pass `null` as %s", type));
        }
    }
}
