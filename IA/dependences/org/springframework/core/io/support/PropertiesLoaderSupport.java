// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.io.support;

import java.io.IOException;
import java.util.Map;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DefaultPropertiesPersister;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.PropertiesPersister;
import org.springframework.core.io.Resource;
import java.util.Properties;
import org.apache.commons.logging.Log;

public abstract class PropertiesLoaderSupport
{
    protected final Log logger;
    protected Properties[] localProperties;
    protected boolean localOverride;
    private Resource[] locations;
    private boolean ignoreResourceNotFound;
    private String fileEncoding;
    private PropertiesPersister propertiesPersister;
    
    public PropertiesLoaderSupport() {
        this.logger = LogFactory.getLog(this.getClass());
        this.localOverride = false;
        this.ignoreResourceNotFound = false;
        this.propertiesPersister = new DefaultPropertiesPersister();
    }
    
    public void setProperties(final Properties properties) {
        this.localProperties = new Properties[] { properties };
    }
    
    public void setPropertiesArray(final Properties[] propertiesArray) {
        this.localProperties = propertiesArray;
    }
    
    public void setLocation(final Resource location) {
        this.locations = new Resource[] { location };
    }
    
    public void setLocations(final Resource[] locations) {
        this.locations = locations;
    }
    
    public void setLocalOverride(final boolean localOverride) {
        this.localOverride = localOverride;
    }
    
    public void setIgnoreResourceNotFound(final boolean ignoreResourceNotFound) {
        this.ignoreResourceNotFound = ignoreResourceNotFound;
    }
    
    public void setFileEncoding(final String encoding) {
        this.fileEncoding = encoding;
    }
    
    public void setPropertiesPersister(final PropertiesPersister propertiesPersister) {
        this.propertiesPersister = ((propertiesPersister != null) ? propertiesPersister : new DefaultPropertiesPersister());
    }
    
    protected Properties mergeProperties() throws IOException {
        final Properties result = new Properties();
        if (this.localOverride) {
            this.loadProperties(result);
        }
        if (this.localProperties != null) {
            for (final Properties localProp : this.localProperties) {
                CollectionUtils.mergePropertiesIntoMap(localProp, (Map<Object, Object>)result);
            }
        }
        if (!this.localOverride) {
            this.loadProperties(result);
        }
        return result;
    }
    
    protected void loadProperties(final Properties props) throws IOException {
        if (this.locations != null) {
            for (final Resource location : this.locations) {
                if (this.logger.isInfoEnabled()) {
                    this.logger.info("Loading properties file from " + location);
                }
                try {
                    PropertiesLoaderUtils.fillProperties(props, new EncodedResource(location, this.fileEncoding), this.propertiesPersister);
                }
                catch (IOException ex) {
                    if (!this.ignoreResourceNotFound) {
                        throw ex;
                    }
                    if (this.logger.isWarnEnabled()) {
                        this.logger.warn("Could not load properties from " + location + ": " + ex.getMessage());
                    }
                }
            }
        }
    }
}
