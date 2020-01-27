// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.support;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.PropertyResourceBundle;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.io.IOException;
import java.net.URLConnection;
import java.net.URL;
import java.security.PrivilegedExceptionAction;
import java.io.InputStream;
import org.springframework.util.StringUtils;
import java.util.MissingResourceException;
import org.springframework.util.Assert;
import java.util.HashMap;
import org.springframework.util.ClassUtils;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.BeanClassLoaderAware;

public class ResourceBundleMessageSource extends AbstractMessageSource implements BeanClassLoaderAware
{
    private String[] basenames;
    private String defaultEncoding;
    private boolean fallbackToSystemLocale;
    private long cacheMillis;
    private ClassLoader bundleClassLoader;
    private ClassLoader beanClassLoader;
    private final Map<String, Map<Locale, ResourceBundle>> cachedResourceBundles;
    private final Map<ResourceBundle, Map<String, Map<Locale, MessageFormat>>> cachedBundleMessageFormats;
    
    public ResourceBundleMessageSource() {
        this.basenames = new String[0];
        this.fallbackToSystemLocale = true;
        this.cacheMillis = -1L;
        this.beanClassLoader = ClassUtils.getDefaultClassLoader();
        this.cachedResourceBundles = new HashMap<String, Map<Locale, ResourceBundle>>();
        this.cachedBundleMessageFormats = new HashMap<ResourceBundle, Map<String, Map<Locale, MessageFormat>>>();
    }
    
    public void setBasename(final String basename) {
        this.setBasenames(basename);
    }
    
    public void setBasenames(final String... basenames) {
        if (basenames != null) {
            this.basenames = new String[basenames.length];
            for (int i = 0; i < basenames.length; ++i) {
                final String basename = basenames[i];
                Assert.hasText(basename, "Basename must not be empty");
                this.basenames[i] = basename.trim();
            }
        }
        else {
            this.basenames = new String[0];
        }
    }
    
    public void setDefaultEncoding(final String defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
    }
    
    public void setFallbackToSystemLocale(final boolean fallbackToSystemLocale) {
        this.fallbackToSystemLocale = fallbackToSystemLocale;
    }
    
    public void setCacheSeconds(final int cacheSeconds) {
        this.cacheMillis = cacheSeconds * 1000;
    }
    
    public void setBundleClassLoader(final ClassLoader classLoader) {
        this.bundleClassLoader = classLoader;
    }
    
    protected ClassLoader getBundleClassLoader() {
        return (this.bundleClassLoader != null) ? this.bundleClassLoader : this.beanClassLoader;
    }
    
    @Override
    public void setBeanClassLoader(final ClassLoader classLoader) {
        this.beanClassLoader = ((classLoader != null) ? classLoader : ClassUtils.getDefaultClassLoader());
    }
    
    @Override
    protected String resolveCodeWithoutArguments(final String code, final Locale locale) {
        String result = null;
        for (int i = 0; result == null && i < this.basenames.length; ++i) {
            final ResourceBundle bundle = this.getResourceBundle(this.basenames[i], locale);
            if (bundle != null) {
                result = this.getStringOrNull(bundle, code);
            }
        }
        return result;
    }
    
    @Override
    protected MessageFormat resolveCode(final String code, final Locale locale) {
        MessageFormat messageFormat = null;
        for (int i = 0; messageFormat == null && i < this.basenames.length; ++i) {
            final ResourceBundle bundle = this.getResourceBundle(this.basenames[i], locale);
            if (bundle != null) {
                messageFormat = this.getMessageFormat(bundle, code, locale);
            }
        }
        return messageFormat;
    }
    
    protected ResourceBundle getResourceBundle(final String basename, final Locale locale) {
        if (this.cacheMillis >= 0L) {
            return this.doGetBundle(basename, locale);
        }
        synchronized (this.cachedResourceBundles) {
            Map<Locale, ResourceBundle> localeMap = this.cachedResourceBundles.get(basename);
            if (localeMap != null) {
                final ResourceBundle bundle = localeMap.get(locale);
                if (bundle != null) {
                    return bundle;
                }
            }
            try {
                final ResourceBundle bundle = this.doGetBundle(basename, locale);
                if (localeMap == null) {
                    localeMap = new HashMap<Locale, ResourceBundle>();
                    this.cachedResourceBundles.put(basename, localeMap);
                }
                localeMap.put(locale, bundle);
                return bundle;
            }
            catch (MissingResourceException ex) {
                if (this.logger.isWarnEnabled()) {
                    this.logger.warn("ResourceBundle [" + basename + "] not found for MessageSource: " + ex.getMessage());
                }
                return null;
            }
        }
    }
    
    protected ResourceBundle doGetBundle(final String basename, final Locale locale) throws MissingResourceException {
        if ((this.defaultEncoding != null && !"ISO-8859-1".equals(this.defaultEncoding)) || !this.fallbackToSystemLocale || this.cacheMillis >= 0L) {
            return ResourceBundle.getBundle(basename, locale, this.getBundleClassLoader(), new MessageSourceControl());
        }
        return ResourceBundle.getBundle(basename, locale, this.getBundleClassLoader());
    }
    
    protected MessageFormat getMessageFormat(final ResourceBundle bundle, final String code, final Locale locale) throws MissingResourceException {
        synchronized (this.cachedBundleMessageFormats) {
            Map<String, Map<Locale, MessageFormat>> codeMap = this.cachedBundleMessageFormats.get(bundle);
            Map<Locale, MessageFormat> localeMap = null;
            if (codeMap != null) {
                localeMap = codeMap.get(code);
                if (localeMap != null) {
                    final MessageFormat result = localeMap.get(locale);
                    if (result != null) {
                        return result;
                    }
                }
            }
            final String msg = this.getStringOrNull(bundle, code);
            if (msg != null) {
                if (codeMap == null) {
                    codeMap = new HashMap<String, Map<Locale, MessageFormat>>();
                    this.cachedBundleMessageFormats.put(bundle, codeMap);
                }
                if (localeMap == null) {
                    localeMap = new HashMap<Locale, MessageFormat>();
                    codeMap.put(code, localeMap);
                }
                final MessageFormat result2 = this.createMessageFormat(msg, locale);
                localeMap.put(locale, result2);
                return result2;
            }
            return null;
        }
    }
    
    private String getStringOrNull(final ResourceBundle bundle, final String key) {
        try {
            return bundle.getString(key);
        }
        catch (MissingResourceException ex) {
            return null;
        }
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + ": basenames=[" + StringUtils.arrayToCommaDelimitedString(this.basenames) + "]";
    }
    
    private class MessageSourceControl extends ResourceBundle.Control
    {
        @Override
        public ResourceBundle newBundle(final String baseName, final Locale locale, final String format, final ClassLoader loader, final boolean reload) throws IllegalAccessException, InstantiationException, IOException {
            if (format.equals("java.properties")) {
                final String bundleName = this.toBundleName(baseName, locale);
                final String resourceName = this.toResourceName(bundleName, "properties");
                final ClassLoader classLoader = loader;
                final boolean reloadFlag = reload;
                InputStream stream;
                try {
                    stream = AccessController.doPrivileged((PrivilegedExceptionAction<InputStream>)new PrivilegedExceptionAction<InputStream>() {
                        @Override
                        public InputStream run() throws IOException {
                            InputStream is = null;
                            if (reloadFlag) {
                                final URL url = classLoader.getResource(resourceName);
                                if (url != null) {
                                    final URLConnection connection = url.openConnection();
                                    if (connection != null) {
                                        connection.setUseCaches(false);
                                        is = connection.getInputStream();
                                    }
                                }
                            }
                            else {
                                is = classLoader.getResourceAsStream(resourceName);
                            }
                            return is;
                        }
                    });
                }
                catch (PrivilegedActionException ex) {
                    throw (IOException)ex.getException();
                }
                if (stream != null) {
                    try {
                        PropertyResourceBundle propertyResourceBundle;
                        if (ResourceBundleMessageSource.this.defaultEncoding != null) {
                            final InputStreamReader reader;
                            propertyResourceBundle = new PropertyResourceBundle(reader);
                            reader = new InputStreamReader(stream, ResourceBundleMessageSource.this.defaultEncoding);
                        }
                        else {
                            propertyResourceBundle = new PropertyResourceBundle(stream);
                        }
                        return propertyResourceBundle;
                    }
                    finally {
                        stream.close();
                    }
                }
                return null;
            }
            return super.newBundle(baseName, locale, format, loader, reload);
        }
        
        @Override
        public Locale getFallbackLocale(final String baseName, final Locale locale) {
            return ResourceBundleMessageSource.this.fallbackToSystemLocale ? super.getFallbackLocale(baseName, locale) : null;
        }
        
        @Override
        public long getTimeToLive(final String baseName, final Locale locale) {
            return (ResourceBundleMessageSource.this.cacheMillis >= 0L) ? ResourceBundleMessageSource.this.cacheMillis : super.getTimeToLive(baseName, locale);
        }
        
        @Override
        public boolean needsReload(final String baseName, final Locale locale, final String format, final ClassLoader loader, final ResourceBundle bundle, final long loadTime) {
            if (super.needsReload(baseName, locale, format, loader, bundle, loadTime)) {
                ResourceBundleMessageSource.this.cachedBundleMessageFormats.remove(bundle);
                return true;
            }
            return false;
        }
    }
}
