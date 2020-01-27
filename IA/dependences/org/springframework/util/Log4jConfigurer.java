// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

import org.apache.log4j.LogManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

public abstract class Log4jConfigurer
{
    public static final String CLASSPATH_URL_PREFIX = "classpath:";
    public static final String XML_FILE_EXTENSION = ".xml";
    
    public static void initLogging(final String location) throws FileNotFoundException {
        final String resolvedLocation = SystemPropertyUtils.resolvePlaceholders(location);
        final URL url = ResourceUtils.getURL(resolvedLocation);
        if (resolvedLocation.toLowerCase().endsWith(".xml")) {
            DOMConfigurator.configure(url);
        }
        else {
            PropertyConfigurator.configure(url);
        }
    }
    
    public static void initLogging(final String location, final long refreshInterval) throws FileNotFoundException {
        final String resolvedLocation = SystemPropertyUtils.resolvePlaceholders(location);
        final File file = ResourceUtils.getFile(resolvedLocation);
        if (!file.exists()) {
            throw new FileNotFoundException("Log4j config file [" + resolvedLocation + "] not found");
        }
        if (resolvedLocation.toLowerCase().endsWith(".xml")) {
            DOMConfigurator.configureAndWatch(file.getAbsolutePath(), refreshInterval);
        }
        else {
            PropertyConfigurator.configureAndWatch(file.getAbsolutePath(), refreshInterval);
        }
    }
    
    public static void shutdownLogging() {
        LogManager.shutdown();
    }
    
    public static void setWorkingDirSystemProperty(final String key) {
        System.setProperty(key, new File("").getAbsolutePath());
    }
}
