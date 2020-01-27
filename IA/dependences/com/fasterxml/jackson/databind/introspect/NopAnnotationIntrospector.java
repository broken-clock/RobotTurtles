// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.cfg.PackageVersion;
import com.fasterxml.jackson.core.Version;
import java.io.Serializable;
import com.fasterxml.jackson.databind.AnnotationIntrospector;

public abstract class NopAnnotationIntrospector extends AnnotationIntrospector implements Serializable
{
    private static final long serialVersionUID = 1L;
    public static final NopAnnotationIntrospector instance;
    
    @Override
    public Version version() {
        return Version.unknownVersion();
    }
    
    static {
        instance = new NopAnnotationIntrospector() {
            private static final long serialVersionUID = 1L;
            
            @Override
            public Version version() {
                return PackageVersion.VERSION;
            }
        };
    }
}
