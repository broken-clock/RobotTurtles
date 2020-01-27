// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.type.ClassKey;
import java.io.Serializable;

public class RootNameLookup implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected transient LRUMap<ClassKey, PropertyName> _rootNames;
    
    public RootNameLookup() {
        this._rootNames = new LRUMap<ClassKey, PropertyName>(20, 200);
    }
    
    public PropertyName findRootName(final JavaType rootType, final MapperConfig<?> config) {
        return this.findRootName(rootType.getRawClass(), config);
    }
    
    public PropertyName findRootName(final Class<?> rootType, final MapperConfig<?> config) {
        final ClassKey key = new ClassKey(rootType);
        PropertyName name = this._rootNames.get(key);
        if (name != null) {
            return name;
        }
        final BeanDescription beanDesc = config.introspectClassAnnotations(rootType);
        final AnnotationIntrospector intr = config.getAnnotationIntrospector();
        final AnnotatedClass ac = beanDesc.getClassInfo();
        name = intr.findRootName(ac);
        if (name == null || !name.hasSimpleName()) {
            name = PropertyName.construct(rootType.getSimpleName());
        }
        this._rootNames.put(key, name);
        return name;
    }
    
    protected Object readResolve() {
        return new RootNameLookup();
    }
}
