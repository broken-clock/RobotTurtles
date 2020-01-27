// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

public class SpringVersion
{
    public static String getVersion() {
        final Package pkg = SpringVersion.class.getPackage();
        return (pkg != null) ? pkg.getImplementationVersion() : null;
    }
}
