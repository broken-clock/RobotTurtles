// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.core;

public class SpringNamingPolicy extends DefaultNamingPolicy
{
    public static final SpringNamingPolicy INSTANCE;
    
    @Override
    protected String getTag() {
        return "BySpringCGLIB";
    }
    
    static {
        INSTANCE = new SpringNamingPolicy();
    }
}
