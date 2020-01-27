// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.env;

import java.util.Map;

public class MapPropertySource extends EnumerablePropertySource<Map<String, Object>>
{
    public MapPropertySource(final String name, final Map<String, Object> source) {
        super(name, source);
    }
    
    @Override
    public Object getProperty(final String name) {
        return ((Map)this.source).get(name);
    }
    
    @Override
    public String[] getPropertyNames() {
        return (String[])((Map)this.source).keySet().toArray(MapPropertySource.EMPTY_NAMES_ARRAY);
    }
}
