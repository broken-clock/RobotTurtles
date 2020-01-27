// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.env;

import org.springframework.core.convert.support.ConfigurableConversionService;
import java.util.Iterator;
import org.springframework.core.SpringProperties;
import java.security.AccessControlException;
import java.util.Map;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import java.util.Collections;
import java.util.Collection;
import java.util.LinkedHashSet;
import org.apache.commons.logging.LogFactory;
import java.util.Set;
import org.apache.commons.logging.Log;

public abstract class AbstractEnvironment implements ConfigurableEnvironment
{
    public static final String IGNORE_GETENV_PROPERTY_NAME = "spring.getenv.ignore";
    public static final String ACTIVE_PROFILES_PROPERTY_NAME = "spring.profiles.active";
    public static final String DEFAULT_PROFILES_PROPERTY_NAME = "spring.profiles.default";
    protected static final String RESERVED_DEFAULT_PROFILE_NAME = "default";
    protected final Log logger;
    private Set<String> activeProfiles;
    private Set<String> defaultProfiles;
    private final MutablePropertySources propertySources;
    private final ConfigurablePropertyResolver propertyResolver;
    
    public AbstractEnvironment() {
        this.logger = LogFactory.getLog(this.getClass());
        this.activeProfiles = new LinkedHashSet<String>();
        this.defaultProfiles = new LinkedHashSet<String>(this.getReservedDefaultProfiles());
        this.propertySources = new MutablePropertySources(this.logger);
        this.propertyResolver = new PropertySourcesPropertyResolver(this.propertySources);
        this.customizePropertySources(this.propertySources);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(String.format("Initialized %s with PropertySources %s", this.getClass().getSimpleName(), this.propertySources));
        }
    }
    
    protected void customizePropertySources(final MutablePropertySources propertySources) {
    }
    
    protected Set<String> getReservedDefaultProfiles() {
        return Collections.singleton("default");
    }
    
    @Override
    public String[] getActiveProfiles() {
        return StringUtils.toStringArray(this.doGetActiveProfiles());
    }
    
    protected Set<String> doGetActiveProfiles() {
        if (this.activeProfiles.isEmpty()) {
            final String profiles = this.getProperty("spring.profiles.active");
            if (StringUtils.hasText(profiles)) {
                this.setActiveProfiles(StringUtils.commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(profiles)));
            }
        }
        return this.activeProfiles;
    }
    
    @Override
    public void setActiveProfiles(final String... profiles) {
        Assert.notNull(profiles, "Profile array must not be null");
        this.activeProfiles.clear();
        for (final String profile : profiles) {
            this.validateProfile(profile);
            this.activeProfiles.add(profile);
        }
    }
    
    @Override
    public void addActiveProfile(final String profile) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(String.format("Activating profile '%s'", profile));
        }
        this.validateProfile(profile);
        this.doGetActiveProfiles();
        this.activeProfiles.add(profile);
    }
    
    @Override
    public String[] getDefaultProfiles() {
        return StringUtils.toStringArray(this.doGetDefaultProfiles());
    }
    
    protected Set<String> doGetDefaultProfiles() {
        if (this.defaultProfiles.equals(this.getReservedDefaultProfiles())) {
            final String profiles = this.getProperty("spring.profiles.default");
            if (StringUtils.hasText(profiles)) {
                this.setDefaultProfiles(StringUtils.commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(profiles)));
            }
        }
        return this.defaultProfiles;
    }
    
    @Override
    public void setDefaultProfiles(final String... profiles) {
        Assert.notNull(profiles, "Profile array must not be null");
        this.defaultProfiles.clear();
        for (final String profile : profiles) {
            this.validateProfile(profile);
            this.defaultProfiles.add(profile);
        }
    }
    
    @Override
    public boolean acceptsProfiles(final String... profiles) {
        Assert.notEmpty(profiles, "Must specify at least one profile");
        for (final String profile : profiles) {
            if (profile != null && profile.length() > 0 && profile.charAt(0) == '!') {
                if (!this.isProfileActive(profile.substring(1))) {
                    return true;
                }
            }
            else if (this.isProfileActive(profile)) {
                return true;
            }
        }
        return false;
    }
    
    protected boolean isProfileActive(final String profile) {
        this.validateProfile(profile);
        return this.doGetActiveProfiles().contains(profile) || (this.doGetActiveProfiles().isEmpty() && this.doGetDefaultProfiles().contains(profile));
    }
    
    protected void validateProfile(final String profile) {
        if (!StringUtils.hasText(profile)) {
            throw new IllegalArgumentException("Invalid profile [" + profile + "]: must contain text");
        }
        if (profile.charAt(0) == '!') {
            throw new IllegalArgumentException("Invalid profile [" + profile + "]: must not begin with ! operator");
        }
    }
    
    @Override
    public MutablePropertySources getPropertySources() {
        return this.propertySources;
    }
    
    @Override
    public Map<String, Object> getSystemEnvironment() {
        if (this.suppressGetenvAccess()) {
            return Collections.emptyMap();
        }
        try {
            return (Map<String, Object>)System.getenv();
        }
        catch (AccessControlException ex) {
            return (Map<String, Object>)new ReadOnlySystemAttributesMap() {
                @Override
                protected String getSystemAttribute(final String attributeName) {
                    try {
                        return System.getenv(attributeName);
                    }
                    catch (AccessControlException ex) {
                        if (AbstractEnvironment.this.logger.isInfoEnabled()) {
                            AbstractEnvironment.this.logger.info(String.format("Caught AccessControlException when accessing system environment variable [%s]; its value will be returned [null]. Reason: %s", attributeName, ex.getMessage()));
                        }
                        return null;
                    }
                }
            };
        }
    }
    
    protected boolean suppressGetenvAccess() {
        return SpringProperties.getFlag("spring.getenv.ignore");
    }
    
    @Override
    public Map<String, Object> getSystemProperties() {
        try {
            return (Map<String, Object>)System.getProperties();
        }
        catch (AccessControlException ex) {
            return (Map<String, Object>)new ReadOnlySystemAttributesMap() {
                @Override
                protected String getSystemAttribute(final String attributeName) {
                    try {
                        return System.getProperty(attributeName);
                    }
                    catch (AccessControlException ex) {
                        if (AbstractEnvironment.this.logger.isInfoEnabled()) {
                            AbstractEnvironment.this.logger.info(String.format("Caught AccessControlException when accessing system property [%s]; its value will be returned [null]. Reason: %s", attributeName, ex.getMessage()));
                        }
                        return null;
                    }
                }
            };
        }
    }
    
    @Override
    public void merge(final ConfigurableEnvironment parent) {
        for (final PropertySource<?> ps : parent.getPropertySources()) {
            if (!this.propertySources.contains(ps.getName())) {
                this.propertySources.addLast(ps);
            }
        }
        for (final String profile : parent.getActiveProfiles()) {
            this.activeProfiles.add(profile);
        }
        if (parent.getDefaultProfiles().length > 0) {
            this.defaultProfiles.remove("default");
            for (final String profile : parent.getDefaultProfiles()) {
                this.defaultProfiles.add(profile);
            }
        }
    }
    
    @Override
    public boolean containsProperty(final String key) {
        return this.propertyResolver.containsProperty(key);
    }
    
    @Override
    public String getProperty(final String key) {
        return this.propertyResolver.getProperty(key);
    }
    
    @Override
    public String getProperty(final String key, final String defaultValue) {
        return this.propertyResolver.getProperty(key, defaultValue);
    }
    
    @Override
    public <T> T getProperty(final String key, final Class<T> targetType) {
        return this.propertyResolver.getProperty(key, targetType);
    }
    
    @Override
    public <T> T getProperty(final String key, final Class<T> targetType, final T defaultValue) {
        return this.propertyResolver.getProperty(key, targetType, defaultValue);
    }
    
    @Override
    public <T> Class<T> getPropertyAsClass(final String key, final Class<T> targetType) {
        return this.propertyResolver.getPropertyAsClass(key, targetType);
    }
    
    @Override
    public String getRequiredProperty(final String key) throws IllegalStateException {
        return this.propertyResolver.getRequiredProperty(key);
    }
    
    @Override
    public <T> T getRequiredProperty(final String key, final Class<T> targetType) throws IllegalStateException {
        return this.propertyResolver.getRequiredProperty(key, targetType);
    }
    
    @Override
    public void setRequiredProperties(final String... requiredProperties) {
        this.propertyResolver.setRequiredProperties(requiredProperties);
    }
    
    @Override
    public void validateRequiredProperties() throws MissingRequiredPropertiesException {
        this.propertyResolver.validateRequiredProperties();
    }
    
    @Override
    public String resolvePlaceholders(final String text) {
        return this.propertyResolver.resolvePlaceholders(text);
    }
    
    @Override
    public String resolveRequiredPlaceholders(final String text) throws IllegalArgumentException {
        return this.propertyResolver.resolveRequiredPlaceholders(text);
    }
    
    @Override
    public void setIgnoreUnresolvableNestedPlaceholders(final boolean ignoreUnresolvableNestedPlaceholders) {
        this.propertyResolver.setIgnoreUnresolvableNestedPlaceholders(ignoreUnresolvableNestedPlaceholders);
    }
    
    @Override
    public void setConversionService(final ConfigurableConversionService conversionService) {
        this.propertyResolver.setConversionService(conversionService);
    }
    
    @Override
    public ConfigurableConversionService getConversionService() {
        return this.propertyResolver.getConversionService();
    }
    
    @Override
    public void setPlaceholderPrefix(final String placeholderPrefix) {
        this.propertyResolver.setPlaceholderPrefix(placeholderPrefix);
    }
    
    @Override
    public void setPlaceholderSuffix(final String placeholderSuffix) {
        this.propertyResolver.setPlaceholderSuffix(placeholderSuffix);
    }
    
    @Override
    public void setValueSeparator(final String valueSeparator) {
        this.propertyResolver.setValueSeparator(valueSeparator);
    }
    
    @Override
    public String toString() {
        return String.format("%s {activeProfiles=%s, defaultProfiles=%s, propertySources=%s}", this.getClass().getSimpleName(), this.activeProfiles, this.defaultProfiles, this.propertySources);
    }
}
