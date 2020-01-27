// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import java.util.Collections;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.util.Set;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdValueProperty;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.util.Iterator;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.util.Annotations;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.PropertyName;
import java.util.Collection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import java.util.HashSet;
import java.util.HashMap;
import com.fasterxml.jackson.databind.deser.impl.ValueInjector;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationConfig;

public class BeanDeserializerBuilder
{
    protected final DeserializationConfig _config;
    protected final DeserializationContext _context;
    protected final BeanDescription _beanDesc;
    protected final Map<String, SettableBeanProperty> _properties;
    protected List<ValueInjector> _injectables;
    protected HashMap<String, SettableBeanProperty> _backRefProperties;
    protected HashSet<String> _ignorableProps;
    protected ValueInstantiator _valueInstantiator;
    protected ObjectIdReader _objectIdReader;
    protected SettableAnyProperty _anySetter;
    protected boolean _ignoreAllUnknown;
    protected AnnotatedMethod _buildMethod;
    protected JsonPOJOBuilder.Value _builderConfig;
    
    public BeanDeserializerBuilder(final BeanDescription beanDesc, final DeserializationContext ctxt) {
        this._properties = new LinkedHashMap<String, SettableBeanProperty>();
        this._beanDesc = beanDesc;
        this._context = ctxt;
        this._config = ctxt.getConfig();
    }
    
    protected BeanDeserializerBuilder(final BeanDeserializerBuilder src) {
        this._properties = new LinkedHashMap<String, SettableBeanProperty>();
        this._beanDesc = src._beanDesc;
        this._context = src._context;
        this._config = src._config;
        this._properties.putAll(src._properties);
        this._injectables = _copy(src._injectables);
        this._backRefProperties = _copy(src._backRefProperties);
        this._ignorableProps = src._ignorableProps;
        this._valueInstantiator = src._valueInstantiator;
        this._objectIdReader = src._objectIdReader;
        this._anySetter = src._anySetter;
        this._ignoreAllUnknown = src._ignoreAllUnknown;
        this._buildMethod = src._buildMethod;
        this._builderConfig = src._builderConfig;
    }
    
    private static HashMap<String, SettableBeanProperty> _copy(final HashMap<String, SettableBeanProperty> src) {
        return (src == null) ? null : new HashMap<String, SettableBeanProperty>(src);
    }
    
    private static <T> List<T> _copy(final List<T> src) {
        return (src == null) ? null : new ArrayList<T>((Collection<? extends T>)src);
    }
    
    public void addOrReplaceProperty(final SettableBeanProperty prop, final boolean allowOverride) {
        this._properties.put(prop.getName(), prop);
    }
    
    public void addProperty(final SettableBeanProperty prop) {
        final SettableBeanProperty old = this._properties.put(prop.getName(), prop);
        if (old != null && old != prop) {
            throw new IllegalArgumentException("Duplicate property '" + prop.getName() + "' for " + this._beanDesc.getType());
        }
    }
    
    public void addBackReferenceProperty(final String referenceName, final SettableBeanProperty prop) {
        if (this._backRefProperties == null) {
            this._backRefProperties = new HashMap<String, SettableBeanProperty>(4);
        }
        prop.fixAccess(this._config);
        this._backRefProperties.put(referenceName, prop);
    }
    
    public void addInjectable(final PropertyName propName, final JavaType propType, final Annotations contextAnnotations, final AnnotatedMember member, final Object valueId) {
        if (this._injectables == null) {
            this._injectables = new ArrayList<ValueInjector>();
        }
        final boolean fixAccess = this._config.canOverrideAccessModifiers();
        final boolean forceAccess = fixAccess && this._config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS);
        if (fixAccess) {
            member.fixAccess(forceAccess);
        }
        this._injectables.add(new ValueInjector(propName, propType, member, valueId));
    }
    
    public void addIgnorable(final String propName) {
        if (this._ignorableProps == null) {
            this._ignorableProps = new HashSet<String>();
        }
        this._ignorableProps.add(propName);
    }
    
    public void addCreatorProperty(final SettableBeanProperty prop) {
        this.addProperty(prop);
    }
    
    public void setAnySetter(final SettableAnyProperty s) {
        if (this._anySetter != null && s != null) {
            throw new IllegalStateException("_anySetter already set to non-null");
        }
        this._anySetter = s;
    }
    
    public void setIgnoreUnknownProperties(final boolean ignore) {
        this._ignoreAllUnknown = ignore;
    }
    
    public void setValueInstantiator(final ValueInstantiator inst) {
        this._valueInstantiator = inst;
    }
    
    public void setObjectIdReader(final ObjectIdReader r) {
        this._objectIdReader = r;
    }
    
    public void setPOJOBuilder(final AnnotatedMethod buildMethod, final JsonPOJOBuilder.Value config) {
        this._buildMethod = buildMethod;
        this._builderConfig = config;
    }
    
    public Iterator<SettableBeanProperty> getProperties() {
        return this._properties.values().iterator();
    }
    
    public SettableBeanProperty findProperty(final PropertyName propertyName) {
        return this._properties.get(propertyName.getSimpleName());
    }
    
    public boolean hasProperty(final PropertyName propertyName) {
        return this.findProperty(propertyName) != null;
    }
    
    public SettableBeanProperty removeProperty(final PropertyName name) {
        return this._properties.remove(name.getSimpleName());
    }
    
    public SettableAnyProperty getAnySetter() {
        return this._anySetter;
    }
    
    public ValueInstantiator getValueInstantiator() {
        return this._valueInstantiator;
    }
    
    public List<ValueInjector> getInjectables() {
        return this._injectables;
    }
    
    public ObjectIdReader getObjectIdReader() {
        return this._objectIdReader;
    }
    
    public AnnotatedMethod getBuildMethod() {
        return this._buildMethod;
    }
    
    public JsonPOJOBuilder.Value getBuilderConfig() {
        return this._builderConfig;
    }
    
    public boolean hasIgnorable(final String name) {
        return this._ignorableProps != null && this._ignorableProps.contains(name);
    }
    
    public JsonDeserializer<?> build() {
        final Collection<SettableBeanProperty> props = this._properties.values();
        this._fixAccess(props);
        BeanPropertyMap propertyMap = BeanPropertyMap.construct(props, this._config.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES), this._collectAliases(props));
        propertyMap.assignIndexes();
        boolean anyViews = !this._config.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION);
        if (!anyViews) {
            for (final SettableBeanProperty prop : props) {
                if (prop.hasViews()) {
                    anyViews = true;
                    break;
                }
            }
        }
        if (this._objectIdReader != null) {
            final ObjectIdValueProperty prop2 = new ObjectIdValueProperty(this._objectIdReader, PropertyMetadata.STD_REQUIRED);
            propertyMap = propertyMap.withProperty(prop2);
        }
        return new BeanDeserializer(this, this._beanDesc, propertyMap, this._backRefProperties, this._ignorableProps, this._ignoreAllUnknown, anyViews);
    }
    
    public AbstractDeserializer buildAbstract() {
        return new AbstractDeserializer(this, this._beanDesc, this._backRefProperties, this._properties);
    }
    
    public JsonDeserializer<?> buildBuilderBased(final JavaType valueType, final String expBuildMethodName) throws JsonMappingException {
        if (this._buildMethod == null) {
            if (!expBuildMethodName.isEmpty()) {
                this._context.reportBadDefinition(this._beanDesc.getType(), String.format("Builder class %s does not have build method (name: '%s')", this._beanDesc.getBeanClass().getName(), expBuildMethodName));
            }
        }
        else {
            final Class<?> rawBuildType = this._buildMethod.getRawReturnType();
            final Class<?> rawValueType = valueType.getRawClass();
            if (rawBuildType != rawValueType && !rawBuildType.isAssignableFrom(rawValueType) && !rawValueType.isAssignableFrom(rawBuildType)) {
                this._context.reportBadDefinition(this._beanDesc.getType(), String.format("Build method '%s' has wrong return type (%s), not compatible with POJO type (%s)", this._buildMethod.getFullName(), rawBuildType.getName(), valueType.getRawClass().getName()));
            }
        }
        final Collection<SettableBeanProperty> props = this._properties.values();
        this._fixAccess(props);
        BeanPropertyMap propertyMap = BeanPropertyMap.construct(props, this._config.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES), this._collectAliases(props));
        propertyMap.assignIndexes();
        boolean anyViews = !this._config.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION);
        if (!anyViews) {
            for (final SettableBeanProperty prop : props) {
                if (prop.hasViews()) {
                    anyViews = true;
                    break;
                }
            }
        }
        if (this._objectIdReader != null) {
            final ObjectIdValueProperty prop2 = new ObjectIdValueProperty(this._objectIdReader, PropertyMetadata.STD_REQUIRED);
            propertyMap = propertyMap.withProperty(prop2);
        }
        return new BuilderBasedDeserializer(this, this._beanDesc, valueType, propertyMap, this._backRefProperties, this._ignorableProps, this._ignoreAllUnknown, anyViews);
    }
    
    protected void _fixAccess(final Collection<SettableBeanProperty> mainProps) {
        for (final SettableBeanProperty prop : mainProps) {
            prop.fixAccess(this._config);
        }
        if (this._anySetter != null) {
            this._anySetter.fixAccess(this._config);
        }
        if (this._buildMethod != null) {
            this._buildMethod.fixAccess(this._config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
        }
    }
    
    protected Map<String, List<PropertyName>> _collectAliases(final Collection<SettableBeanProperty> props) {
        Map<String, List<PropertyName>> mapping = null;
        final AnnotationIntrospector intr = this._config.getAnnotationIntrospector();
        if (intr != null) {
            for (final SettableBeanProperty prop : props) {
                final List<PropertyName> aliases = intr.findPropertyAliases(prop.getMember());
                if (aliases != null) {
                    if (aliases.isEmpty()) {
                        continue;
                    }
                    if (mapping == null) {
                        mapping = new HashMap<String, List<PropertyName>>();
                    }
                    mapping.put(prop.getName(), aliases);
                }
            }
        }
        if (mapping == null) {
            return Collections.emptyMap();
        }
        return mapping;
    }
}
