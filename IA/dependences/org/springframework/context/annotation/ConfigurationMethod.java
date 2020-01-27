// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.beans.factory.parsing.Location;
import org.springframework.core.type.MethodMetadata;

abstract class ConfigurationMethod
{
    protected final MethodMetadata metadata;
    protected final ConfigurationClass configurationClass;
    
    public ConfigurationMethod(final MethodMetadata metadata, final ConfigurationClass configurationClass) {
        this.metadata = metadata;
        this.configurationClass = configurationClass;
    }
    
    public MethodMetadata getMetadata() {
        return this.metadata;
    }
    
    public ConfigurationClass getConfigurationClass() {
        return this.configurationClass;
    }
    
    public Location getResourceLocation() {
        return new Location(this.configurationClass.getResource(), this.metadata);
    }
    
    String getFullyQualifiedMethodName() {
        return this.metadata.getDeclaringClassName() + "#" + this.metadata.getMethodName();
    }
    
    static String getShortMethodName(final String fullyQualifiedMethodName) {
        return fullyQualifiedMethodName.substring(fullyQualifiedMethodName.indexOf(35) + 1);
    }
    
    public void validate(final ProblemReporter problemReporter) {
    }
    
    @Override
    public String toString() {
        return String.format("[%s:name=%s,declaringClass=%s]", this.getClass().getSimpleName(), this.getMetadata().getMethodName(), this.getMetadata().getDeclaringClassName());
    }
}
