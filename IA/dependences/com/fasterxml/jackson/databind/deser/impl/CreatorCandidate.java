// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import com.fasterxml.jackson.databind.AnnotationIntrospector;

public final class CreatorCandidate
{
    protected final AnnotationIntrospector _intr;
    protected final AnnotatedWithParams _creator;
    protected final int _paramCount;
    protected final Param[] _params;
    
    protected CreatorCandidate(final AnnotationIntrospector intr, final AnnotatedWithParams ct, final Param[] params, final int count) {
        this._intr = intr;
        this._creator = ct;
        this._params = params;
        this._paramCount = count;
    }
    
    public static CreatorCandidate construct(final AnnotationIntrospector intr, final AnnotatedWithParams creator, final BeanPropertyDefinition[] propDefs) {
        final int pcount = creator.getParameterCount();
        final Param[] params = new Param[pcount];
        for (int i = 0; i < pcount; ++i) {
            final AnnotatedParameter annParam = creator.getParameter(i);
            final JacksonInject.Value injectId = intr.findInjectableValue(annParam);
            params[i] = new Param(annParam, (propDefs == null) ? null : propDefs[i], injectId);
        }
        return new CreatorCandidate(intr, creator, params, pcount);
    }
    
    public AnnotatedWithParams creator() {
        return this._creator;
    }
    
    public int paramCount() {
        return this._paramCount;
    }
    
    public JacksonInject.Value injection(final int i) {
        return this._params[i].injection;
    }
    
    public AnnotatedParameter parameter(final int i) {
        return this._params[i].annotated;
    }
    
    public BeanPropertyDefinition propertyDef(final int i) {
        return this._params[i].propDef;
    }
    
    public PropertyName paramName(final int i) {
        final BeanPropertyDefinition propDef = this._params[i].propDef;
        if (propDef != null) {
            return propDef.getFullName();
        }
        return null;
    }
    
    public PropertyName explicitParamName(final int i) {
        final BeanPropertyDefinition propDef = this._params[i].propDef;
        if (propDef != null && propDef.isExplicitlyNamed()) {
            return propDef.getFullName();
        }
        return null;
    }
    
    public PropertyName findImplicitParamName(final int i) {
        final String str = this._intr.findImplicitPropertyName(this._params[i].annotated);
        if (str != null && !str.isEmpty()) {
            return PropertyName.construct(str);
        }
        return null;
    }
    
    public int findOnlyParamWithoutInjection() {
        int missing = -1;
        for (int i = 0; i < this._paramCount; ++i) {
            if (this._params[i].injection == null) {
                if (missing >= 0) {
                    return -1;
                }
                missing = i;
            }
        }
        return missing;
    }
    
    @Override
    public String toString() {
        return this._creator.toString();
    }
    
    public static final class Param
    {
        public final AnnotatedParameter annotated;
        public final BeanPropertyDefinition propDef;
        public final JacksonInject.Value injection;
        
        public Param(final AnnotatedParameter p, final BeanPropertyDefinition pd, final JacksonInject.Value i) {
            this.annotated = p;
            this.propDef = pd;
            this.injection = i;
        }
        
        public PropertyName fullName() {
            if (this.propDef == null) {
                return null;
            }
            return this.propDef.getFullName();
        }
        
        public boolean hasFullName() {
            if (this.propDef == null) {
                return false;
            }
            final PropertyName n = this.propDef.getFullName();
            return n.hasSimpleName();
        }
    }
}
