// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.env;

public class StandardEnvironment extends AbstractEnvironment
{
    public static final String SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME = "systemEnvironment";
    public static final String SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME = "systemProperties";
    
    @Override
    protected void customizePropertySources(final MutablePropertySources propertySources) {
        propertySources.addLast(new MapPropertySource("systemProperties", this.getSystemProperties()));
        propertySources.addLast(new SystemEnvironmentPropertySource("systemEnvironment", this.getSystemEnvironment()));
    }
}
