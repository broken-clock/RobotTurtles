// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.converter.json;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import java.util.Iterator;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.SerializationConfig;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import org.codehaus.jackson.map.AnnotationIntrospector;
import java.text.DateFormat;
import java.util.Map;
import org.springframework.beans.factory.InitializingBean;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.FactoryBean;

@Deprecated
public class JacksonObjectMapperFactoryBean implements FactoryBean<ObjectMapper>, InitializingBean
{
    private ObjectMapper objectMapper;
    private Map<Object, Boolean> features;
    private DateFormat dateFormat;
    private AnnotationIntrospector annotationIntrospector;
    
    public JacksonObjectMapperFactoryBean() {
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
    
    public void setAutoDetectFields(final boolean autoDetectFields) {
        this.features.put(SerializationConfig.Feature.AUTO_DETECT_FIELDS, autoDetectFields);
        this.features.put(DeserializationConfig.Feature.AUTO_DETECT_FIELDS, autoDetectFields);
    }
    
    public void setAutoDetectGettersSetters(final boolean autoDetectGettersSetters) {
        this.features.put(SerializationConfig.Feature.AUTO_DETECT_GETTERS, autoDetectGettersSetters);
        this.features.put(DeserializationConfig.Feature.AUTO_DETECT_SETTERS, autoDetectGettersSetters);
    }
    
    public void setFailOnEmptyBeans(final boolean failOnEmptyBeans) {
        this.features.put(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, failOnEmptyBeans);
    }
    
    public void setIndentOutput(final boolean indentOutput) {
        this.features.put(SerializationConfig.Feature.INDENT_OUTPUT, indentOutput);
    }
    
    public void setFeaturesToEnable(final Object[] featuresToEnable) {
        if (featuresToEnable != null) {
            for (final Object feature : featuresToEnable) {
                this.features.put(feature, Boolean.TRUE);
            }
        }
    }
    
    public void setFeaturesToDisable(final Object[] featuresToDisable) {
        if (featuresToDisable != null) {
            for (final Object feature : featuresToDisable) {
                this.features.put(feature, Boolean.FALSE);
            }
        }
    }
    
    @Override
    public void afterPropertiesSet() {
        if (this.objectMapper == null) {
            this.objectMapper = new ObjectMapper();
        }
        if (this.annotationIntrospector != null) {
            this.objectMapper.setSerializationConfig(this.objectMapper.getSerializationConfig().withAnnotationIntrospector(this.annotationIntrospector));
            this.objectMapper.setDeserializationConfig(this.objectMapper.getDeserializationConfig().withAnnotationIntrospector(this.annotationIntrospector));
        }
        if (this.dateFormat != null) {
            this.objectMapper.setDateFormat(this.dateFormat);
        }
        for (final Map.Entry<Object, Boolean> entry : this.features.entrySet()) {
            this.configureFeature(entry.getKey(), entry.getValue());
        }
    }
    
    private void configureFeature(final Object feature, final boolean enabled) {
        if (feature instanceof JsonParser.Feature) {
            this.objectMapper.configure((JsonParser.Feature)feature, enabled);
        }
        else if (feature instanceof JsonGenerator.Feature) {
            this.objectMapper.configure((JsonGenerator.Feature)feature, enabled);
        }
        else if (feature instanceof SerializationConfig.Feature) {
            this.objectMapper.configure((SerializationConfig.Feature)feature, enabled);
        }
        else {
            if (!(feature instanceof DeserializationConfig.Feature)) {
                throw new IllegalArgumentException("Unknown feature class: " + feature.getClass().getName());
            }
            this.objectMapper.configure((DeserializationConfig.Feature)feature, enabled);
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
