// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.env;

import java.util.Map;
import java.util.Properties;

public class PropertiesPropertySource extends MapPropertySource
{
    public PropertiesPropertySource(final String name, final Properties source) {
        super(name, (Map<String, Object>)source);
    }
}
