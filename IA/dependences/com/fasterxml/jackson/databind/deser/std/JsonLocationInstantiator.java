// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.util.Annotations;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.CreatorProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;

public class JsonLocationInstantiator extends Base
{
    public JsonLocationInstantiator() {
        super(JsonLocation.class);
    }
    
    @Override
    public boolean canCreateFromObjectWith() {
        return true;
    }
    
    @Override
    public SettableBeanProperty[] getFromObjectArguments(final DeserializationConfig config) {
        final JavaType intType = config.constructType(Integer.TYPE);
        final JavaType longType = config.constructType(Long.TYPE);
        return new SettableBeanProperty[] { creatorProp("sourceRef", config.constructType(Object.class), 0), creatorProp("byteOffset", longType, 1), creatorProp("charOffset", longType, 2), creatorProp("lineNr", intType, 3), creatorProp("columnNr", intType, 4) };
    }
    
    private static CreatorProperty creatorProp(final String name, final JavaType type, final int index) {
        return new CreatorProperty(PropertyName.construct(name), type, null, null, null, null, index, null, PropertyMetadata.STD_REQUIRED);
    }
    
    @Override
    public Object createFromObjectWith(final DeserializationContext ctxt, final Object[] args) {
        return new JsonLocation(args[0], _long(args[1]), _long(args[2]), _int(args[3]), _int(args[4]));
    }
    
    private static final long _long(final Object o) {
        return (o == null) ? 0L : ((Number)o).longValue();
    }
    
    private static final int _int(final Object o) {
        return (o == null) ? 0 : ((Number)o).intValue();
    }
}
