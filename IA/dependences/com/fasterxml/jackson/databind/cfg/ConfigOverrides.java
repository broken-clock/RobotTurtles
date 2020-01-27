// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.cfg;

import java.util.HashMap;
import java.util.Iterator;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;
import java.io.Serializable;

public class ConfigOverrides implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected Map<Class<?>, MutableConfigOverride> _overrides;
    protected JsonInclude.Value _defaultInclusion;
    protected JsonSetter.Value _defaultSetterInfo;
    protected VisibilityChecker<?> _visibilityChecker;
    protected Boolean _defaultMergeable;
    
    public ConfigOverrides() {
        this(null, JsonInclude.Value.empty(), JsonSetter.Value.empty(), VisibilityChecker.Std.defaultInstance(), null);
    }
    
    protected ConfigOverrides(final Map<Class<?>, MutableConfigOverride> overrides, final JsonInclude.Value defIncl, final JsonSetter.Value defSetter, final VisibilityChecker<?> defVisibility, final Boolean defMergeable) {
        this._overrides = overrides;
        this._defaultInclusion = defIncl;
        this._defaultSetterInfo = defSetter;
        this._visibilityChecker = defVisibility;
        this._defaultMergeable = defMergeable;
    }
    
    public ConfigOverrides copy() {
        Map<Class<?>, MutableConfigOverride> newOverrides;
        if (this._overrides == null) {
            newOverrides = null;
        }
        else {
            newOverrides = this._newMap();
            for (final Map.Entry<Class<?>, MutableConfigOverride> entry : this._overrides.entrySet()) {
                newOverrides.put(entry.getKey(), entry.getValue().copy());
            }
        }
        return new ConfigOverrides(newOverrides, this._defaultInclusion, this._defaultSetterInfo, this._visibilityChecker, this._defaultMergeable);
    }
    
    public ConfigOverride findOverride(final Class<?> type) {
        if (this._overrides == null) {
            return null;
        }
        return this._overrides.get(type);
    }
    
    public MutableConfigOverride findOrCreateOverride(final Class<?> type) {
        if (this._overrides == null) {
            this._overrides = this._newMap();
        }
        MutableConfigOverride override = this._overrides.get(type);
        if (override == null) {
            override = new MutableConfigOverride();
            this._overrides.put(type, override);
        }
        return override;
    }
    
    public JsonInclude.Value getDefaultInclusion() {
        return this._defaultInclusion;
    }
    
    public JsonSetter.Value getDefaultSetterInfo() {
        return this._defaultSetterInfo;
    }
    
    public Boolean getDefaultMergeable() {
        return this._defaultMergeable;
    }
    
    public VisibilityChecker<?> getDefaultVisibility() {
        return this._visibilityChecker;
    }
    
    public void setDefaultInclusion(final JsonInclude.Value v) {
        this._defaultInclusion = v;
    }
    
    public void setDefaultSetterInfo(final JsonSetter.Value v) {
        this._defaultSetterInfo = v;
    }
    
    public void setDefaultMergeable(final Boolean v) {
        this._defaultMergeable = v;
    }
    
    public void setDefaultVisibility(final VisibilityChecker<?> v) {
        this._visibilityChecker = v;
    }
    
    protected Map<Class<?>, MutableConfigOverride> _newMap() {
        return new HashMap<Class<?>, MutableConfigOverride>();
    }
}
