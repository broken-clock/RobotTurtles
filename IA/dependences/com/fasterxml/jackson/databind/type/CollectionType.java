// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.type;

import java.lang.reflect.TypeVariable;
import com.fasterxml.jackson.databind.JavaType;

public final class CollectionType extends CollectionLikeType
{
    private static final long serialVersionUID = 1L;
    
    private CollectionType(final Class<?> collT, final TypeBindings bindings, final JavaType superClass, final JavaType[] superInts, final JavaType elemT, final Object valueHandler, final Object typeHandler, final boolean asStatic) {
        super(collT, bindings, superClass, superInts, elemT, valueHandler, typeHandler, asStatic);
    }
    
    protected CollectionType(final TypeBase base, final JavaType elemT) {
        super(base, elemT);
    }
    
    public static CollectionType construct(final Class<?> rawType, final TypeBindings bindings, final JavaType superClass, final JavaType[] superInts, final JavaType elemT) {
        return new CollectionType(rawType, bindings, superClass, superInts, elemT, null, null, false);
    }
    
    @Deprecated
    public static CollectionType construct(final Class<?> rawType, final JavaType elemT) {
        final TypeVariable<?>[] vars = rawType.getTypeParameters();
        TypeBindings bindings;
        if (vars == null || vars.length != 1) {
            bindings = TypeBindings.emptyBindings();
        }
        else {
            bindings = TypeBindings.create(rawType, elemT);
        }
        return new CollectionType(rawType, bindings, TypeBase._bogusSuperClass(rawType), null, elemT, null, null, false);
    }
    
    @Deprecated
    @Override
    protected JavaType _narrow(final Class<?> subclass) {
        return new CollectionType(subclass, this._bindings, this._superClass, this._superInterfaces, this._elementType, null, null, this._asStatic);
    }
    
    @Override
    public JavaType withContentType(final JavaType contentType) {
        if (this._elementType == contentType) {
            return this;
        }
        return new CollectionType(this._class, this._bindings, this._superClass, this._superInterfaces, contentType, this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public CollectionType withTypeHandler(final Object h) {
        return new CollectionType(this._class, this._bindings, this._superClass, this._superInterfaces, this._elementType, this._valueHandler, h, this._asStatic);
    }
    
    @Override
    public CollectionType withContentTypeHandler(final Object h) {
        return new CollectionType(this._class, this._bindings, this._superClass, this._superInterfaces, this._elementType.withTypeHandler(h), this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public CollectionType withValueHandler(final Object h) {
        return new CollectionType(this._class, this._bindings, this._superClass, this._superInterfaces, this._elementType, h, this._typeHandler, this._asStatic);
    }
    
    @Override
    public CollectionType withContentValueHandler(final Object h) {
        return new CollectionType(this._class, this._bindings, this._superClass, this._superInterfaces, this._elementType.withValueHandler(h), this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public CollectionType withStaticTyping() {
        if (this._asStatic) {
            return this;
        }
        return new CollectionType(this._class, this._bindings, this._superClass, this._superInterfaces, this._elementType.withStaticTyping(), this._valueHandler, this._typeHandler, true);
    }
    
    @Override
    public JavaType refine(final Class<?> rawType, final TypeBindings bindings, final JavaType superClass, final JavaType[] superInterfaces) {
        return new CollectionType(rawType, bindings, superClass, superInterfaces, this._elementType, this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public String toString() {
        return "[collection type; class " + this._class.getName() + ", contains " + this._elementType + "]";
    }
}
