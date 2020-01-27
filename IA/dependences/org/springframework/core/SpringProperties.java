// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

import java.io.InputStream;
import java.net.URL;
import java.io.IOException;
import org.apache.commons.logging.LogFactory;
import java.util.Properties;
import org.apache.commons.logging.Log;

public abstract class SpringProperties
{
    private static final String PROPERTIES_RESOURCE_LOCATION = "spring.properties";
    private static final Log logger;
    private static final Properties localProperties;
    
    public static void setProperty(final String key, final String value) {
        if (value != null) {
            SpringProperties.localProperties.setProperty(key, value);
        }
        else {
            SpringProperties.localProperties.remove(key);
        }
    }
    
    public static String getProperty(final String key) {
        String value = SpringProperties.localProperties.getProperty(key);
        if (value == null) {
            try {
                value = System.getProperty(key);
            }
            catch (Throwable ex) {
                if (SpringProperties.logger.isDebugEnabled()) {
                    SpringProperties.logger.debug("Could not retrieve system property '" + key + "': " + ex);
                }
            }
        }
        return value;
    }
    
    public static void setFlag(final String key) {
        SpringProperties.localProperties.put(key, Boolean.TRUE.toString());
    }
    
    public static boolean getFlag(final String key) {
        return Boolean.parseBoolean(getProperty(key));
    }
    
    static {
        logger = LogFactory.getLog(SpringProperties.class);
        localProperties = new Properties();
        try {
            final ClassLoader cl = SpringProperties.class.getClassLoader();
            final URL url = cl.getResource("spring.properties");
            if (url != null) {
                SpringProperties.logger.info("Found 'spring.properties' file in local classpath");
                final InputStream is = url.openStream();
                try {
                    SpringProperties.localProperties.load(is);
                }
                finally {
                    is.close();
                }
            }
        }
        catch (IOException ex) {
            if (SpringProperties.logger.isInfoEnabled()) {
                SpringProperties.logger.info("Could not load 'spring.properties' file from local classpath: " + ex);
            }
        }
    }
}
