// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.xml;

import org.apache.commons.logging.LogFactory;
import java.util.Properties;
import org.springframework.util.CollectionUtils;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import java.io.IOException;
import org.springframework.core.io.Resource;
import java.io.FileNotFoundException;
import org.springframework.core.io.ClassPathResource;
import org.xml.sax.InputSource;
import org.springframework.util.Assert;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.xml.sax.EntityResolver;

public class PluggableSchemaResolver implements EntityResolver
{
    public static final String DEFAULT_SCHEMA_MAPPINGS_LOCATION = "META-INF/spring.schemas";
    private static final Log logger;
    private final ClassLoader classLoader;
    private final String schemaMappingsLocation;
    private volatile Map<String, String> schemaMappings;
    
    public PluggableSchemaResolver(final ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.schemaMappingsLocation = "META-INF/spring.schemas";
    }
    
    public PluggableSchemaResolver(final ClassLoader classLoader, final String schemaMappingsLocation) {
        Assert.hasText(schemaMappingsLocation, "'schemaMappingsLocation' must not be empty");
        this.classLoader = classLoader;
        this.schemaMappingsLocation = schemaMappingsLocation;
    }
    
    @Override
    public InputSource resolveEntity(final String publicId, final String systemId) throws IOException {
        if (PluggableSchemaResolver.logger.isTraceEnabled()) {
            PluggableSchemaResolver.logger.trace("Trying to resolve XML entity with public id [" + publicId + "] and system id [" + systemId + "]");
        }
        if (systemId != null) {
            final String resourceLocation = this.getSchemaMappings().get(systemId);
            if (resourceLocation != null) {
                final Resource resource = new ClassPathResource(resourceLocation, this.classLoader);
                try {
                    final InputSource source = new InputSource(resource.getInputStream());
                    source.setPublicId(publicId);
                    source.setSystemId(systemId);
                    if (PluggableSchemaResolver.logger.isDebugEnabled()) {
                        PluggableSchemaResolver.logger.debug("Found XML schema [" + systemId + "] in classpath: " + resourceLocation);
                    }
                    return source;
                }
                catch (FileNotFoundException ex) {
                    if (PluggableSchemaResolver.logger.isDebugEnabled()) {
                        PluggableSchemaResolver.logger.debug("Couldn't find XML schema [" + systemId + "]: " + resource, ex);
                    }
                }
            }
        }
        return null;
    }
    
    private Map<String, String> getSchemaMappings() {
        if (this.schemaMappings == null) {
            synchronized (this) {
                if (this.schemaMappings == null) {
                    if (PluggableSchemaResolver.logger.isDebugEnabled()) {
                        PluggableSchemaResolver.logger.debug("Loading schema mappings from [" + this.schemaMappingsLocation + "]");
                    }
                    try {
                        final Properties mappings = PropertiesLoaderUtils.loadAllProperties(this.schemaMappingsLocation, this.classLoader);
                        if (PluggableSchemaResolver.logger.isDebugEnabled()) {
                            PluggableSchemaResolver.logger.debug("Loaded schema mappings: " + mappings);
                        }
                        final Map<String, String> schemaMappings = new ConcurrentHashMap<String, String>(mappings.size());
                        CollectionUtils.mergePropertiesIntoMap(mappings, schemaMappings);
                        this.schemaMappings = schemaMappings;
                    }
                    catch (IOException ex) {
                        throw new IllegalStateException("Unable to load schema mappings from location [" + this.schemaMappingsLocation + "]", ex);
                    }
                }
            }
        }
        return this.schemaMappings;
    }
    
    @Override
    public String toString() {
        return "EntityResolver using mappings " + this.getSchemaMappings();
    }
    
    static {
        logger = LogFactory.getLog(PluggableSchemaResolver.class);
    }
}
