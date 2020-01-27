// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.util.TreeMap;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.PropertyName;
import java.lang.reflect.Modifier;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.MapperFeature;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.LinkedHashMap;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.cfg.MapperConfig;

public class POJOPropertiesCollector
{
    protected final MapperConfig<?> _config;
    protected final boolean _forSerialization;
    protected final boolean _stdBeanNaming;
    protected final JavaType _type;
    protected final AnnotatedClass _classDef;
    protected final VisibilityChecker<?> _visibilityChecker;
    protected final AnnotationIntrospector _annotationIntrospector;
    protected final boolean _useAnnotations;
    protected final String _mutatorPrefix;
    protected boolean _collected;
    protected LinkedHashMap<String, POJOPropertyBuilder> _properties;
    protected LinkedList<POJOPropertyBuilder> _creatorProperties;
    protected LinkedList<AnnotatedMember> _anyGetters;
    protected LinkedList<AnnotatedMethod> _anySetters;
    protected LinkedList<AnnotatedMember> _anySetterField;
    protected LinkedList<AnnotatedMember> _jsonValueAccessors;
    protected HashSet<String> _ignoredPropertyNames;
    protected LinkedHashMap<Object, AnnotatedMember> _injectables;
    
    protected POJOPropertiesCollector(final MapperConfig<?> config, final boolean forSerialization, final JavaType type, final AnnotatedClass classDef, final String mutatorPrefix) {
        this._config = config;
        this._stdBeanNaming = config.isEnabled(MapperFeature.USE_STD_BEAN_NAMING);
        this._forSerialization = forSerialization;
        this._type = type;
        this._classDef = classDef;
        this._mutatorPrefix = ((mutatorPrefix == null) ? "set" : mutatorPrefix);
        if (config.isAnnotationProcessingEnabled()) {
            this._useAnnotations = true;
            this._annotationIntrospector = this._config.getAnnotationIntrospector();
        }
        else {
            this._useAnnotations = false;
            this._annotationIntrospector = AnnotationIntrospector.nopInstance();
        }
        this._visibilityChecker = this._config.getDefaultVisibilityChecker(type.getRawClass(), classDef);
    }
    
    public MapperConfig<?> getConfig() {
        return this._config;
    }
    
    public JavaType getType() {
        return this._type;
    }
    
    public AnnotatedClass getClassDef() {
        return this._classDef;
    }
    
    public AnnotationIntrospector getAnnotationIntrospector() {
        return this._annotationIntrospector;
    }
    
    public List<BeanPropertyDefinition> getProperties() {
        final Map<String, POJOPropertyBuilder> props = this.getPropertyMap();
        return new ArrayList<BeanPropertyDefinition>(props.values());
    }
    
    public Map<Object, AnnotatedMember> getInjectables() {
        if (!this._collected) {
            this.collectAll();
        }
        return this._injectables;
    }
    
    @Deprecated
    public AnnotatedMethod getJsonValueMethod() {
        final AnnotatedMember m = this.getJsonValueAccessor();
        if (m instanceof AnnotatedMethod) {
            return (AnnotatedMethod)m;
        }
        return null;
    }
    
    public AnnotatedMember getJsonValueAccessor() {
        if (!this._collected) {
            this.collectAll();
        }
        if (this._jsonValueAccessors != null) {
            if (this._jsonValueAccessors.size() > 1) {
                this.reportProblem("Multiple 'as-value' properties defined (%s vs %s)", this._jsonValueAccessors.get(0), this._jsonValueAccessors.get(1));
            }
            return this._jsonValueAccessors.get(0);
        }
        return null;
    }
    
    public AnnotatedMember getAnyGetter() {
        if (!this._collected) {
            this.collectAll();
        }
        if (this._anyGetters != null) {
            if (this._anyGetters.size() > 1) {
                this.reportProblem("Multiple 'any-getters' defined (%s vs %s)", this._anyGetters.get(0), this._anyGetters.get(1));
            }
            return this._anyGetters.getFirst();
        }
        return null;
    }
    
    public AnnotatedMember getAnySetterField() {
        if (!this._collected) {
            this.collectAll();
        }
        if (this._anySetterField != null) {
            if (this._anySetterField.size() > 1) {
                this.reportProblem("Multiple 'any-setter' fields defined (%s vs %s)", this._anySetterField.get(0), this._anySetterField.get(1));
            }
            return this._anySetterField.getFirst();
        }
        return null;
    }
    
    public AnnotatedMethod getAnySetterMethod() {
        if (!this._collected) {
            this.collectAll();
        }
        if (this._anySetters != null) {
            if (this._anySetters.size() > 1) {
                this.reportProblem("Multiple 'any-setter' methods defined (%s vs %s)", this._anySetters.get(0), this._anySetters.get(1));
            }
            return this._anySetters.getFirst();
        }
        return null;
    }
    
    public Set<String> getIgnoredPropertyNames() {
        return this._ignoredPropertyNames;
    }
    
    public ObjectIdInfo getObjectIdInfo() {
        ObjectIdInfo info = this._annotationIntrospector.findObjectIdInfo(this._classDef);
        if (info != null) {
            info = this._annotationIntrospector.findObjectReferenceInfo(this._classDef, info);
        }
        return info;
    }
    
    public Class<?> findPOJOBuilderClass() {
        return this._annotationIntrospector.findPOJOBuilder(this._classDef);
    }
    
    protected Map<String, POJOPropertyBuilder> getPropertyMap() {
        if (!this._collected) {
            this.collectAll();
        }
        return this._properties;
    }
    
    protected void collectAll() {
        final LinkedHashMap<String, POJOPropertyBuilder> props = new LinkedHashMap<String, POJOPropertyBuilder>();
        this._addFields(props);
        this._addMethods(props);
        if (!this._classDef.isNonStaticInnerClass()) {
            this._addCreators(props);
        }
        this._addInjectables(props);
        this._removeUnwantedProperties(props);
        this._removeUnwantedAccessor(props);
        this._renameProperties(props);
        for (final POJOPropertyBuilder property : props.values()) {
            property.mergeAnnotations(this._forSerialization);
        }
        final PropertyNamingStrategy naming = this._findNamingStrategy();
        if (naming != null) {
            this._renameUsing(props, naming);
        }
        for (final POJOPropertyBuilder property2 : props.values()) {
            property2.trimByVisibility();
        }
        if (this._config.isEnabled(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME)) {
            this._renameWithWrappers(props);
        }
        this._sortProperties(props);
        this._properties = props;
        this._collected = true;
    }
    
    protected void _addFields(final Map<String, POJOPropertyBuilder> props) {
        final AnnotationIntrospector ai = this._annotationIntrospector;
        final boolean pruneFinalFields = !this._forSerialization && !this._config.isEnabled(MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS);
        final boolean transientAsIgnoral = this._config.isEnabled(MapperFeature.PROPAGATE_TRANSIENT_MARKER);
        for (final AnnotatedField f : this._classDef.fields()) {
            String implName = ai.findImplicitPropertyName(f);
            if (Boolean.TRUE.equals(ai.hasAsValue(f))) {
                if (this._jsonValueAccessors == null) {
                    this._jsonValueAccessors = new LinkedList<AnnotatedMember>();
                }
                this._jsonValueAccessors.add(f);
            }
            else if (Boolean.TRUE.equals(ai.hasAnySetter(f))) {
                if (this._anySetterField == null) {
                    this._anySetterField = new LinkedList<AnnotatedMember>();
                }
                this._anySetterField.add(f);
            }
            else {
                if (implName == null) {
                    implName = f.getName();
                }
                PropertyName pn;
                if (this._forSerialization) {
                    pn = ai.findNameForSerialization(f);
                }
                else {
                    pn = ai.findNameForDeserialization(f);
                }
                boolean nameExplicit;
                final boolean hasName = nameExplicit = (pn != null);
                if (nameExplicit && pn.isEmpty()) {
                    pn = this._propNameFromSimple(implName);
                    nameExplicit = false;
                }
                boolean visible = pn != null;
                if (!visible) {
                    visible = this._visibilityChecker.isFieldVisible(f);
                }
                boolean ignored = ai.hasIgnoreMarker(f);
                if (f.isTransient() && !hasName) {
                    visible = false;
                    if (transientAsIgnoral) {
                        ignored = true;
                    }
                }
                if (pruneFinalFields && pn == null && !ignored && Modifier.isFinal(f.getModifiers())) {
                    continue;
                }
                this._property(props, implName).addField(f, pn, nameExplicit, visible, ignored);
            }
        }
    }
    
    protected void _addCreators(final Map<String, POJOPropertyBuilder> props) {
        if (!this._useAnnotations) {
            return;
        }
        for (final AnnotatedConstructor ctor : this._classDef.getConstructors()) {
            if (this._creatorProperties == null) {
                this._creatorProperties = new LinkedList<POJOPropertyBuilder>();
            }
            for (int i = 0, len = ctor.getParameterCount(); i < len; ++i) {
                this._addCreatorParam(props, ctor.getParameter(i));
            }
        }
        for (final AnnotatedMethod factory : this._classDef.getFactoryMethods()) {
            if (this._creatorProperties == null) {
                this._creatorProperties = new LinkedList<POJOPropertyBuilder>();
            }
            for (int i = 0, len = factory.getParameterCount(); i < len; ++i) {
                this._addCreatorParam(props, factory.getParameter(i));
            }
        }
    }
    
    protected void _addCreatorParam(final Map<String, POJOPropertyBuilder> props, final AnnotatedParameter param) {
        String impl = this._annotationIntrospector.findImplicitPropertyName(param);
        if (impl == null) {
            impl = "";
        }
        PropertyName pn = this._annotationIntrospector.findNameForDeserialization(param);
        final boolean expl = pn != null && !pn.isEmpty();
        if (!expl) {
            if (impl.isEmpty()) {
                return;
            }
            final JsonCreator.Mode creatorMode = this._annotationIntrospector.findCreatorAnnotation(this._config, param.getOwner());
            if (creatorMode == null || creatorMode == JsonCreator.Mode.DISABLED) {
                return;
            }
            pn = PropertyName.construct(impl);
        }
        final POJOPropertyBuilder prop = (expl && impl.isEmpty()) ? this._property(props, pn) : this._property(props, impl);
        prop.addCtor(param, pn, expl, true, false);
        this._creatorProperties.add(prop);
    }
    
    protected void _addMethods(final Map<String, POJOPropertyBuilder> props) {
        final AnnotationIntrospector ai = this._annotationIntrospector;
        for (final AnnotatedMethod m : this._classDef.memberMethods()) {
            final int argCount = m.getParameterCount();
            if (argCount == 0) {
                this._addGetterMethod(props, m, ai);
            }
            else if (argCount == 1) {
                this._addSetterMethod(props, m, ai);
            }
            else {
                if (argCount != 2 || ai == null || !Boolean.TRUE.equals(ai.hasAnySetter(m))) {
                    continue;
                }
                if (this._anySetters == null) {
                    this._anySetters = new LinkedList<AnnotatedMethod>();
                }
                this._anySetters.add(m);
            }
        }
    }
    
    protected void _addGetterMethod(final Map<String, POJOPropertyBuilder> props, final AnnotatedMethod m, final AnnotationIntrospector ai) {
        if (!m.hasReturnType()) {
            return;
        }
        if (Boolean.TRUE.equals(ai.hasAnyGetter(m))) {
            if (this._anyGetters == null) {
                this._anyGetters = new LinkedList<AnnotatedMember>();
            }
            this._anyGetters.add(m);
            return;
        }
        if (Boolean.TRUE.equals(ai.hasAsValue(m))) {
            if (this._jsonValueAccessors == null) {
                this._jsonValueAccessors = new LinkedList<AnnotatedMember>();
            }
            this._jsonValueAccessors.add(m);
            return;
        }
        PropertyName pn = ai.findNameForSerialization(m);
        boolean nameExplicit = pn != null;
        String implName;
        boolean visible;
        if (!nameExplicit) {
            implName = ai.findImplicitPropertyName(m);
            if (implName == null) {
                implName = BeanUtil.okNameForRegularGetter(m, m.getName(), this._stdBeanNaming);
            }
            if (implName == null) {
                implName = BeanUtil.okNameForIsGetter(m, m.getName(), this._stdBeanNaming);
                if (implName == null) {
                    return;
                }
                visible = this._visibilityChecker.isIsGetterVisible(m);
            }
            else {
                visible = this._visibilityChecker.isGetterVisible(m);
            }
        }
        else {
            implName = ai.findImplicitPropertyName(m);
            if (implName == null) {
                implName = BeanUtil.okNameForGetter(m, this._stdBeanNaming);
            }
            if (implName == null) {
                implName = m.getName();
            }
            if (pn.isEmpty()) {
                pn = this._propNameFromSimple(implName);
                nameExplicit = false;
            }
            visible = true;
        }
        final boolean ignore = ai.hasIgnoreMarker(m);
        this._property(props, implName).addGetter(m, pn, nameExplicit, visible, ignore);
    }
    
    protected void _addSetterMethod(final Map<String, POJOPropertyBuilder> props, final AnnotatedMethod m, final AnnotationIntrospector ai) {
        PropertyName pn = (ai == null) ? null : ai.findNameForDeserialization(m);
        boolean nameExplicit = pn != null;
        String implName;
        boolean visible;
        if (!nameExplicit) {
            implName = ((ai == null) ? null : ai.findImplicitPropertyName(m));
            if (implName == null) {
                implName = BeanUtil.okNameForMutator(m, this._mutatorPrefix, this._stdBeanNaming);
            }
            if (implName == null) {
                return;
            }
            visible = this._visibilityChecker.isSetterVisible(m);
        }
        else {
            implName = ((ai == null) ? null : ai.findImplicitPropertyName(m));
            if (implName == null) {
                implName = BeanUtil.okNameForMutator(m, this._mutatorPrefix, this._stdBeanNaming);
            }
            if (implName == null) {
                implName = m.getName();
            }
            if (pn.isEmpty()) {
                pn = this._propNameFromSimple(implName);
                nameExplicit = false;
            }
            visible = true;
        }
        final boolean ignore = ai != null && ai.hasIgnoreMarker(m);
        this._property(props, implName).addSetter(m, pn, nameExplicit, visible, ignore);
    }
    
    protected void _addInjectables(final Map<String, POJOPropertyBuilder> props) {
        final AnnotationIntrospector ai = this._annotationIntrospector;
        for (final AnnotatedField f : this._classDef.fields()) {
            this._doAddInjectable(ai.findInjectableValue(f), f);
        }
        for (final AnnotatedMethod m : this._classDef.memberMethods()) {
            if (m.getParameterCount() != 1) {
                continue;
            }
            this._doAddInjectable(ai.findInjectableValue(m), m);
        }
    }
    
    protected void _doAddInjectable(final JacksonInject.Value injectable, final AnnotatedMember m) {
        if (injectable == null) {
            return;
        }
        final Object id = injectable.getId();
        if (this._injectables == null) {
            this._injectables = new LinkedHashMap<Object, AnnotatedMember>();
        }
        final AnnotatedMember prev = this._injectables.put(id, m);
        if (prev != null && prev.getClass() == m.getClass()) {
            final String type = id.getClass().getName();
            throw new IllegalArgumentException("Duplicate injectable value with id '" + String.valueOf(id) + "' (of type " + type + ")");
        }
    }
    
    private PropertyName _propNameFromSimple(final String simpleName) {
        return PropertyName.construct(simpleName, null);
    }
    
    protected void _removeUnwantedProperties(final Map<String, POJOPropertyBuilder> props) {
        final Iterator<POJOPropertyBuilder> it = props.values().iterator();
        while (it.hasNext()) {
            final POJOPropertyBuilder prop = it.next();
            if (!prop.anyVisible()) {
                it.remove();
            }
            else {
                if (!prop.anyIgnorals()) {
                    continue;
                }
                if (!prop.isExplicitlyIncluded()) {
                    it.remove();
                    this._collectIgnorals(prop.getName());
                }
                else {
                    prop.removeIgnored();
                    if (prop.couldDeserialize()) {
                        continue;
                    }
                    this._collectIgnorals(prop.getName());
                }
            }
        }
    }
    
    protected void _removeUnwantedAccessor(final Map<String, POJOPropertyBuilder> props) {
        final boolean inferMutators = this._config.isEnabled(MapperFeature.INFER_PROPERTY_MUTATORS);
        for (final POJOPropertyBuilder prop : props.values()) {
            final JsonProperty.Access acc = prop.removeNonVisible(inferMutators);
            if (acc == JsonProperty.Access.READ_ONLY) {
                this._collectIgnorals(prop.getName());
            }
        }
    }
    
    private void _collectIgnorals(final String name) {
        if (!this._forSerialization) {
            if (this._ignoredPropertyNames == null) {
                this._ignoredPropertyNames = new HashSet<String>();
            }
            this._ignoredPropertyNames.add(name);
        }
    }
    
    protected void _renameProperties(final Map<String, POJOPropertyBuilder> props) {
        final Iterator<Map.Entry<String, POJOPropertyBuilder>> it = props.entrySet().iterator();
        LinkedList<POJOPropertyBuilder> renamed = null;
        while (it.hasNext()) {
            final Map.Entry<String, POJOPropertyBuilder> entry = it.next();
            final POJOPropertyBuilder prop = entry.getValue();
            final Collection<PropertyName> l = prop.findExplicitNames();
            if (l.isEmpty()) {
                continue;
            }
            it.remove();
            if (renamed == null) {
                renamed = new LinkedList<POJOPropertyBuilder>();
            }
            if (l.size() == 1) {
                final PropertyName n = l.iterator().next();
                renamed.add(prop.withName(n));
            }
            else {
                renamed.addAll(prop.explode(l));
            }
        }
        if (renamed != null) {
            for (final POJOPropertyBuilder prop : renamed) {
                final String name = prop.getName();
                final POJOPropertyBuilder old = props.get(name);
                if (old == null) {
                    props.put(name, prop);
                }
                else {
                    old.addAll(prop);
                }
                this._updateCreatorProperty(prop, this._creatorProperties);
                if (this._ignoredPropertyNames != null) {
                    this._ignoredPropertyNames.remove(name);
                }
            }
        }
    }
    
    protected void _renameUsing(final Map<String, POJOPropertyBuilder> propMap, final PropertyNamingStrategy naming) {
        final POJOPropertyBuilder[] props = propMap.values().toArray(new POJOPropertyBuilder[propMap.size()]);
        propMap.clear();
        for (POJOPropertyBuilder prop : props) {
            final PropertyName fullName = prop.getFullName();
            String rename = null;
            if (!prop.isExplicitlyNamed() || this._config.isEnabled(MapperFeature.ALLOW_EXPLICIT_PROPERTY_RENAMING)) {
                if (this._forSerialization) {
                    if (prop.hasGetter()) {
                        rename = naming.nameForGetterMethod(this._config, prop.getGetter(), fullName.getSimpleName());
                    }
                    else if (prop.hasField()) {
                        rename = naming.nameForField(this._config, prop.getField(), fullName.getSimpleName());
                    }
                }
                else if (prop.hasSetter()) {
                    rename = naming.nameForSetterMethod(this._config, prop.getSetter(), fullName.getSimpleName());
                }
                else if (prop.hasConstructorParameter()) {
                    rename = naming.nameForConstructorParameter(this._config, prop.getConstructorParameter(), fullName.getSimpleName());
                }
                else if (prop.hasField()) {
                    rename = naming.nameForField(this._config, prop.getField(), fullName.getSimpleName());
                }
                else if (prop.hasGetter()) {
                    rename = naming.nameForGetterMethod(this._config, prop.getGetter(), fullName.getSimpleName());
                }
            }
            String simpleName;
            if (rename != null && !fullName.hasSimpleName(rename)) {
                prop = prop.withSimpleName(rename);
                simpleName = rename;
            }
            else {
                simpleName = fullName.getSimpleName();
            }
            final POJOPropertyBuilder old = propMap.get(simpleName);
            if (old == null) {
                propMap.put(simpleName, prop);
            }
            else {
                old.addAll(prop);
            }
            this._updateCreatorProperty(prop, this._creatorProperties);
        }
    }
    
    protected void _renameWithWrappers(final Map<String, POJOPropertyBuilder> props) {
        final Iterator<Map.Entry<String, POJOPropertyBuilder>> it = props.entrySet().iterator();
        LinkedList<POJOPropertyBuilder> renamed = null;
        while (it.hasNext()) {
            final Map.Entry<String, POJOPropertyBuilder> entry = it.next();
            POJOPropertyBuilder prop = entry.getValue();
            final AnnotatedMember member = prop.getPrimaryMember();
            if (member == null) {
                continue;
            }
            final PropertyName wrapperName = this._annotationIntrospector.findWrapperName(member);
            if (wrapperName == null) {
                continue;
            }
            if (!wrapperName.hasSimpleName()) {
                continue;
            }
            if (wrapperName.equals(prop.getFullName())) {
                continue;
            }
            if (renamed == null) {
                renamed = new LinkedList<POJOPropertyBuilder>();
            }
            prop = prop.withName(wrapperName);
            renamed.add(prop);
            it.remove();
        }
        if (renamed != null) {
            for (final POJOPropertyBuilder prop : renamed) {
                final String name = prop.getName();
                final POJOPropertyBuilder old = props.get(name);
                if (old == null) {
                    props.put(name, prop);
                }
                else {
                    old.addAll(prop);
                }
            }
        }
    }
    
    protected void _sortProperties(final Map<String, POJOPropertyBuilder> props) {
        final AnnotationIntrospector intr = this._annotationIntrospector;
        final Boolean alpha = intr.findSerializationSortAlphabetically(this._classDef);
        boolean sort;
        if (alpha == null) {
            sort = this._config.shouldSortPropertiesAlphabetically();
        }
        else {
            sort = alpha;
        }
        final String[] propertyOrder = intr.findSerializationPropertyOrder(this._classDef);
        if (!sort && this._creatorProperties == null && propertyOrder == null) {
            return;
        }
        final int size = props.size();
        Map<String, POJOPropertyBuilder> all;
        if (sort) {
            all = new TreeMap<String, POJOPropertyBuilder>();
        }
        else {
            all = new LinkedHashMap<String, POJOPropertyBuilder>(size + size);
        }
        for (final POJOPropertyBuilder prop : props.values()) {
            all.put(prop.getName(), prop);
        }
        final Map<String, POJOPropertyBuilder> ordered = new LinkedHashMap<String, POJOPropertyBuilder>(size + size);
        if (propertyOrder != null) {
            for (String name : propertyOrder) {
                POJOPropertyBuilder w = all.get(name);
                if (w == null) {
                    for (final POJOPropertyBuilder prop2 : props.values()) {
                        if (name.equals(prop2.getInternalName())) {
                            w = prop2;
                            name = prop2.getName();
                            break;
                        }
                    }
                }
                if (w != null) {
                    ordered.put(name, w);
                }
            }
        }
        if (this._creatorProperties != null) {
            Collection<POJOPropertyBuilder> cr;
            if (sort) {
                final TreeMap<String, POJOPropertyBuilder> sorted = new TreeMap<String, POJOPropertyBuilder>();
                for (final POJOPropertyBuilder prop3 : this._creatorProperties) {
                    sorted.put(prop3.getName(), prop3);
                }
                cr = sorted.values();
            }
            else {
                cr = this._creatorProperties;
            }
            for (final POJOPropertyBuilder prop4 : cr) {
                final String name = prop4.getName();
                if (all.containsKey(name)) {
                    ordered.put(name, prop4);
                }
            }
        }
        ordered.putAll(all);
        props.clear();
        props.putAll(ordered);
    }
    
    protected void reportProblem(String msg, final Object... args) {
        if (args.length > 0) {
            msg = String.format(msg, args);
        }
        throw new IllegalArgumentException("Problem with definition of " + this._classDef + ": " + msg);
    }
    
    protected POJOPropertyBuilder _property(final Map<String, POJOPropertyBuilder> props, final PropertyName name) {
        final String simpleName = name.getSimpleName();
        POJOPropertyBuilder prop = props.get(simpleName);
        if (prop == null) {
            prop = new POJOPropertyBuilder(this._config, this._annotationIntrospector, this._forSerialization, name);
            props.put(simpleName, prop);
        }
        return prop;
    }
    
    protected POJOPropertyBuilder _property(final Map<String, POJOPropertyBuilder> props, final String implName) {
        POJOPropertyBuilder prop = props.get(implName);
        if (prop == null) {
            prop = new POJOPropertyBuilder(this._config, this._annotationIntrospector, this._forSerialization, PropertyName.construct(implName));
            props.put(implName, prop);
        }
        return prop;
    }
    
    private PropertyNamingStrategy _findNamingStrategy() {
        final Object namingDef = this._annotationIntrospector.findNamingStrategy(this._classDef);
        if (namingDef == null) {
            return this._config.getPropertyNamingStrategy();
        }
        if (namingDef instanceof PropertyNamingStrategy) {
            return (PropertyNamingStrategy)namingDef;
        }
        if (!(namingDef instanceof Class)) {
            throw new IllegalStateException("AnnotationIntrospector returned PropertyNamingStrategy definition of type " + namingDef.getClass().getName() + "; expected type PropertyNamingStrategy or Class<PropertyNamingStrategy> instead");
        }
        final Class<?> namingClass = (Class<?>)namingDef;
        if (namingClass == PropertyNamingStrategy.class) {
            return null;
        }
        if (!PropertyNamingStrategy.class.isAssignableFrom(namingClass)) {
            throw new IllegalStateException("AnnotationIntrospector returned Class " + namingClass.getName() + "; expected Class<PropertyNamingStrategy>");
        }
        final HandlerInstantiator hi = this._config.getHandlerInstantiator();
        if (hi != null) {
            final PropertyNamingStrategy pns = hi.namingStrategyInstance(this._config, this._classDef, namingClass);
            if (pns != null) {
                return pns;
            }
        }
        return ClassUtil.createInstance(namingClass, this._config.canOverrideAccessModifiers());
    }
    
    protected void _updateCreatorProperty(final POJOPropertyBuilder prop, final List<POJOPropertyBuilder> creatorProperties) {
        if (creatorProperties != null) {
            final String intName = prop.getInternalName();
            for (int i = 0, len = creatorProperties.size(); i < len; ++i) {
                if (creatorProperties.get(i).getInternalName().equals(intName)) {
                    creatorProperties.set(i, prop);
                    break;
                }
            }
        }
    }
}
