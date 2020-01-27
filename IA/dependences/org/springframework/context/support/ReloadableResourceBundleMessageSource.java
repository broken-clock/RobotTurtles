// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.support;

import org.springframework.util.StringUtils;
import java.io.InputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import org.springframework.core.io.Resource;
import java.io.IOException;
import java.util.Collection;
import java.util.ArrayList;
import java.text.MessageFormat;
import java.util.Iterator;
import org.springframework.util.Assert;
import java.util.HashMap;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.util.DefaultPropertiesPersister;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.PropertiesPersister;
import java.util.Properties;
import org.springframework.context.ResourceLoaderAware;

public class ReloadableResourceBundleMessageSource extends AbstractMessageSource implements ResourceLoaderAware
{
    private static final String PROPERTIES_SUFFIX = ".properties";
    private static final String XML_SUFFIX = ".xml";
    private String[] basenames;
    private String defaultEncoding;
    private Properties fileEncodings;
    private boolean fallbackToSystemLocale;
    private long cacheMillis;
    private PropertiesPersister propertiesPersister;
    private ResourceLoader resourceLoader;
    private final Map<String, Map<Locale, List<String>>> cachedFilenames;
    private final Map<String, PropertiesHolder> cachedProperties;
    private final Map<Locale, PropertiesHolder> cachedMergedProperties;
    
    public ReloadableResourceBundleMessageSource() {
        this.basenames = new String[0];
        this.fallbackToSystemLocale = true;
        this.cacheMillis = -1L;
        this.propertiesPersister = new DefaultPropertiesPersister();
        this.resourceLoader = new DefaultResourceLoader();
        this.cachedFilenames = new HashMap<String, Map<Locale, List<String>>>();
        this.cachedProperties = new HashMap<String, PropertiesHolder>();
        this.cachedMergedProperties = new HashMap<Locale, PropertiesHolder>();
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
    
    public void setFileEncodings(final Properties fileEncodings) {
        this.fileEncodings = fileEncodings;
    }
    
    public void setFallbackToSystemLocale(final boolean fallbackToSystemLocale) {
        this.fallbackToSystemLocale = fallbackToSystemLocale;
    }
    
    public void setCacheSeconds(final int cacheSeconds) {
        this.cacheMillis = cacheSeconds * 1000;
    }
    
    public void setPropertiesPersister(final PropertiesPersister propertiesPersister) {
        this.propertiesPersister = ((propertiesPersister != null) ? propertiesPersister : new DefaultPropertiesPersister());
    }
    
    @Override
    public void setResourceLoader(final ResourceLoader resourceLoader) {
        this.resourceLoader = ((resourceLoader != null) ? resourceLoader : new DefaultResourceLoader());
    }
    
    @Override
    protected String resolveCodeWithoutArguments(final String code, final Locale locale) {
        if (this.cacheMillis < 0L) {
            final PropertiesHolder propHolder = this.getMergedProperties(locale);
            final String result = propHolder.getProperty(code);
            if (result != null) {
                return result;
            }
        }
        else {
            for (final String basename : this.basenames) {
                final List<String> filenames = this.calculateAllFilenames(basename, locale);
                for (final String filename : filenames) {
                    final PropertiesHolder propHolder2 = this.getProperties(filename);
                    final String result2 = propHolder2.getProperty(code);
                    if (result2 != null) {
                        return result2;
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    protected MessageFormat resolveCode(final String code, final Locale locale) {
        if (this.cacheMillis < 0L) {
            final PropertiesHolder propHolder = this.getMergedProperties(locale);
            final MessageFormat result = propHolder.getMessageFormat(code, locale);
            if (result != null) {
                return result;
            }
        }
        else {
            for (final String basename : this.basenames) {
                final List<String> filenames = this.calculateAllFilenames(basename, locale);
                for (final String filename : filenames) {
                    final PropertiesHolder propHolder2 = this.getProperties(filename);
                    final MessageFormat result2 = propHolder2.getMessageFormat(code, locale);
                    if (result2 != null) {
                        return result2;
                    }
                }
            }
        }
        return null;
    }
    
    protected PropertiesHolder getMergedProperties(final Locale locale) {
        synchronized (this.cachedMergedProperties) {
            PropertiesHolder mergedHolder = this.cachedMergedProperties.get(locale);
            if (mergedHolder != null) {
                return mergedHolder;
            }
            final Properties mergedProps = new Properties();
            mergedHolder = new PropertiesHolder(mergedProps, -1L);
            for (int i = this.basenames.length - 1; i >= 0; --i) {
                final List<String> filenames = this.calculateAllFilenames(this.basenames[i], locale);
                for (int j = filenames.size() - 1; j >= 0; --j) {
                    final String filename = filenames.get(j);
                    final PropertiesHolder propHolder = this.getProperties(filename);
                    if (propHolder.getProperties() != null) {
                        mergedProps.putAll(propHolder.getProperties());
                    }
                }
            }
            this.cachedMergedProperties.put(locale, mergedHolder);
            return mergedHolder;
        }
    }
    
    protected List<String> calculateAllFilenames(final String basename, final Locale locale) {
        synchronized (this.cachedFilenames) {
            Map<Locale, List<String>> localeMap = this.cachedFilenames.get(basename);
            if (localeMap != null) {
                final List<String> filenames = localeMap.get(locale);
                if (filenames != null) {
                    return filenames;
                }
            }
            final List<String> filenames = new ArrayList<String>(7);
            filenames.addAll(this.calculateFilenamesForLocale(basename, locale));
            if (this.fallbackToSystemLocale && !locale.equals(Locale.getDefault())) {
                final List<String> fallbackFilenames = this.calculateFilenamesForLocale(basename, Locale.getDefault());
                for (final String fallbackFilename : fallbackFilenames) {
                    if (!filenames.contains(fallbackFilename)) {
                        filenames.add(fallbackFilename);
                    }
                }
            }
            filenames.add(basename);
            if (localeMap != null) {
                localeMap.put(locale, filenames);
            }
            else {
                localeMap = new HashMap<Locale, List<String>>();
                localeMap.put(locale, filenames);
                this.cachedFilenames.put(basename, localeMap);
            }
            return filenames;
        }
    }
    
    protected List<String> calculateFilenamesForLocale(final String basename, final Locale locale) {
        final List<String> result = new ArrayList<String>(3);
        final String language = locale.getLanguage();
        final String country = locale.getCountry();
        final String variant = locale.getVariant();
        final StringBuilder temp = new StringBuilder(basename);
        temp.append('_');
        if (language.length() > 0) {
            temp.append(language);
            result.add(0, temp.toString());
        }
        temp.append('_');
        if (country.length() > 0) {
            temp.append(country);
            result.add(0, temp.toString());
        }
        if (variant.length() > 0 && (language.length() > 0 || country.length() > 0)) {
            temp.append('_').append(variant);
            result.add(0, temp.toString());
        }
        return result;
    }
    
    protected PropertiesHolder getProperties(final String filename) {
        synchronized (this.cachedProperties) {
            final PropertiesHolder propHolder = this.cachedProperties.get(filename);
            if (propHolder != null && (propHolder.getRefreshTimestamp() < 0L || propHolder.getRefreshTimestamp() > System.currentTimeMillis() - this.cacheMillis)) {
                return propHolder;
            }
            return this.refreshProperties(filename, propHolder);
        }
    }
    
    protected PropertiesHolder refreshProperties(final String filename, PropertiesHolder propHolder) {
        final long refreshTimestamp = (this.cacheMillis < 0L) ? -1L : System.currentTimeMillis();
        Resource resource = this.resourceLoader.getResource(filename + ".properties");
        if (!resource.exists()) {
            resource = this.resourceLoader.getResource(filename + ".xml");
        }
        if (resource.exists()) {
            long fileTimestamp = -1L;
            if (this.cacheMillis >= 0L) {
                try {
                    fileTimestamp = resource.lastModified();
                    if (propHolder != null && propHolder.getFileTimestamp() == fileTimestamp) {
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("Re-caching properties for filename [" + filename + "] - file hasn't been modified");
                        }
                        propHolder.setRefreshTimestamp(refreshTimestamp);
                        return propHolder;
                    }
                }
                catch (IOException ex) {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug(resource + " could not be resolved in the file system - assuming that is hasn't changed", ex);
                    }
                    fileTimestamp = -1L;
                }
            }
            try {
                final Properties props = this.loadProperties(resource, filename);
                propHolder = new PropertiesHolder(props, fileTimestamp);
            }
            catch (IOException ex) {
                if (this.logger.isWarnEnabled()) {
                    this.logger.warn("Could not parse properties file [" + resource.getFilename() + "]", ex);
                }
                propHolder = new PropertiesHolder();
            }
        }
        else {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("No properties file found for [" + filename + "] - neither plain properties nor XML");
            }
            propHolder = new PropertiesHolder();
        }
        propHolder.setRefreshTimestamp(refreshTimestamp);
        this.cachedProperties.put(filename, propHolder);
        return propHolder;
    }
    
    protected Properties loadProperties(final Resource resource, final String filename) throws IOException {
        final InputStream is = resource.getInputStream();
        final Properties props = new Properties();
        try {
            if (resource.getFilename().endsWith(".xml")) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Loading properties [" + resource.getFilename() + "]");
                }
                this.propertiesPersister.loadFromXml(props, is);
            }
            else {
                String encoding = null;
                if (this.fileEncodings != null) {
                    encoding = this.fileEncodings.getProperty(filename);
                }
                if (encoding == null) {
                    encoding = this.defaultEncoding;
                }
                if (encoding != null) {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Loading properties [" + resource.getFilename() + "] with encoding '" + encoding + "'");
                    }
                    this.propertiesPersister.load(props, new InputStreamReader(is, encoding));
                }
                else {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Loading properties [" + resource.getFilename() + "]");
                    }
                    this.propertiesPersister.load(props, is);
                }
            }
            return props;
        }
        finally {
            is.close();
        }
    }
    
    public void clearCache() {
        this.logger.debug("Clearing entire resource bundle cache");
        synchronized (this.cachedProperties) {
            this.cachedProperties.clear();
        }
        synchronized (this.cachedMergedProperties) {
            this.cachedMergedProperties.clear();
        }
    }
    
    public void clearCacheIncludingAncestors() {
        this.clearCache();
        if (this.getParentMessageSource() instanceof ReloadableResourceBundleMessageSource) {
            ((ReloadableResourceBundleMessageSource)this.getParentMessageSource()).clearCacheIncludingAncestors();
        }
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + ": basenames=[" + StringUtils.arrayToCommaDelimitedString(this.basenames) + "]";
    }
    
    protected class PropertiesHolder
    {
        private Properties properties;
        private long fileTimestamp;
        private long refreshTimestamp;
        private final Map<String, Map<Locale, MessageFormat>> cachedMessageFormats;
        
        public PropertiesHolder(final Properties properties, final long fileTimestamp) {
            this.fileTimestamp = -1L;
            this.refreshTimestamp = -1L;
            this.cachedMessageFormats = new HashMap<String, Map<Locale, MessageFormat>>();
            this.properties = properties;
            this.fileTimestamp = fileTimestamp;
        }
        
        public PropertiesHolder() {
            this.fileTimestamp = -1L;
            this.refreshTimestamp = -1L;
            this.cachedMessageFormats = new HashMap<String, Map<Locale, MessageFormat>>();
        }
        
        public Properties getProperties() {
            return this.properties;
        }
        
        public long getFileTimestamp() {
            return this.fileTimestamp;
        }
        
        public void setRefreshTimestamp(final long refreshTimestamp) {
            this.refreshTimestamp = refreshTimestamp;
        }
        
        public long getRefreshTimestamp() {
            return this.refreshTimestamp;
        }
        
        public String getProperty(final String code) {
            if (this.properties == null) {
                return null;
            }
            return this.properties.getProperty(code);
        }
        
        public MessageFormat getMessageFormat(final String code, final Locale locale) {
            if (this.properties == null) {
                return null;
            }
            synchronized (this.cachedMessageFormats) {
                Map<Locale, MessageFormat> localeMap = this.cachedMessageFormats.get(code);
                if (localeMap != null) {
                    final MessageFormat result = localeMap.get(locale);
                    if (result != null) {
                        return result;
                    }
                }
                final String msg = this.properties.getProperty(code);
                if (msg != null) {
                    if (localeMap == null) {
                        localeMap = new HashMap<Locale, MessageFormat>();
                        this.cachedMessageFormats.put(code, localeMap);
                    }
                    final MessageFormat result2 = ReloadableResourceBundleMessageSource.this.createMessageFormat(msg, locale);
                    localeMap.put(locale, result2);
                    return result2;
                }
                return null;
            }
        }
    }
}
