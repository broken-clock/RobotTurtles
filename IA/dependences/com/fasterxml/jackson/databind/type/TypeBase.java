// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.core.type.ResolvedType;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.JavaType;

public abstract class TypeBase extends JavaType implements JsonSerializable
{
    private static final long serialVersionUID = 1L;
    private static final TypeBindings NO_BINDINGS;
    private static final JavaType[] NO_TYPES;
    protected final JavaType _superClass;
    protected final JavaType[] _superInterfaces;
    protected final TypeBindings _bindings;
    transient volatile String _canonicalName;
    
    protected TypeBase(final Class<?> raw, final TypeBindings bindings, final JavaType superClass, final JavaType[] superInts, final int hash, final Object valueHandler, final Object typeHandler, final boolean asStatic) {
        super(raw, hash, valueHandler, typeHandler, asStatic);
        this._bindings = ((bindings == null) ? TypeBase.NO_BINDINGS : bindings);
        this._superClass = superClass;
        this._superInterfaces = superInts;
    }
    
    protected TypeBase(final TypeBase base) {
        super(base);
        this._superClass = base._superClass;
        this._superInterfaces = base._superInterfaces;
        this._bindings = base._bindings;
    }
    
    @Override
    public String toCanonical() {
        String str = this._canonicalName;
        if (str == null) {
            str = this.buildCanonicalName();
        }
        return str;
    }
    
    protected String buildCanonicalName() {
        return this._class.getName();
    }
    
    @Override
    public abstract StringBuilder getGenericSignature(final StringBuilder p0);
    
    @Override
    public abstract StringBuilder getErasedSignature(final StringBuilder p0);
    
    @Override
    public TypeBindings getBindings() {
        return this._bindings;
    }
    
    @Override
    public int containedTypeCount() {
        return this._bindings.size();
    }
    
    @Override
    public JavaType containedType(final int index) {
        return this._bindings.getBoundType(index);
    }
    
    @Deprecated
    @Override
    public String containedTypeName(final int index) {
        return this._bindings.getBoundName(index);
    }
    
    @Override
    public JavaType getSuperClass() {
        return this._superClass;
    }
    
    @Override
    public List<JavaType> getInterfaces() {
        if (this._superInterfaces == null) {
            return Collections.emptyList();
        }
        switch (this._superInterfaces.length) {
            case 0: {
                return Collections.emptyList();
            }
            case 1: {
                return Collections.singletonList(this._superInterfaces[0]);
            }
            default: {
                return Arrays.asList(this._superInterfaces);
            }
        }
    }
    
    @Override
    public final JavaType findSuperType(final Class<?> rawTarget) {
        if (rawTarget == this._class) {
            return this;
        }
        if (rawTarget.isInterface() && this._superInterfaces != null) {
            for (int i = 0, count = this._superInterfaces.length; i < count; ++i) {
                final JavaType type = this._superInterfaces[i].findSuperType(rawTarget);
                if (type != null) {
                    return type;
                }
            }
        }
        if (this._superClass != null) {
            final JavaType type2 = this._superClass.findSuperType(rawTarget);
            if (type2 != null) {
                return type2;
            }
        }
        return null;
    }
    
    @Override
    public JavaType[] findTypeParameters(final Class<?> expType) {
        final JavaType match = this.findSuperType(expType);
        if (match == null) {
            return TypeBase.NO_TYPES;
        }
        return match.getBindings().typeParameterArray();
    }
    
    @Override
    public void serializeWithType(final JsonGenerator g, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
        final WritableTypeId typeIdDef = new WritableTypeId(this, JsonToken.VALUE_STRING);
        typeSer.writeTypePrefix(g, typeIdDef);
        this.serialize(g, provider);
        typeSer.writeTypeSuffix(g, typeIdDef);
    }
    
    @Override
    public void serialize(final JsonGenerator gen, final SerializerProvider provider) throws IOException, JsonProcessingException {
        gen.writeString(this.toCanonical());
    }
    
    protected static StringBuilder _classSignature(final Class<?> cls, final StringBuilder sb, final boolean trailingSemicolon) {
        if (cls.isPrimitive()) {
            if (cls == Boolean.TYPE) {
                sb.append('Z');
            }
            else if (cls == Byte.TYPE) {
                sb.append('B');
            }
            else if (cls == Short.TYPE) {
                sb.append('S');
            }
            else if (cls == Character.TYPE) {
                sb.append('C');
            }
            else if (cls == Integer.TYPE) {
                sb.append('I');
            }
            else if (cls == Long.TYPE) {
                sb.append('J');
            }
            else if (cls == Float.TYPE) {
                sb.append('F');
            }
            else if (cls == Double.TYPE) {
                sb.append('D');
            }
            else {
                if (cls != Void.TYPE) {
                    throw new IllegalStateException("Unrecognized primitive type: " + cls.getName());
                }
                sb.append('V');
            }
        }
        else {
            sb.append('L');
            final String name = cls.getName();
            for (int i = 0, len = name.length(); i < len; ++i) {
                char c = name.charAt(i);
                if (c == '.') {
                    c = '/';
                }
                sb.append(c);
            }
            if (trailingSemicolon) {
                sb.append(';');
            }
        }
        return sb;
    }
    
    protected static JavaType _bogusSuperClass(final Class<?> cls) {
        final Class<?> parent = cls.getSuperclass();
        if (parent == null) {
            return null;
        }
        return TypeFactory.unknownType();
    }
    
    static {
        NO_BINDINGS = TypeBindings.emptyBindings();
        NO_TYPES = new JavaType[0];
    }
}
