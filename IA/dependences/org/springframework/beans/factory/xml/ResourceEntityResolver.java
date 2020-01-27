// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.xml;

import org.apache.commons.logging.LogFactory;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.springframework.core.io.Resource;
import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import org.xml.sax.InputSource;
import org.springframework.core.io.ResourceLoader;
import org.apache.commons.logging.Log;

public class ResourceEntityResolver extends DelegatingEntityResolver
{
    private static final Log logger;
    private final ResourceLoader resourceLoader;
    
    public ResourceEntityResolver(final ResourceLoader resourceLoader) {
        super(resourceLoader.getClassLoader());
        this.resourceLoader = resourceLoader;
    }
    
    @Override
    public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException, IOException {
        InputSource source = super.resolveEntity(publicId, systemId);
        if (source == null && systemId != null) {
            String resourcePath = null;
            try {
                final String decodedSystemId = URLDecoder.decode(systemId, "UTF-8");
                final String givenUrl = new URL(decodedSystemId).toString();
                final String systemRootUrl = new File("").toURI().toURL().toString();
                if (givenUrl.startsWith(systemRootUrl)) {
                    resourcePath = givenUrl.substring(systemRootUrl.length());
                }
            }
            catch (Exception ex) {
                if (ResourceEntityResolver.logger.isDebugEnabled()) {
                    ResourceEntityResolver.logger.debug("Could not resolve XML entity [" + systemId + "] against system root URL", ex);
                }
                resourcePath = systemId;
            }
            if (resourcePath != null) {
                if (ResourceEntityResolver.logger.isTraceEnabled()) {
                    ResourceEntityResolver.logger.trace("Trying to locate XML entity [" + systemId + "] as resource [" + resourcePath + "]");
                }
                final Resource resource = this.resourceLoader.getResource(resourcePath);
                source = new InputSource(resource.getInputStream());
                source.setPublicId(publicId);
                source.setSystemId(systemId);
                if (ResourceEntityResolver.logger.isDebugEnabled()) {
                    ResourceEntityResolver.logger.debug("Found XML entity [" + systemId + "]: " + resource);
                }
            }
        }
        return source;
    }
    
    static {
        logger = LogFactory.getLog(ResourceEntityResolver.class);
    }
}
