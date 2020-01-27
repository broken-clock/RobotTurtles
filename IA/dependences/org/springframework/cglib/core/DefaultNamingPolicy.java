// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.core;

public class DefaultNamingPolicy implements NamingPolicy
{
    public static final DefaultNamingPolicy INSTANCE;
    
    public String getClassName(String prefix, final String source, final Object key, final Predicate names) {
        if (prefix == null) {
            prefix = "org.springframework.cglib.empty.Object";
        }
        else if (prefix.startsWith("java")) {
            prefix = "$" + prefix;
        }
        String attempt;
        final String base = attempt = prefix + "$$" + source.substring(source.lastIndexOf(46) + 1) + this.getTag() + "$$" + Integer.toHexString(key.hashCode());
        for (int index = 2; names.evaluate(attempt); attempt = base + "_" + index++) {}
        return attempt;
    }
    
    protected String getTag() {
        return "ByCGLIB";
    }
    
    public int hashCode() {
        return this.getTag().hashCode();
    }
    
    public boolean equals(final Object o) {
        return o instanceof DefaultNamingPolicy && ((DefaultNamingPolicy)o).getTag().equals(this.getTag());
    }
    
    static {
        INSTANCE = new DefaultNamingPolicy();
    }
}
