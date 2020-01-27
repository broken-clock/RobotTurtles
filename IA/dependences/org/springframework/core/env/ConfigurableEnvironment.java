// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.env;

import java.util.Map;

public interface ConfigurableEnvironment extends Environment, ConfigurablePropertyResolver
{
    void setActiveProfiles(final String... p0);
    
    void addActiveProfile(final String p0);
    
    void setDefaultProfiles(final String... p0);
    
    MutablePropertySources getPropertySources();
    
    Map<String, Object> getSystemEnvironment();
    
    Map<String, Object> getSystemProperties();
    
    void merge(final ConfigurableEnvironment p0);
}
