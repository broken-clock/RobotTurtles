// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.databind.util.ArrayIterator;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.Serializers;
import java.io.Serializable;

public final class SerializerFactoryConfig implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected static final Serializers[] NO_SERIALIZERS;
    protected static final BeanSerializerModifier[] NO_MODIFIERS;
    protected final Serializers[] _additionalSerializers;
    protected final Serializers[] _additionalKeySerializers;
    protected final BeanSerializerModifier[] _modifiers;
    
    public SerializerFactoryConfig() {
        this(null, null, null);
    }
    
    protected SerializerFactoryConfig(final Serializers[] allAdditionalSerializers, final Serializers[] allAdditionalKeySerializers, final BeanSerializerModifier[] modifiers) {
        this._additionalSerializers = ((allAdditionalSerializers == null) ? SerializerFactoryConfig.NO_SERIALIZERS : allAdditionalSerializers);
        this._additionalKeySerializers = ((allAdditionalKeySerializers == null) ? SerializerFactoryConfig.NO_SERIALIZERS : allAdditionalKeySerializers);
        this._modifiers = ((modifiers == null) ? SerializerFactoryConfig.NO_MODIFIERS : modifiers);
    }
    
    public SerializerFactoryConfig withAdditionalSerializers(final Serializers additional) {
        if (additional == null) {
            throw new IllegalArgumentException("Cannot pass null Serializers");
        }
        final Serializers[] all = ArrayBuilders.insertInListNoDup(this._additionalSerializers, additional);
        return new SerializerFactoryConfig(all, this._additionalKeySerializers, this._modifiers);
    }
    
    public SerializerFactoryConfig withAdditionalKeySerializers(final Serializers additional) {
        if (additional == null) {
            throw new IllegalArgumentException("Cannot pass null Serializers");
        }
        final Serializers[] all = ArrayBuilders.insertInListNoDup(this._additionalKeySerializers, additional);
        return new SerializerFactoryConfig(this._additionalSerializers, all, this._modifiers);
    }
    
    public SerializerFactoryConfig withSerializerModifier(final BeanSerializerModifier modifier) {
        if (modifier == null) {
            throw new IllegalArgumentException("Cannot pass null modifier");
        }
        final BeanSerializerModifier[] modifiers = ArrayBuilders.insertInListNoDup(this._modifiers, modifier);
        return new SerializerFactoryConfig(this._additionalSerializers, this._additionalKeySerializers, modifiers);
    }
    
    public boolean hasSerializers() {
        return this._additionalSerializers.length > 0;
    }
    
    public boolean hasKeySerializers() {
        return this._additionalKeySerializers.length > 0;
    }
    
    public boolean hasSerializerModifiers() {
        return this._modifiers.length > 0;
    }
    
    public Iterable<Serializers> serializers() {
        return new ArrayIterator<Serializers>(this._additionalSerializers);
    }
    
    public Iterable<Serializers> keySerializers() {
        return new ArrayIterator<Serializers>(this._additionalKeySerializers);
    }
    
    public Iterable<BeanSerializerModifier> serializerModifiers() {
        return new ArrayIterator<BeanSerializerModifier>(this._modifiers);
    }
    
    static {
        NO_SERIALIZERS = new Serializers[0];
        NO_MODIFIERS = new BeanSerializerModifier[0];
    }
}
