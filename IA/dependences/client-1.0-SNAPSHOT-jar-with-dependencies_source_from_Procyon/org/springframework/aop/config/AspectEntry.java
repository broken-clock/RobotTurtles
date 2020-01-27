// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.config;

import org.springframework.util.StringUtils;
import org.springframework.beans.factory.parsing.ParseState;

public class AspectEntry implements ParseState.Entry
{
    private final String id;
    private final String ref;
    
    public AspectEntry(final String id, final String ref) {
        this.id = id;
        this.ref = ref;
    }
    
    @Override
    public String toString() {
        return "Aspect: " + (StringUtils.hasLength(this.id) ? ("id='" + this.id + "'") : ("ref='" + this.ref + "'"));
    }
}
