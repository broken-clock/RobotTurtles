// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.introspect;

import java.util.Collections;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.PropertyName;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyMetadata;
import java.io.Serializable;
import com.fasterxml.jackson.databind.BeanProperty;

public abstract class ConcreteBeanPropertyBase implements BeanProperty, Serializable
{
    private static final long serialVersionUID = 1L;
    protected final PropertyMetadata _metadata;
    protected transient JsonFormat.Value _propertyFormat;
    protected transient List<PropertyName> _aliases;
    
    protected ConcreteBeanPropertyBase(final PropertyMetadata md) {
        this._metadata = ((md == null) ? PropertyMetadata.STD_REQUIRED_OR_OPTIONAL : md);
    }
    
    protected ConcreteBeanPropertyBase(final ConcreteBeanPropertyBase src) {
        this._metadata = src._metadata;
        this._propertyFormat = src._propertyFormat;
    }
    
    @Override
    public boolean isRequired() {
        return this._metadata.isRequired();
    }
    
    @Override
    public PropertyMetadata getMetadata() {
        return this._metadata;
    }
    
    @Override
    public boolean isVirtual() {
        return false;
    }
    
    @Deprecated
    @Override
    public final JsonFormat.Value findFormatOverrides(final AnnotationIntrospector intr) {
        JsonFormat.Value f = null;
        if (intr != null) {
            final AnnotatedMember member = this.getMember();
            if (member != null) {
                f = intr.findFormat(member);
            }
        }
        if (f == null) {
            f = ConcreteBeanPropertyBase.EMPTY_FORMAT;
        }
        return f;
    }
    
    @Override
    public JsonFormat.Value findPropertyFormat(final MapperConfig<?> config, final Class<?> baseType) {
        JsonFormat.Value v = this._propertyFormat;
        if (v == null) {
            final JsonFormat.Value v2 = config.getDefaultPropertyFormat(baseType);
            JsonFormat.Value v3 = null;
            final AnnotationIntrospector intr = config.getAnnotationIntrospector();
            if (intr != null) {
                final AnnotatedMember member = this.getMember();
                if (member != null) {
                    v3 = intr.findFormat(member);
                }
            }
            if (v2 == null) {
                v = ((v3 == null) ? ConcreteBeanPropertyBase.EMPTY_FORMAT : v3);
            }
            else {
                v = ((v3 == null) ? v2 : v2.withOverrides(v3));
            }
            this._propertyFormat = v;
        }
        return v;
    }
    
    @Override
    public JsonInclude.Value findPropertyInclusion(final MapperConfig<?> config, final Class<?> baseType) {
        final AnnotationIntrospector intr = config.getAnnotationIntrospector();
        final AnnotatedMember member = this.getMember();
        if (member == null) {
            final JsonInclude.Value def = config.getDefaultPropertyInclusion(baseType);
            return def;
        }
        final JsonInclude.Value v0 = config.getDefaultInclusion(baseType, member.getRawType());
        if (intr == null) {
            return v0;
        }
        final JsonInclude.Value v2 = intr.findPropertyInclusion(member);
        if (v0 == null) {
            return v2;
        }
        return v0.withOverrides(v2);
    }
    
    @Override
    public List<PropertyName> findAliases(final MapperConfig<?> config) {
        List<PropertyName> aliases = this._aliases;
        if (aliases == null) {
            final AnnotationIntrospector intr = config.getAnnotationIntrospector();
            if (intr != null) {
                aliases = intr.findPropertyAliases(this.getMember());
            }
            if (aliases == null) {
                aliases = Collections.emptyList();
            }
            this._aliases = aliases;
        }
        return aliases;
    }
}
