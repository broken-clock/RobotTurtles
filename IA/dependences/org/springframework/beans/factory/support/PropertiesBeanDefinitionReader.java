// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.util.StringUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import java.util.Iterator;
import org.springframework.beans.BeansException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.io.InputStream;
import java.io.IOException;
import java.util.Map;
import java.io.Reader;
import java.io.InputStreamReader;
import java.util.Properties;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.Resource;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

public class PropertiesBeanDefinitionReader extends AbstractBeanDefinitionReader
{
    public static final String TRUE_VALUE = "true";
    public static final String SEPARATOR = ".";
    public static final String CLASS_KEY = "(class)";
    public static final String PARENT_KEY = "(parent)";
    public static final String SCOPE_KEY = "(scope)";
    public static final String SINGLETON_KEY = "(singleton)";
    public static final String ABSTRACT_KEY = "(abstract)";
    public static final String LAZY_INIT_KEY = "(lazy-init)";
    public static final String REF_SUFFIX = "(ref)";
    public static final String REF_PREFIX = "*";
    public static final String CONSTRUCTOR_ARG_PREFIX = "$";
    private String defaultParentBean;
    private PropertiesPersister propertiesPersister;
    
    public PropertiesBeanDefinitionReader(final BeanDefinitionRegistry registry) {
        super(registry);
        this.propertiesPersister = new DefaultPropertiesPersister();
    }
    
    public void setDefaultParentBean(final String defaultParentBean) {
        this.defaultParentBean = defaultParentBean;
    }
    
    public String getDefaultParentBean() {
        return this.defaultParentBean;
    }
    
    public void setPropertiesPersister(final PropertiesPersister propertiesPersister) {
        this.propertiesPersister = ((propertiesPersister != null) ? propertiesPersister : new DefaultPropertiesPersister());
    }
    
    public PropertiesPersister getPropertiesPersister() {
        return this.propertiesPersister;
    }
    
    @Override
    public int loadBeanDefinitions(final Resource resource) throws BeanDefinitionStoreException {
        return this.loadBeanDefinitions(new EncodedResource(resource), null);
    }
    
    public int loadBeanDefinitions(final Resource resource, final String prefix) throws BeanDefinitionStoreException {
        return this.loadBeanDefinitions(new EncodedResource(resource), prefix);
    }
    
    public int loadBeanDefinitions(final EncodedResource encodedResource) throws BeanDefinitionStoreException {
        return this.loadBeanDefinitions(encodedResource, null);
    }
    
    public int loadBeanDefinitions(final EncodedResource encodedResource, final String prefix) throws BeanDefinitionStoreException {
        final Properties props = new Properties();
        try {
            final InputStream is = encodedResource.getResource().getInputStream();
            try {
                if (encodedResource.getEncoding() != null) {
                    this.getPropertiesPersister().load(props, new InputStreamReader(is, encodedResource.getEncoding()));
                }
                else {
                    this.getPropertiesPersister().load(props, is);
                }
            }
            finally {
                is.close();
            }
            return this.registerBeanDefinitions(props, prefix, encodedResource.getResource().getDescription());
        }
        catch (IOException ex) {
            throw new BeanDefinitionStoreException("Could not parse properties from " + encodedResource.getResource(), ex);
        }
    }
    
    public int registerBeanDefinitions(final ResourceBundle rb) throws BeanDefinitionStoreException {
        return this.registerBeanDefinitions(rb, null);
    }
    
    public int registerBeanDefinitions(final ResourceBundle rb, final String prefix) throws BeanDefinitionStoreException {
        final Map<String, Object> map = new HashMap<String, Object>();
        final Enumeration<String> keys = rb.getKeys();
        while (keys.hasMoreElements()) {
            final String key = keys.nextElement();
            map.put(key, rb.getObject(key));
        }
        return this.registerBeanDefinitions(map, prefix);
    }
    
    public int registerBeanDefinitions(final Map<?, ?> map) throws BeansException {
        return this.registerBeanDefinitions(map, null);
    }
    
    public int registerBeanDefinitions(final Map<?, ?> map, final String prefix) throws BeansException {
        return this.registerBeanDefinitions(map, prefix, "Map " + map);
    }
    
    public int registerBeanDefinitions(final Map<?, ?> map, String prefix, final String resourceDescription) throws BeansException {
        if (prefix == null) {
            prefix = "";
        }
        int beanCount = 0;
        for (final Object key : map.keySet()) {
            if (!(key instanceof String)) {
                throw new IllegalArgumentException("Illegal key [" + key + "]: only Strings allowed");
            }
            final String keyString = (String)key;
            if (!keyString.startsWith(prefix)) {
                continue;
            }
            final String nameAndProperty = keyString.substring(prefix.length());
            int sepIdx = -1;
            final int propKeyIdx = nameAndProperty.indexOf("[");
            if (propKeyIdx != -1) {
                sepIdx = nameAndProperty.lastIndexOf(".", propKeyIdx);
            }
            else {
                sepIdx = nameAndProperty.lastIndexOf(".");
            }
            if (sepIdx != -1) {
                final String beanName = nameAndProperty.substring(0, sepIdx);
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Found bean name '" + beanName + "'");
                }
                if (this.getRegistry().containsBeanDefinition(beanName)) {
                    continue;
                }
                this.registerBeanDefinition(beanName, map, prefix + beanName, resourceDescription);
                ++beanCount;
            }
            else {
                if (!this.logger.isDebugEnabled()) {
                    continue;
                }
                this.logger.debug("Invalid bean name and property [" + nameAndProperty + "]");
            }
        }
        return beanCount;
    }
    
    protected void registerBeanDefinition(final String beanName, final Map<?, ?> map, final String prefix, final String resourceDescription) throws BeansException {
        String className = null;
        String parent = null;
        String scope = "singleton";
        boolean isAbstract = false;
        boolean lazyInit = false;
        final ConstructorArgumentValues cas = new ConstructorArgumentValues();
        final MutablePropertyValues pvs = new MutablePropertyValues();
        for (final Map.Entry<?, ?> entry : map.entrySet()) {
            final String key = StringUtils.trimWhitespace((String)entry.getKey());
            if (key.startsWith(prefix + ".")) {
                String property = key.substring(prefix.length() + ".".length());
                if ("(class)".equals(property)) {
                    className = StringUtils.trimWhitespace((String)entry.getValue());
                }
                else if ("(parent)".equals(property)) {
                    parent = StringUtils.trimWhitespace((String)entry.getValue());
                }
                else if ("(abstract)".equals(property)) {
                    final String val = StringUtils.trimWhitespace((String)entry.getValue());
                    isAbstract = "true".equals(val);
                }
                else if ("(scope)".equals(property)) {
                    scope = StringUtils.trimWhitespace((String)entry.getValue());
                }
                else if ("(singleton)".equals(property)) {
                    final String val = StringUtils.trimWhitespace((String)entry.getValue());
                    scope = ((val == null || "true".equals(val)) ? "singleton" : "prototype");
                }
                else if ("(lazy-init)".equals(property)) {
                    final String val = StringUtils.trimWhitespace((String)entry.getValue());
                    lazyInit = "true".equals(val);
                }
                else if (property.startsWith("$")) {
                    if (property.endsWith("(ref)")) {
                        final int index = Integer.parseInt(property.substring(1, property.length() - "(ref)".length()));
                        cas.addIndexedArgumentValue(index, new RuntimeBeanReference(entry.getValue().toString()));
                    }
                    else {
                        final int index = Integer.parseInt(property.substring(1));
                        cas.addIndexedArgumentValue(index, this.readValue(entry));
                    }
                }
                else if (property.endsWith("(ref)")) {
                    property = property.substring(0, property.length() - "(ref)".length());
                    final String ref = StringUtils.trimWhitespace((String)entry.getValue());
                    final Object val2 = new RuntimeBeanReference(ref);
                    pvs.add(property, val2);
                }
                else {
                    pvs.add(property, this.readValue(entry));
                }
            }
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Registering bean definition for bean name '" + beanName + "' with " + pvs);
        }
        if (parent == null && className == null && !beanName.equals(this.defaultParentBean)) {
            parent = this.defaultParentBean;
        }
        try {
            final AbstractBeanDefinition bd = BeanDefinitionReaderUtils.createBeanDefinition(parent, className, this.getBeanClassLoader());
            bd.setScope(scope);
            bd.setAbstract(isAbstract);
            bd.setLazyInit(lazyInit);
            bd.setConstructorArgumentValues(cas);
            bd.setPropertyValues(pvs);
            this.getRegistry().registerBeanDefinition(beanName, bd);
        }
        catch (ClassNotFoundException ex) {
            throw new CannotLoadBeanClassException(resourceDescription, beanName, className, ex);
        }
        catch (LinkageError err) {
            throw new CannotLoadBeanClassException(resourceDescription, beanName, className, err);
        }
    }
    
    private Object readValue(final Map.Entry<?, ?> entry) {
        Object val = entry.getValue();
        if (val instanceof String) {
            final String strVal = (String)val;
            if (strVal.startsWith("*")) {
                final String targetName = strVal.substring(1);
                if (targetName.startsWith("*")) {
                    val = targetName;
                }
                else {
                    val = new RuntimeBeanReference(targetName);
                }
            }
        }
        return val;
    }
}
