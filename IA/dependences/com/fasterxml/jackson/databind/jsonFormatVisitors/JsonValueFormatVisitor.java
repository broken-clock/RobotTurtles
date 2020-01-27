// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.jsonFormatVisitors;

import java.util.Set;

public interface JsonValueFormatVisitor
{
    void format(final JsonValueFormat p0);
    
    void enumTypes(final Set<String> p0);
    
    public static class Base implements JsonValueFormatVisitor
    {
        @Override
        public void format(final JsonValueFormat format) {
        }
        
        @Override
        public void enumTypes(final Set<String> enums) {
        }
    }
}
