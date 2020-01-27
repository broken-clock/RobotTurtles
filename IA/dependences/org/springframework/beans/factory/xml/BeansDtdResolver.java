// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.xml;

import org.apache.commons.logging.LogFactory;
import java.util.Arrays;
import org.springframework.core.io.Resource;
import java.io.IOException;
import org.springframework.core.io.ClassPathResource;
import org.xml.sax.InputSource;
import org.apache.commons.logging.Log;
import org.xml.sax.EntityResolver;

public class BeansDtdResolver implements EntityResolver
{
    private static final String DTD_EXTENSION = ".dtd";
    private static final String[] DTD_NAMES;
    private static final Log logger;
    
    @Override
    public InputSource resolveEntity(final String publicId, final String systemId) throws IOException {
        if (BeansDtdResolver.logger.isTraceEnabled()) {
            BeansDtdResolver.logger.trace("Trying to resolve XML entity with public ID [" + publicId + "] and system ID [" + systemId + "]");
        }
        if (systemId != null && systemId.endsWith(".dtd")) {
            final int lastPathSeparator = systemId.lastIndexOf("/");
            for (final String DTD_NAME : BeansDtdResolver.DTD_NAMES) {
                final int dtdNameStart = systemId.indexOf(DTD_NAME);
                if (dtdNameStart > lastPathSeparator) {
                    final String dtdFile = systemId.substring(dtdNameStart);
                    if (BeansDtdResolver.logger.isTraceEnabled()) {
                        BeansDtdResolver.logger.trace("Trying to locate [" + dtdFile + "] in Spring jar");
                    }
                    try {
                        final Resource resource = new ClassPathResource(dtdFile, this.getClass());
                        final InputSource source = new InputSource(resource.getInputStream());
                        source.setPublicId(publicId);
                        source.setSystemId(systemId);
                        if (BeansDtdResolver.logger.isDebugEnabled()) {
                            BeansDtdResolver.logger.debug("Found beans DTD [" + systemId + "] in classpath: " + dtdFile);
                        }
                        return source;
                    }
                    catch (IOException ex) {
                        if (BeansDtdResolver.logger.isDebugEnabled()) {
                            BeansDtdResolver.logger.debug("Could not resolve beans DTD [" + systemId + "]: not found in class path", ex);
                        }
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return "EntityResolver for DTDs " + Arrays.toString(BeansDtdResolver.DTD_NAMES);
    }
    
    static {
        DTD_NAMES = new String[] { "spring-beans-2.0", "spring-beans" };
        logger = LogFactory.getLog(BeansDtdResolver.class);
    }
}
