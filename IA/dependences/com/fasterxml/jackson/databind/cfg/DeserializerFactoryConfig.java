// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializers;
import com.fasterxml.jackson.databind.util.ArrayIterator;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.fasterxml.jackson.databind.deser.KeyDeserializers;
import com.fasterxml.jackson.databind.deser.ValueInstantiators;
import com.fasterxml.jackson.databind.AbstractTypeResolver;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.Deserializers;
import java.io.Serializable;

public class DeserializerFactoryConfig implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected static final Deserializers[] NO_DESERIALIZERS;
    protected static final BeanDeserializerModifier[] NO_MODIFIERS;
    protected static final AbstractTypeResolver[] NO_ABSTRACT_TYPE_RESOLVERS;
    protected static final ValueInstantiators[] NO_VALUE_INSTANTIATORS;
    protected static final KeyDeserializers[] DEFAULT_KEY_DESERIALIZERS;
    protected final Deserializers[] _additionalDeserializers;
    protected final KeyDeserializers[] _additionalKeyDeserializers;
    protected final BeanDeserializerModifier[] _modifiers;
    protected final AbstractTypeResolver[] _abstractTypeResolvers;
    protected final ValueInstantiators[] _valueInstantiators;
    
    public DeserializerFactoryConfig() {
        this(null, null, null, null, null);
    }
    
    protected DeserializerFactoryConfig(final Deserializers[] allAdditionalDeserializers, final KeyDeserializers[] allAdditionalKeyDeserializers, final BeanDeserializerModifier[] modifiers, final AbstractTypeResolver[] atr, final ValueInstantiators[] vi) {
        this._additionalDeserializers = ((allAdditionalDeserializers == null) ? DeserializerFactoryConfig.NO_DESERIALIZERS : allAdditionalDeserializers);
        this._additionalKeyDeserializers = ((allAdditionalKeyDeserializers == null) ? DeserializerFactoryConfig.DEFAULT_KEY_DESERIALIZERS : allAdditionalKeyDeserializers);
        this._modifiers = ((modifiers == null) ? DeserializerFactoryConfig.NO_MODIFIERS : modifiers);
        this._abstractTypeResolvers = ((atr == null) ? DeserializerFactoryConfig.NO_ABSTRACT_TYPE_RESOLVERS : atr);
        this._valueInstantiators = ((vi == null) ? DeserializerFactoryConfig.NO_VALUE_INSTANTIATORS : vi);
    }
    
    public DeserializerFactoryConfig withAdditionalDeserializers(final Deserializers additional) {
        if (additional == null) {
            throw new IllegalArgumentException("Cannot pass null Deserializers");
        }
        final Deserializers[] all = ArrayBuilders.insertInListNoDup(this._additionalDeserializers, additional);
        return new DeserializerFactoryConfig(all, this._additionalKeyDeserializers, this._modifiers, this._abstractTypeResolvers, this._valueInstantiators);
    }
    
    public DeserializerFactoryConfig withAdditionalKeyDeserializers(final KeyDeserializers additional) {
        if (additional == null) {
            throw new IllegalArgumentException("Cannot pass null KeyDeserializers");
        }
        final KeyDeserializers[] all = ArrayBuilders.insertInListNoDup(this._additionalKeyDeserializers, additional);
        return new DeserializerFactoryConfig(this._additionalDeserializers, all, this._modifiers, this._abstractTypeResolvers, this._valueInstantiators);
    }
    
    public DeserializerFactoryConfig withDeserializerModifier(final BeanDeserializerModifier modifier) {
        if (modifier == null) {
            throw new IllegalArgumentException("Cannot pass null modifier");
        }
        final BeanDeserializerModifier[] all = ArrayBuilders.insertInListNoDup(this._modifiers, modifier);
        return new DeserializerFactoryConfig(this._additionalDeserializers, this._additionalKeyDeserializers, all, this._abstractTypeResolvers, this._valueInstantiators);
    }
    
    public DeserializerFactoryConfig withAbstractTypeResolver(final AbstractTypeResolver resolver) {
        if (resolver == null) {
            throw new IllegalArgumentException("Cannot pass null resolver");
        }
        final AbstractTypeResolver[] all = ArrayBuilders.insertInListNoDup(this._abstractTypeResolvers, resolver);
        return new DeserializerFactoryConfig(this._additionalDeserializers, this._additionalKeyDeserializers, this._modifiers, all, this._valueInstantiators);
    }
    
    public DeserializerFactoryConfig withValueInstantiators(final ValueInstantiators instantiators) {
        if (instantiators == null) {
            throw new IllegalArgumentException("Cannot pass null resolver");
        }
        final ValueInstantiators[] all = ArrayBuilders.insertInListNoDup(this._valueInstantiators, instantiators);
        return new DeserializerFactoryConfig(this._additionalDeserializers, this._additionalKeyDeserializers, this._modifiers, this._abstractTypeResolvers, all);
    }
    
    public boolean hasDeserializers() {
        return this._additionalDeserializers.length > 0;
    }
    
    public boolean hasKeyDeserializers() {
        return this._additionalKeyDeserializers.length > 0;
    }
    
    public boolean hasDeserializerModifiers() {
        return this._modifiers.length > 0;
    }
    
    public boolean hasAbstractTypeResolvers() {
        return this._abstractTypeResolvers.length > 0;
    }
    
    public boolean hasValueInstantiators() {
        return this._valueInstantiators.length > 0;
    }
    
    public Iterable<Deserializers> deserializers() {
        return new ArrayIterator<Deserializers>(this._additionalDeserializers);
    }
    
    public Iterable<KeyDeserializers> keyDeserializers() {
        return new ArrayIterator<KeyDeserializers>(this._additionalKeyDeserializers);
    }
    
    public Iterable<BeanDeserializerModifier> deserializerModifiers() {
        return new ArrayIterator<BeanDeserializerModifier>(this._modifiers);
    }
    
    public Iterable<AbstractTypeResolver> abstractTypeResolvers() {
        return new ArrayIterator<AbstractTypeResolver>(this._abstractTypeResolvers);
    }
    
    public Iterable<ValueInstantiators> valueInstantiators() {
        return new ArrayIterator<ValueInstantiators>(this._valueInstantiators);
    }
    
    static {
        NO_DESERIALIZERS = new Deserializers[0];
        NO_MODIFIERS = new BeanDeserializerModifier[0];
        NO_ABSTRACT_TYPE_RESOLVERS = new AbstractTypeResolver[0];
        NO_VALUE_INSTANTIATORS = new ValueInstantiators[0];
        DEFAULT_KEY_DESERIALIZERS = new KeyDeserializers[] { new StdKeyDeserializers() };
    }
}
