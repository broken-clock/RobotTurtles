// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.converter.json;

import org.springframework.util.ClassUtils;
import org.springframework.beans.FatalBeanException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import java.util.Iterator;
import org.springframework.beans.BeanUtils;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.Collection;
import java.util.LinkedList;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import org.springframework.util.Assert;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.Module;
import java.util.List;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import java.util.Map;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.text.DateFormat;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.BeanClassLoaderAware;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.FactoryBean;

public class Jackson2ObjectMapperFactoryBean implements FactoryBean<ObjectMapper>, BeanClassLoaderAware, InitializingBean
{
    private ObjectMapper objectMapper;
    private DateFormat dateFormat;
    private JsonInclude.Include serializationInclusion;
    private AnnotationIntrospector annotationIntrospector;
    private final Map<Class<?>, JsonSerializer<?>> serializers;
    private final Map<Class<?>, JsonDeserializer<?>> deserializers;
    private final Map<Object, Boolean> features;
    private List<Module> modules;
    private Class<? extends Module>[] modulesToInstall;
    private boolean findModulesViaServiceLoader;
    private PropertyNamingStrategy propertyNamingStrategy;
    private ClassLoader beanClassLoader;
    
    public Jackson2ObjectMapperFactoryBean() {
        this.serializers = new LinkedHashMap<Class<?>, JsonSerializer<?>>();
        this.deserializers = new LinkedHashMap<Class<?>, JsonDeserializer<?>>();
        this.features = new HashMap<Object, Boolean>();
    }
    
    public void setObjectMapper(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    public void setDateFormat(final DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }
    
    public void setSimpleDateFormat(final String format) {
        this.dateFormat = new SimpleDateFormat(format);
    }
    
    public void setAnnotationIntrospector(final AnnotationIntrospector annotationIntrospector) {
        this.annotationIntrospector = annotationIntrospector;
    }
    
    public void setSerializationInclusion(final JsonInclude.Include serializationInclusion) {
        this.serializationInclusion = serializationInclusion;
    }
    
    public void setSerializers(final JsonSerializer<?>... serializers) {
        if (serializers != null) {
            for (final JsonSerializer<?> serializer : serializers) {
                final Class<?> handledType = serializer.handledType();
                Assert.isTrue(handledType != null && handledType != Object.class, "Unknown handled type in " + serializer.getClass().getName());
                this.serializers.put(serializer.handledType(), serializer);
            }
        }
    }
    
    public void setSerializersByType(final Map<Class<?>, JsonSerializer<?>> serializers) {
        if (serializers != null) {
            this.serializers.putAll(serializers);
        }
    }
    
    public void setDeserializersByType(final Map<Class<?>, JsonDeserializer<?>> deserializers) {
        if (deserializers != null) {
            this.deserializers.putAll(deserializers);
        }
    }
    
    public void setAutoDetectFields(final boolean autoDetectFields) {
        this.features.put(MapperFeature.AUTO_DETECT_FIELDS, autoDetectFields);
    }
    
    public void setAutoDetectGettersSetters(final boolean autoDetectGettersSetters) {
        this.features.put(MapperFeature.AUTO_DETECT_GETTERS, autoDetectGettersSetters);
        this.features.put(MapperFeature.AUTO_DETECT_SETTERS, autoDetectGettersSetters);
    }
    
    public void setFailOnEmptyBeans(final boolean failOnEmptyBeans) {
        this.features.put(SerializationFeature.FAIL_ON_EMPTY_BEANS, failOnEmptyBeans);
    }
    
    public void setIndentOutput(final boolean indentOutput) {
        this.features.put(SerializationFeature.INDENT_OUTPUT, indentOutput);
    }
    
    public void setFeaturesToEnable(final Object... featuresToEnable) {
        if (featuresToEnable != null) {
            for (final Object feature : featuresToEnable) {
                this.features.put(feature, Boolean.TRUE);
            }
        }
    }
    
    public void setFeaturesToDisable(final Object... featuresToDisable) {
        if (featuresToDisable != null) {
            for (final Object feature : featuresToDisable) {
                this.features.put(feature, Boolean.FALSE);
            }
        }
    }
    
    public void setModules(final List<Module> modules) {
        this.modules = new LinkedList<Module>(modules);
    }
    
    public void setModulesToInstall(final Class<? extends Module>... modules) {
        this.modulesToInstall = modules;
    }
    
    public void setFindModulesViaServiceLoader(final boolean findModules) {
        this.findModulesViaServiceLoader = findModules;
    }
    
    public void setPropertyNamingStrategy(final PropertyNamingStrategy propertyNamingStrategy) {
        this.propertyNamingStrategy = propertyNamingStrategy;
    }
    
    @Override
    public void setBeanClassLoader(final ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }
    
    @Override
    public void afterPropertiesSet() {
        if (this.objectMapper == null) {
            this.objectMapper = new ObjectMapper();
        }
        if (this.dateFormat != null) {
            this.objectMapper.setDateFormat(this.dateFormat);
        }
        if (this.annotationIntrospector != null) {
            this.objectMapper.setAnnotationIntrospector(this.annotationIntrospector);
        }
        if (this.serializationInclusion != null) {
            this.objectMapper.setSerializationInclusion(this.serializationInclusion);
        }
        if (!this.serializers.isEmpty() || !this.deserializers.isEmpty()) {
            final SimpleModule module = new SimpleModule();
            this.addSerializers(module);
            this.addDeserializers(module);
            this.objectMapper.registerModule(module);
        }
        for (final Object feature : this.features.keySet()) {
            this.configureFeature(feature, this.features.get(feature));
        }
        if (this.modules != null) {
            for (final Module module2 : this.modules) {
                this.objectMapper.registerModule(module2);
            }
        }
        else {
            if (this.modulesToInstall != null) {
                for (final Class<? extends Module> module3 : this.modulesToInstall) {
                    this.objectMapper.registerModule(BeanUtils.instantiate(module3));
                }
            }
            if (this.findModulesViaServiceLoader) {
                this.objectMapper.registerModules(ObjectMapper.findModules(this.beanClassLoader));
            }
            else {
                this.registerWellKnownModulesIfAvailable();
            }
        }
        if (this.propertyNamingStrategy != null) {
            this.objectMapper.setPropertyNamingStrategy(this.propertyNamingStrategy);
        }
    }
    
    private <T> void addSerializers(final SimpleModule module) {
        for (final Class<?> type : this.serializers.keySet()) {
            module.addSerializer(type, this.serializers.get(type));
        }
    }
    
    private <T> void addDeserializers(final SimpleModule module) {
        for (final Class<?> type : this.deserializers.keySet()) {
            module.addDeserializer(type, this.deserializers.get(type));
        }
    }
    
    private void configureFeature(final Object feature, final boolean enabled) {
        if (feature instanceof JsonParser.Feature) {
            this.objectMapper.configure((JsonParser.Feature)feature, enabled);
        }
        else if (feature instanceof JsonGenerator.Feature) {
            this.objectMapper.configure((JsonGenerator.Feature)feature, enabled);
        }
        else if (feature instanceof SerializationFeature) {
            this.objectMapper.configure((SerializationFeature)feature, enabled);
        }
        else if (feature instanceof DeserializationFeature) {
            this.objectMapper.configure((DeserializationFeature)feature, enabled);
        }
        else {
            if (!(feature instanceof MapperFeature)) {
                throw new FatalBeanException("Unknown feature class: " + feature.getClass().getName());
            }
            this.objectMapper.configure((MapperFeature)feature, enabled);
        }
    }
    
    private void registerWellKnownModulesIfAvailable() {
        ClassLoader cl = this.beanClassLoader;
        if (cl == null) {
            cl = this.getClass().getClassLoader();
        }
        if (ClassUtils.isPresent("java.time.LocalDate", cl)) {
            try {
                final Class<? extends Module> jsr310Module = (Class<? extends Module>)cl.loadClass("com.fasterxml.jackson.datatype.jsr310.JSR310Module");
                this.objectMapper.registerModule(BeanUtils.instantiate(jsr310Module));
            }
            catch (ClassNotFoundException ex) {}
        }
        if (ClassUtils.isPresent("org.joda.time.LocalDate", cl)) {
            try {
                final Class<? extends Module> jodaModule = (Class<? extends Module>)cl.loadClass("com.fasterxml.jackson.datatype.joda.JodaModule");
                this.objectMapper.registerModule(BeanUtils.instantiate(jodaModule));
            }
            catch (ClassNotFoundException ex2) {}
        }
    }
    
    @Override
    public ObjectMapper getObject() {
        return this.objectMapper;
    }
    
    @Override
    public Class<?> getObjectType() {
        return ObjectMapper.class;
    }
    
    @Override
    public boolean isSingleton() {
        return true;
    }
}
