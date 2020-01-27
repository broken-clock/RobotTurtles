// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

public class DefaultParameterNameDiscoverer extends PrioritizedParameterNameDiscoverer
{
    private static final boolean standardReflectionAvailable;
    
    public DefaultParameterNameDiscoverer() {
        if (DefaultParameterNameDiscoverer.standardReflectionAvailable) {
            this.addDiscoverer(new StandardReflectionParameterNameDiscoverer());
        }
        this.addDiscoverer(new LocalVariableTableParameterNameDiscoverer());
    }
    
    static {
        standardReflectionAvailable = (JdkVersion.getMajorJavaVersion() >= 5);
    }
}
