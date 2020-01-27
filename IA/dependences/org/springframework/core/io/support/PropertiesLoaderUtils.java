// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.io.support;

import java.net.URLConnection;
import java.util.Enumeration;
import org.springframework.util.ResourceUtils;
import java.net.URL;
import org.springframework.util.ClassUtils;
import org.springframework.util.Assert;
import org.springframework.core.io.Resource;
import java.io.Reader;
import java.io.InputStream;
import org.springframework.util.PropertiesPersister;
import org.springframework.util.DefaultPropertiesPersister;
import java.io.IOException;
import java.util.Properties;

public abstract class PropertiesLoaderUtils
{
    private static final String XML_FILE_EXTENSION = ".xml";
    
    public static Properties loadProperties(final EncodedResource resource) throws IOException {
        final Properties props = new Properties();
        fillProperties(props, resource);
        return props;
    }
    
    public static void fillProperties(final Properties props, final EncodedResource resource) throws IOException {
        fillProperties(props, resource, new DefaultPropertiesPersister());
    }
    
    static void fillProperties(final Properties props, final EncodedResource resource, final PropertiesPersister persister) throws IOException {
        InputStream stream = null;
        Reader reader = null;
        try {
            final String filename = resource.getResource().getFilename();
            if (filename != null && filename.endsWith(".xml")) {
                stream = resource.getInputStream();
                persister.loadFromXml(props, stream);
            }
            else if (resource.requiresReader()) {
                reader = resource.getReader();
                persister.load(props, reader);
            }
            else {
                stream = resource.getInputStream();
                persister.load(props, stream);
            }
        }
        finally {
            if (stream != null) {
                stream.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }
    
    public static Properties loadProperties(final Resource resource) throws IOException {
        final Properties props = new Properties();
        fillProperties(props, resource);
        return props;
    }
    
    public static void fillProperties(final Properties props, final Resource resource) throws IOException {
        final InputStream is = resource.getInputStream();
        try {
            final String filename = resource.getFilename();
            if (filename != null && filename.endsWith(".xml")) {
                props.loadFromXML(is);
            }
            else {
                props.load(is);
            }
        }
        finally {
            is.close();
        }
    }
    
    public static Properties loadAllProperties(final String resourceName) throws IOException {
        return loadAllProperties(resourceName, null);
    }
    
    public static Properties loadAllProperties(final String resourceName, final ClassLoader classLoader) throws IOException {
        Assert.notNull(resourceName, "Resource name must not be null");
        ClassLoader clToUse = classLoader;
        if (clToUse == null) {
            clToUse = ClassUtils.getDefaultClassLoader();
        }
        final Properties props = new Properties();
        final Enumeration<URL> urls = clToUse.getResources(resourceName);
        while (urls.hasMoreElements()) {
            final URL url = urls.nextElement();
            final URLConnection con = url.openConnection();
            ResourceUtils.useCachesIfNecessary(con);
            final InputStream is = con.getInputStream();
            try {
                if (resourceName != null && resourceName.endsWith(".xml")) {
                    props.loadFromXML(is);
                }
                else {
                    props.load(is);
                }
            }
            finally {
                is.close();
            }
        }
        return props;
    }
}
