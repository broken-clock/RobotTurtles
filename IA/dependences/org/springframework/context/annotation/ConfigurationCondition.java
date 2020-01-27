// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

public interface ConfigurationCondition extends Condition
{
    ConfigurationPhase getConfigurationPhase();
    
    public enum ConfigurationPhase
    {
        PARSE_CONFIGURATION, 
        REGISTER_BEAN;
    }
}
