// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.core.util.InternCache;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.core.SerializableString;
import java.io.Serializable;

public class PropertyName implements Serializable
{
    private static final long serialVersionUID = 1L;
    private static final String _USE_DEFAULT = "";
    private static final String _NO_NAME = "";
    public static final PropertyName USE_DEFAULT;
    public static final PropertyName NO_NAME;
    protected final String _simpleName;
    protected final String _namespace;
    protected SerializableString _encodedSimple;
    
    public PropertyName(final String simpleName) {
        this(simpleName, null);
    }
    
    public PropertyName(final String simpleName, final String namespace) {
        this._simpleName = ClassUtil.nonNullString(simpleName);
        this._namespace = namespace;
    }
    
    protected Object readResolve() {
        if (this._namespace == null && (this._simpleName == null || "".equals(this._simpleName))) {
            return PropertyName.USE_DEFAULT;
        }
        return this;
    }
    
    public static PropertyName construct(final String simpleName) {
        if (simpleName == null || simpleName.length() == 0) {
            return PropertyName.USE_DEFAULT;
        }
        return new PropertyName(InternCache.instance.intern(simpleName), null);
    }
    
    public static PropertyName construct(String simpleName, final String ns) {
        if (simpleName == null) {
            simpleName = "";
        }
        if (ns == null && simpleName.length() == 0) {
            return PropertyName.USE_DEFAULT;
        }
        return new PropertyName(InternCache.instance.intern(simpleName), ns);
    }
    
    public PropertyName internSimpleName() {
        if (this._simpleName.length() == 0) {
            return this;
        }
        final String interned = InternCache.instance.intern(this._simpleName);
        if (interned == this._simpleName) {
            return this;
        }
        return new PropertyName(interned, this._namespace);
    }
    
    public PropertyName withSimpleName(String simpleName) {
        if (simpleName == null) {
            simpleName = "";
        }
        if (simpleName.equals(this._simpleName)) {
            return this;
        }
        return new PropertyName(simpleName, this._namespace);
    }
    
    public PropertyName withNamespace(final String ns) {
        if (ns == null) {
            if (this._namespace == null) {
                return this;
            }
        }
        else if (ns.equals(this._namespace)) {
            return this;
        }
        return new PropertyName(this._simpleName, ns);
    }
    
    public String getSimpleName() {
        return this._simpleName;
    }
    
    public SerializableString simpleAsEncoded(final MapperConfig<?> config) {
        SerializableString sstr = this._encodedSimple;
        if (sstr == null) {
            if (config == null) {
                sstr = new SerializedString(this._simpleName);
            }
            else {
                sstr = config.compileString(this._simpleName);
            }
            this._encodedSimple = sstr;
        }
        return sstr;
    }
    
    public String getNamespace() {
        return this._namespace;
    }
    
    public boolean hasSimpleName() {
        return this._simpleName.length() > 0;
    }
    
    public boolean hasSimpleName(final String str) {
        return this._simpleName.equals(str);
    }
    
    public boolean hasNamespace() {
        return this._namespace != null;
    }
    
    public boolean isEmpty() {
        return this._namespace == null && this._simpleName.isEmpty();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        final PropertyName other = (PropertyName)o;
        if (this._simpleName == null) {
            if (other._simpleName != null) {
                return false;
            }
        }
        else if (!this._simpleName.equals(other._simpleName)) {
            return false;
        }
        if (this._namespace == null) {
            return null == other._namespace;
        }
        return this._namespace.equals(other._namespace);
    }
    
    @Override
    public int hashCode() {
        if (this._namespace == null) {
            return this._simpleName.hashCode();
        }
        return this._namespace.hashCode() ^ this._simpleName.hashCode();
    }
    
    @Override
    public String toString() {
        if (this._namespace == null) {
            return this._simpleName;
        }
        return "{" + this._namespace + "}" + this._simpleName;
    }
    
    static {
        USE_DEFAULT = new PropertyName("", null);
        NO_NAME = new PropertyName(new String(""), null);
    }
}
