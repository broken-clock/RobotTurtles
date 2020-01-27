// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ext;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializationConfig;
import java.io.Serializable;

public class OptionalHandlerFactory implements Serializable
{
    private static final long serialVersionUID = 1L;
    private static final String PACKAGE_PREFIX_JAVAX_XML = "javax.xml.";
    private static final String SERIALIZERS_FOR_JAVAX_XML = "com.fasterxml.jackson.databind.ext.CoreXMLSerializers";
    private static final String DESERIALIZERS_FOR_JAVAX_XML = "com.fasterxml.jackson.databind.ext.CoreXMLDeserializers";
    private static final String SERIALIZER_FOR_DOM_NODE = "com.fasterxml.jackson.databind.ext.DOMSerializer";
    private static final String DESERIALIZER_FOR_DOM_DOCUMENT = "com.fasterxml.jackson.databind.ext.DOMDeserializer$DocumentDeserializer";
    private static final String DESERIALIZER_FOR_DOM_NODE = "com.fasterxml.jackson.databind.ext.DOMDeserializer$NodeDeserializer";
    private static final Class<?> CLASS_DOM_NODE;
    private static final Class<?> CLASS_DOM_DOCUMENT;
    private static final Java7Support _jdk7Helper;
    public static final OptionalHandlerFactory instance;
    
    protected OptionalHandlerFactory() {
    }
    
    public JsonSerializer<?> findSerializer(final SerializationConfig config, final JavaType type, final BeanDescription beanDesc) {
        final Class<?> rawType = type.getRawClass();
        if (OptionalHandlerFactory._jdk7Helper != null) {
            final JsonSerializer<?> ser = OptionalHandlerFactory._jdk7Helper.getSerializerForJavaNioFilePath(rawType);
            if (ser != null) {
                return ser;
            }
        }
        if (OptionalHandlerFactory.CLASS_DOM_NODE != null && OptionalHandlerFactory.CLASS_DOM_NODE.isAssignableFrom(rawType)) {
            return (JsonSerializer<?>)this.instantiate("com.fasterxml.jackson.databind.ext.DOMSerializer");
        }
        final String className = rawType.getName();
        if (!className.startsWith("javax.xml.") && !this.hasSuperClassStartingWith(rawType, "javax.xml.")) {
            return null;
        }
        final String factoryName = "com.fasterxml.jackson.databind.ext.CoreXMLSerializers";
        final Object ob = this.instantiate(factoryName);
        if (ob == null) {
            return null;
        }
        return ((Serializers)ob).findSerializer(config, type, beanDesc);
    }
    
    public JsonDeserializer<?> findDeserializer(final JavaType type, final DeserializationConfig config, final BeanDescription beanDesc) throws JsonMappingException {
        final Class<?> rawType = type.getRawClass();
        if (OptionalHandlerFactory._jdk7Helper != null) {
            final JsonDeserializer<?> deser = OptionalHandlerFactory._jdk7Helper.getDeserializerForJavaNioFilePath(rawType);
            if (deser != null) {
                return deser;
            }
        }
        if (OptionalHandlerFactory.CLASS_DOM_NODE != null && OptionalHandlerFactory.CLASS_DOM_NODE.isAssignableFrom(rawType)) {
            return (JsonDeserializer<?>)this.instantiate("com.fasterxml.jackson.databind.ext.DOMDeserializer$NodeDeserializer");
        }
        if (OptionalHandlerFactory.CLASS_DOM_DOCUMENT != null && OptionalHandlerFactory.CLASS_DOM_DOCUMENT.isAssignableFrom(rawType)) {
            return (JsonDeserializer<?>)this.instantiate("com.fasterxml.jackson.databind.ext.DOMDeserializer$DocumentDeserializer");
        }
        final String className = rawType.getName();
        if (!className.startsWith("javax.xml.") && !this.hasSuperClassStartingWith(rawType, "javax.xml.")) {
            return null;
        }
        final String factoryName = "com.fasterxml.jackson.databind.ext.CoreXMLDeserializers";
        final Object ob = this.instantiate(factoryName);
        if (ob == null) {
            return null;
        }
        return ((Deserializers)ob).findBeanDeserializer(type, config, beanDesc);
    }
    
    private Object instantiate(final String className) {
        try {
            return ClassUtil.createInstance(Class.forName(className), false);
        }
        catch (LinkageError linkageError) {}
        catch (Exception ex) {}
        return null;
    }
    
    private boolean hasSuperClassStartingWith(final Class<?> rawType, final String prefix) {
        for (Class<?> supertype = rawType.getSuperclass(); supertype != null; supertype = supertype.getSuperclass()) {
            if (supertype == Object.class) {
                return false;
            }
            if (supertype.getName().startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
    
    static {
        Class<?> doc = null;
        Class<?> node = null;
        try {
            node = Node.class;
            doc = Document.class;
        }
        catch (Exception e) {
            Logger.getLogger(OptionalHandlerFactory.class.getName()).log(Level.INFO, "Could not load DOM `Node` and/or `Document` classes: no DOM support");
        }
        CLASS_DOM_NODE = node;
        CLASS_DOM_DOCUMENT = doc;
        Java7Support x = null;
        try {
            x = Java7Support.instance();
        }
        catch (Throwable t) {}
        _jdk7Helper = x;
        instance = new OptionalHandlerFactory();
    }
}
