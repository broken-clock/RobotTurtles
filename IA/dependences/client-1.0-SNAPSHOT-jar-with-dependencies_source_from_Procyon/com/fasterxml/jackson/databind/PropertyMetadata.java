// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.annotation.Nulls;
import java.io.Serializable;

public class PropertyMetadata implements Serializable
{
    private static final long serialVersionUID = -1L;
    public static final PropertyMetadata STD_REQUIRED;
    public static final PropertyMetadata STD_OPTIONAL;
    public static final PropertyMetadata STD_REQUIRED_OR_OPTIONAL;
    protected final Boolean _required;
    protected final String _description;
    protected final Integer _index;
    protected final String _defaultValue;
    protected final transient MergeInfo _mergeInfo;
    protected Nulls _valueNulls;
    protected Nulls _contentNulls;
    
    protected PropertyMetadata(final Boolean req, final String desc, final Integer index, final String def, final MergeInfo mergeInfo, final Nulls valueNulls, final Nulls contentNulls) {
        this._required = req;
        this._description = desc;
        this._index = index;
        this._defaultValue = ((def == null || def.isEmpty()) ? null : def);
        this._mergeInfo = mergeInfo;
        this._valueNulls = valueNulls;
        this._contentNulls = contentNulls;
    }
    
    public static PropertyMetadata construct(final Boolean req, final String desc, final Integer index, final String defaultValue) {
        if (desc != null || index != null || defaultValue != null) {
            return new PropertyMetadata(req, desc, index, defaultValue, null, null, null);
        }
        if (req == null) {
            return PropertyMetadata.STD_REQUIRED_OR_OPTIONAL;
        }
        return req ? PropertyMetadata.STD_REQUIRED : PropertyMetadata.STD_OPTIONAL;
    }
    
    @Deprecated
    public static PropertyMetadata construct(final boolean req, final String desc, final Integer index, final String defaultValue) {
        if (desc != null || index != null || defaultValue != null) {
            return new PropertyMetadata(req, desc, index, defaultValue, null, null, null);
        }
        return req ? PropertyMetadata.STD_REQUIRED : PropertyMetadata.STD_OPTIONAL;
    }
    
    protected Object readResolve() {
        if (this._description != null || this._index != null || this._defaultValue != null || this._mergeInfo != null || this._valueNulls != null || this._contentNulls != null) {
            return this;
        }
        if (this._required == null) {
            return PropertyMetadata.STD_REQUIRED_OR_OPTIONAL;
        }
        return this._required ? PropertyMetadata.STD_REQUIRED : PropertyMetadata.STD_OPTIONAL;
    }
    
    public PropertyMetadata withDescription(final String desc) {
        return new PropertyMetadata(this._required, desc, this._index, this._defaultValue, this._mergeInfo, this._valueNulls, this._contentNulls);
    }
    
    public PropertyMetadata withMergeInfo(final MergeInfo mergeInfo) {
        return new PropertyMetadata(this._required, this._description, this._index, this._defaultValue, mergeInfo, this._valueNulls, this._contentNulls);
    }
    
    public PropertyMetadata withNulls(final Nulls valueNulls, final Nulls contentNulls) {
        return new PropertyMetadata(this._required, this._description, this._index, this._defaultValue, this._mergeInfo, valueNulls, contentNulls);
    }
    
    public PropertyMetadata withDefaultValue(String def) {
        if (def == null || def.isEmpty()) {
            if (this._defaultValue == null) {
                return this;
            }
            def = null;
        }
        else if (def.equals(this._defaultValue)) {
            return this;
        }
        return new PropertyMetadata(this._required, this._description, this._index, def, this._mergeInfo, this._valueNulls, this._contentNulls);
    }
    
    public PropertyMetadata withIndex(final Integer index) {
        return new PropertyMetadata(this._required, this._description, index, this._defaultValue, this._mergeInfo, this._valueNulls, this._contentNulls);
    }
    
    public PropertyMetadata withRequired(final Boolean b) {
        if (b == null) {
            if (this._required == null) {
                return this;
            }
        }
        else if (b.equals(this._required)) {
            return this;
        }
        return new PropertyMetadata(b, this._description, this._index, this._defaultValue, this._mergeInfo, this._valueNulls, this._contentNulls);
    }
    
    public String getDescription() {
        return this._description;
    }
    
    public String getDefaultValue() {
        return this._defaultValue;
    }
    
    public boolean hasDefaultValue() {
        return this._defaultValue != null;
    }
    
    public boolean isRequired() {
        return this._required != null && this._required;
    }
    
    public Boolean getRequired() {
        return this._required;
    }
    
    public Integer getIndex() {
        return this._index;
    }
    
    public boolean hasIndex() {
        return this._index != null;
    }
    
    public MergeInfo getMergeInfo() {
        return this._mergeInfo;
    }
    
    public Nulls getValueNulls() {
        return this._valueNulls;
    }
    
    public Nulls getContentNulls() {
        return this._contentNulls;
    }
    
    static {
        STD_REQUIRED = new PropertyMetadata(Boolean.TRUE, null, null, null, null, null, null);
        STD_OPTIONAL = new PropertyMetadata(Boolean.FALSE, null, null, null, null, null, null);
        STD_REQUIRED_OR_OPTIONAL = new PropertyMetadata(null, null, null, null, null, null, null);
    }
    
    public static final class MergeInfo
    {
        public final AnnotatedMember getter;
        public final boolean fromDefaults;
        
        protected MergeInfo(final AnnotatedMember getter, final boolean fromDefaults) {
            this.getter = getter;
            this.fromDefaults = fromDefaults;
        }
        
        public static MergeInfo createForDefaults(final AnnotatedMember getter) {
            return new MergeInfo(getter, true);
        }
        
        public static MergeInfo createForTypeOverride(final AnnotatedMember getter) {
            return new MergeInfo(getter, false);
        }
        
        public static MergeInfo createForPropertyOverride(final AnnotatedMember getter) {
            return new MergeInfo(getter, false);
        }
    }
}
