// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.impl;

import java.util.HashMap;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.reflect.InvocationTargetException;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonParser;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.util.Iterator;
import java.util.Collections;
import java.util.Arrays;
import java.util.Collection;
import com.fasterxml.jackson.databind.PropertyName;
import java.util.List;
import java.util.Map;
import java.io.Serializable;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;

public class BeanPropertyMap implements Iterable<SettableBeanProperty>, Serializable
{
    private static final long serialVersionUID = 2L;
    protected final boolean _caseInsensitive;
    private int _hashMask;
    private int _size;
    private int _spillCount;
    private Object[] _hashArea;
    private final SettableBeanProperty[] _propsInOrder;
    private final Map<String, List<PropertyName>> _aliasDefs;
    private final Map<String, String> _aliasMapping;
    
    public BeanPropertyMap(final boolean caseInsensitive, final Collection<SettableBeanProperty> props, final Map<String, List<PropertyName>> aliasDefs) {
        this._caseInsensitive = caseInsensitive;
        this._propsInOrder = props.toArray(new SettableBeanProperty[props.size()]);
        this._aliasDefs = aliasDefs;
        this._aliasMapping = this._buildAliasMapping(aliasDefs);
        this.init(props);
    }
    
    private BeanPropertyMap(final BeanPropertyMap src, final SettableBeanProperty newProp, final int hashIndex, final int orderedIndex) {
        this._caseInsensitive = src._caseInsensitive;
        this._hashMask = src._hashMask;
        this._size = src._size;
        this._spillCount = src._spillCount;
        this._aliasDefs = src._aliasDefs;
        this._aliasMapping = src._aliasMapping;
        this._hashArea = Arrays.copyOf(src._hashArea, src._hashArea.length);
        this._propsInOrder = Arrays.copyOf(src._propsInOrder, src._propsInOrder.length);
        this._hashArea[hashIndex] = newProp;
        this._propsInOrder[orderedIndex] = newProp;
    }
    
    private BeanPropertyMap(final BeanPropertyMap src, final SettableBeanProperty newProp, final String key, final int slot) {
        this._caseInsensitive = src._caseInsensitive;
        this._hashMask = src._hashMask;
        this._size = src._size;
        this._spillCount = src._spillCount;
        this._aliasDefs = src._aliasDefs;
        this._aliasMapping = src._aliasMapping;
        this._hashArea = Arrays.copyOf(src._hashArea, src._hashArea.length);
        final int last = src._propsInOrder.length;
        (this._propsInOrder = Arrays.copyOf(src._propsInOrder, last + 1))[last] = newProp;
        final int hashSize = this._hashMask + 1;
        int ix = slot << 1;
        if (this._hashArea[ix] != null) {
            ix = hashSize + (slot >> 1) << 1;
            if (this._hashArea[ix] != null) {
                ix = (hashSize + (hashSize >> 1) << 1) + this._spillCount;
                this._spillCount += 2;
                if (ix >= this._hashArea.length) {
                    this._hashArea = Arrays.copyOf(this._hashArea, this._hashArea.length + 4);
                }
            }
        }
        this._hashArea[ix] = key;
        this._hashArea[ix + 1] = newProp;
    }
    
    @Deprecated
    public BeanPropertyMap(final boolean caseInsensitive, final Collection<SettableBeanProperty> props) {
        this(caseInsensitive, props, Collections.emptyMap());
    }
    
    protected BeanPropertyMap(final BeanPropertyMap base, final boolean caseInsensitive) {
        this._caseInsensitive = caseInsensitive;
        this._aliasDefs = base._aliasDefs;
        this._aliasMapping = base._aliasMapping;
        this._propsInOrder = Arrays.copyOf(base._propsInOrder, base._propsInOrder.length);
        this.init(Arrays.asList(this._propsInOrder));
    }
    
    public BeanPropertyMap withCaseInsensitivity(final boolean state) {
        if (this._caseInsensitive == state) {
            return this;
        }
        return new BeanPropertyMap(this, state);
    }
    
    protected void init(final Collection<SettableBeanProperty> props) {
        this._size = props.size();
        final int hashSize = findSize(this._size);
        this._hashMask = hashSize - 1;
        final int alloc = (hashSize + (hashSize >> 1)) * 2;
        Object[] hashed = new Object[alloc];
        int spillCount = 0;
        for (final SettableBeanProperty prop : props) {
            if (prop == null) {
                continue;
            }
            final String key = this.getPropertyName(prop);
            final int slot = this._hashCode(key);
            int ix = slot << 1;
            if (hashed[ix] != null) {
                ix = hashSize + (slot >> 1) << 1;
                if (hashed[ix] != null) {
                    ix = (hashSize + (hashSize >> 1) << 1) + spillCount;
                    spillCount += 2;
                    if (ix >= hashed.length) {
                        hashed = Arrays.copyOf(hashed, hashed.length + 4);
                    }
                }
            }
            hashed[ix] = key;
            hashed[ix + 1] = prop;
        }
        this._hashArea = hashed;
        this._spillCount = spillCount;
    }
    
    private static final int findSize(final int size) {
        if (size <= 5) {
            return 8;
        }
        if (size <= 12) {
            return 16;
        }
        int needed;
        int result;
        for (needed = size + (size >> 2), result = 32; result < needed; result += result) {}
        return result;
    }
    
    public static BeanPropertyMap construct(final Collection<SettableBeanProperty> props, final boolean caseInsensitive, final Map<String, List<PropertyName>> aliasMapping) {
        return new BeanPropertyMap(caseInsensitive, props, aliasMapping);
    }
    
    @Deprecated
    public static BeanPropertyMap construct(final Collection<SettableBeanProperty> props, final boolean caseInsensitive) {
        return construct(props, caseInsensitive, Collections.emptyMap());
    }
    
    public BeanPropertyMap withProperty(final SettableBeanProperty newProp) {
        final String key = this.getPropertyName(newProp);
        for (int i = 1, end = this._hashArea.length; i < end; i += 2) {
            final SettableBeanProperty prop = (SettableBeanProperty)this._hashArea[i];
            if (prop != null && prop.getName().equals(key)) {
                return new BeanPropertyMap(this, newProp, i, this._findFromOrdered(prop));
            }
        }
        final int slot = this._hashCode(key);
        return new BeanPropertyMap(this, newProp, key, slot);
    }
    
    public BeanPropertyMap assignIndexes() {
        int index = 0;
        for (int i = 1, end = this._hashArea.length; i < end; i += 2) {
            final SettableBeanProperty prop = (SettableBeanProperty)this._hashArea[i];
            if (prop != null) {
                prop.assignIndex(index++);
            }
        }
        return this;
    }
    
    public BeanPropertyMap renameAll(final NameTransformer transformer) {
        if (transformer == null || transformer == NameTransformer.NOP) {
            return this;
        }
        final int len = this._propsInOrder.length;
        final ArrayList<SettableBeanProperty> newProps = new ArrayList<SettableBeanProperty>(len);
        for (int i = 0; i < len; ++i) {
            final SettableBeanProperty prop = this._propsInOrder[i];
            if (prop == null) {
                newProps.add(prop);
            }
            else {
                newProps.add(this._rename(prop, transformer));
            }
        }
        return new BeanPropertyMap(this._caseInsensitive, newProps, this._aliasDefs);
    }
    
    public BeanPropertyMap withoutProperties(final Collection<String> toExclude) {
        if (toExclude.isEmpty()) {
            return this;
        }
        final int len = this._propsInOrder.length;
        final ArrayList<SettableBeanProperty> newProps = new ArrayList<SettableBeanProperty>(len);
        for (int i = 0; i < len; ++i) {
            final SettableBeanProperty prop = this._propsInOrder[i];
            if (prop != null && !toExclude.contains(prop.getName())) {
                newProps.add(prop);
            }
        }
        return new BeanPropertyMap(this._caseInsensitive, newProps, this._aliasDefs);
    }
    
    @Deprecated
    public void replace(final SettableBeanProperty newProp) {
        final String key = this.getPropertyName(newProp);
        final int ix = this._findIndexInHash(key);
        if (ix < 0) {
            throw new NoSuchElementException("No entry '" + key + "' found, can't replace");
        }
        final SettableBeanProperty prop = (SettableBeanProperty)this._hashArea[ix];
        this._hashArea[ix] = newProp;
        this._propsInOrder[this._findFromOrdered(prop)] = newProp;
    }
    
    public void replace(final SettableBeanProperty origProp, final SettableBeanProperty newProp) {
        for (int i = 1, end = this._hashArea.length; i <= end; i += 2) {
            if (this._hashArea[i] == origProp) {
                this._hashArea[i] = newProp;
                this._propsInOrder[this._findFromOrdered(origProp)] = newProp;
                return;
            }
        }
        throw new NoSuchElementException("No entry '" + origProp.getName() + "' found, can't replace");
    }
    
    public void remove(final SettableBeanProperty propToRm) {
        final ArrayList<SettableBeanProperty> props = new ArrayList<SettableBeanProperty>(this._size);
        final String key = this.getPropertyName(propToRm);
        boolean found = false;
        for (int i = 1, end = this._hashArea.length; i < end; i += 2) {
            final SettableBeanProperty prop = (SettableBeanProperty)this._hashArea[i];
            if (prop != null) {
                if (!found) {
                    found = key.equals(this._hashArea[i - 1]);
                    if (found) {
                        this._propsInOrder[this._findFromOrdered(prop)] = null;
                        continue;
                    }
                }
                props.add(prop);
            }
        }
        if (!found) {
            throw new NoSuchElementException("No entry '" + propToRm.getName() + "' found, can't remove");
        }
        this.init(props);
    }
    
    public int size() {
        return this._size;
    }
    
    public boolean isCaseInsensitive() {
        return this._caseInsensitive;
    }
    
    public boolean hasAliases() {
        return !this._aliasDefs.isEmpty();
    }
    
    @Override
    public Iterator<SettableBeanProperty> iterator() {
        return this._properties().iterator();
    }
    
    private List<SettableBeanProperty> _properties() {
        final ArrayList<SettableBeanProperty> p = new ArrayList<SettableBeanProperty>(this._size);
        for (int i = 1, end = this._hashArea.length; i < end; i += 2) {
            final SettableBeanProperty prop = (SettableBeanProperty)this._hashArea[i];
            if (prop != null) {
                p.add(prop);
            }
        }
        return p;
    }
    
    public SettableBeanProperty[] getPropertiesInInsertionOrder() {
        return this._propsInOrder;
    }
    
    protected final String getPropertyName(final SettableBeanProperty prop) {
        return this._caseInsensitive ? prop.getName().toLowerCase() : prop.getName();
    }
    
    public SettableBeanProperty find(final int index) {
        for (int i = 1, end = this._hashArea.length; i < end; i += 2) {
            final SettableBeanProperty prop = (SettableBeanProperty)this._hashArea[i];
            if (prop != null && index == prop.getPropertyIndex()) {
                return prop;
            }
        }
        return null;
    }
    
    public SettableBeanProperty find(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Cannot pass null property name");
        }
        if (this._caseInsensitive) {
            key = key.toLowerCase();
        }
        final int slot = key.hashCode() & this._hashMask;
        final int ix = slot << 1;
        final Object match = this._hashArea[ix];
        if (match == key || key.equals(match)) {
            return (SettableBeanProperty)this._hashArea[ix + 1];
        }
        return this._find2(key, slot, match);
    }
    
    private final SettableBeanProperty _find2(final String key, final int slot, Object match) {
        if (match == null) {
            return this._findWithAlias(this._aliasMapping.get(key));
        }
        final int hashSize = this._hashMask + 1;
        final int ix = hashSize + (slot >> 1) << 1;
        match = this._hashArea[ix];
        if (key.equals(match)) {
            return (SettableBeanProperty)this._hashArea[ix + 1];
        }
        if (match != null) {
            for (int i = hashSize + (hashSize >> 1) << 1, end = i + this._spillCount; i < end; i += 2) {
                match = this._hashArea[i];
                if (match == key || key.equals(match)) {
                    return (SettableBeanProperty)this._hashArea[i + 1];
                }
            }
        }
        return this._findWithAlias(this._aliasMapping.get(key));
    }
    
    private SettableBeanProperty _findWithAlias(final String keyFromAlias) {
        if (keyFromAlias == null) {
            return null;
        }
        final int slot = this._hashCode(keyFromAlias);
        final int ix = slot << 1;
        final Object match = this._hashArea[ix];
        if (keyFromAlias.equals(match)) {
            return (SettableBeanProperty)this._hashArea[ix + 1];
        }
        if (match == null) {
            return null;
        }
        return this._find2ViaAlias(keyFromAlias, slot, match);
    }
    
    private SettableBeanProperty _find2ViaAlias(final String key, final int slot, Object match) {
        final int hashSize = this._hashMask + 1;
        final int ix = hashSize + (slot >> 1) << 1;
        match = this._hashArea[ix];
        if (key.equals(match)) {
            return (SettableBeanProperty)this._hashArea[ix + 1];
        }
        if (match != null) {
            for (int i = hashSize + (hashSize >> 1) << 1, end = i + this._spillCount; i < end; i += 2) {
                match = this._hashArea[i];
                if (match == key || key.equals(match)) {
                    return (SettableBeanProperty)this._hashArea[i + 1];
                }
            }
        }
        return null;
    }
    
    public boolean findDeserializeAndSet(final JsonParser p, final DeserializationContext ctxt, final Object bean, final String key) throws IOException {
        final SettableBeanProperty prop = this.find(key);
        if (prop == null) {
            return false;
        }
        try {
            prop.deserializeAndSet(p, ctxt, bean);
        }
        catch (Exception e) {
            this.wrapAndThrow(e, bean, key, ctxt);
        }
        return true;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Properties=[");
        int count = 0;
        for (final SettableBeanProperty prop : this) {
            if (count++ > 0) {
                sb.append(", ");
            }
            sb.append(prop.getName());
            sb.append('(');
            sb.append(prop.getType());
            sb.append(')');
        }
        sb.append(']');
        if (!this._aliasDefs.isEmpty()) {
            sb.append("(aliases: ");
            sb.append(this._aliasDefs);
            sb.append(")");
        }
        return sb.toString();
    }
    
    protected SettableBeanProperty _rename(SettableBeanProperty prop, final NameTransformer xf) {
        if (prop == null) {
            return prop;
        }
        final String newName = xf.transform(prop.getName());
        prop = prop.withSimpleName(newName);
        final JsonDeserializer<?> deser = prop.getValueDeserializer();
        if (deser != null) {
            final JsonDeserializer<Object> newDeser = (JsonDeserializer<Object>)deser.unwrappingDeserializer(xf);
            if (newDeser != deser) {
                prop = prop.withValueDeserializer(newDeser);
            }
        }
        return prop;
    }
    
    protected void wrapAndThrow(Throwable t, final Object bean, final String fieldName, final DeserializationContext ctxt) throws IOException {
        while (t instanceof InvocationTargetException && t.getCause() != null) {
            t = t.getCause();
        }
        ClassUtil.throwIfError(t);
        final boolean wrap = ctxt == null || ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS);
        if (t instanceof IOException) {
            if (!wrap || !(t instanceof JsonProcessingException)) {
                throw (IOException)t;
            }
        }
        else if (!wrap) {
            ClassUtil.throwIfRTE(t);
        }
        throw JsonMappingException.wrapWithPath(t, bean, fieldName);
    }
    
    private final int _findIndexInHash(final String key) {
        final int slot = this._hashCode(key);
        int ix = slot << 1;
        if (key.equals(this._hashArea[ix])) {
            return ix + 1;
        }
        final int hashSize = this._hashMask + 1;
        ix = hashSize + (slot >> 1) << 1;
        if (key.equals(this._hashArea[ix])) {
            return ix + 1;
        }
        for (int i = hashSize + (hashSize >> 1) << 1, end = i + this._spillCount; i < end; i += 2) {
            if (key.equals(this._hashArea[i])) {
                return i + 1;
            }
        }
        return -1;
    }
    
    private final int _findFromOrdered(final SettableBeanProperty prop) {
        for (int i = 0, end = this._propsInOrder.length; i < end; ++i) {
            if (this._propsInOrder[i] == prop) {
                return i;
            }
        }
        throw new IllegalStateException("Illegal state: property '" + prop.getName() + "' missing from _propsInOrder");
    }
    
    private final int _hashCode(final String key) {
        return key.hashCode() & this._hashMask;
    }
    
    private Map<String, String> _buildAliasMapping(final Map<String, List<PropertyName>> defs) {
        if (defs == null || defs.isEmpty()) {
            return Collections.emptyMap();
        }
        final Map<String, String> aliases = new HashMap<String, String>();
        for (final Map.Entry<String, List<PropertyName>> entry : defs.entrySet()) {
            String key = entry.getKey();
            if (this._caseInsensitive) {
                key = key.toLowerCase();
            }
            for (final PropertyName pn : entry.getValue()) {
                String mapped = pn.getSimpleName();
                if (this._caseInsensitive) {
                    mapped = mapped.toLowerCase();
                }
                aliases.put(mapped, key);
            }
        }
        return aliases;
    }
}
