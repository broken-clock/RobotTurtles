// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.type;

import java.util.concurrent.atomic.AtomicReference;
import java.lang.reflect.WildcardType;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.util.Properties;
import java.util.Collection;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Map;
import java.util.EnumSet;
import java.util.TreeSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.HashMap;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.reflect.Type;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.fasterxml.jackson.databind.util.LRUMap;
import com.fasterxml.jackson.databind.JavaType;
import java.io.Serializable;

public final class TypeFactory implements Serializable
{
    private static final long serialVersionUID = 1L;
    private static final JavaType[] NO_TYPES;
    protected static final TypeFactory instance;
    protected static final TypeBindings EMPTY_BINDINGS;
    private static final Class<?> CLS_STRING;
    private static final Class<?> CLS_OBJECT;
    private static final Class<?> CLS_COMPARABLE;
    private static final Class<?> CLS_CLASS;
    private static final Class<?> CLS_ENUM;
    private static final Class<?> CLS_BOOL;
    private static final Class<?> CLS_INT;
    private static final Class<?> CLS_LONG;
    protected static final SimpleType CORE_TYPE_BOOL;
    protected static final SimpleType CORE_TYPE_INT;
    protected static final SimpleType CORE_TYPE_LONG;
    protected static final SimpleType CORE_TYPE_STRING;
    protected static final SimpleType CORE_TYPE_OBJECT;
    protected static final SimpleType CORE_TYPE_COMPARABLE;
    protected static final SimpleType CORE_TYPE_ENUM;
    protected static final SimpleType CORE_TYPE_CLASS;
    protected final LRUMap<Object, JavaType> _typeCache;
    protected final TypeModifier[] _modifiers;
    protected final TypeParser _parser;
    protected final ClassLoader _classLoader;
    
    private TypeFactory() {
        this(null);
    }
    
    protected TypeFactory(LRUMap<Object, JavaType> typeCache) {
        if (typeCache == null) {
            typeCache = new LRUMap<Object, JavaType>(16, 200);
        }
        this._typeCache = typeCache;
        this._parser = new TypeParser(this);
        this._modifiers = null;
        this._classLoader = null;
    }
    
    protected TypeFactory(LRUMap<Object, JavaType> typeCache, final TypeParser p, final TypeModifier[] mods, final ClassLoader classLoader) {
        if (typeCache == null) {
            typeCache = new LRUMap<Object, JavaType>(16, 200);
        }
        this._typeCache = typeCache;
        this._parser = p.withFactory(this);
        this._modifiers = mods;
        this._classLoader = classLoader;
    }
    
    public TypeFactory withModifier(final TypeModifier mod) {
        LRUMap<Object, JavaType> typeCache = this._typeCache;
        TypeModifier[] mods;
        if (mod == null) {
            mods = null;
            typeCache = null;
        }
        else if (this._modifiers == null) {
            mods = new TypeModifier[] { mod };
        }
        else {
            mods = ArrayBuilders.insertInListNoDup(this._modifiers, mod);
        }
        return new TypeFactory(typeCache, this._parser, mods, this._classLoader);
    }
    
    public TypeFactory withClassLoader(final ClassLoader classLoader) {
        return new TypeFactory(this._typeCache, this._parser, this._modifiers, classLoader);
    }
    
    public TypeFactory withCache(final LRUMap<Object, JavaType> cache) {
        return new TypeFactory(cache, this._parser, this._modifiers, this._classLoader);
    }
    
    public static TypeFactory defaultInstance() {
        return TypeFactory.instance;
    }
    
    public void clearCache() {
        this._typeCache.clear();
    }
    
    public ClassLoader getClassLoader() {
        return this._classLoader;
    }
    
    public static JavaType unknownType() {
        return defaultInstance()._unknownType();
    }
    
    public static Class<?> rawClass(final Type t) {
        if (t instanceof Class) {
            return (Class<?>)t;
        }
        return defaultInstance().constructType(t).getRawClass();
    }
    
    public Class<?> findClass(final String className) throws ClassNotFoundException {
        if (className.indexOf(46) < 0) {
            final Class<?> prim = this._findPrimitive(className);
            if (prim != null) {
                return prim;
            }
        }
        Throwable prob = null;
        ClassLoader loader = this.getClassLoader();
        if (loader == null) {
            loader = Thread.currentThread().getContextClassLoader();
        }
        if (loader != null) {
            try {
                return this.classForName(className, true, loader);
            }
            catch (Exception e) {
                prob = ClassUtil.getRootCause(e);
            }
        }
        try {
            return this.classForName(className);
        }
        catch (Exception e) {
            if (prob == null) {
                prob = ClassUtil.getRootCause(e);
            }
            ClassUtil.throwIfRTE(prob);
            throw new ClassNotFoundException(prob.getMessage(), prob);
        }
    }
    
    protected Class<?> classForName(final String name, final boolean initialize, final ClassLoader loader) throws ClassNotFoundException {
        return Class.forName(name, true, loader);
    }
    
    protected Class<?> classForName(final String name) throws ClassNotFoundException {
        return Class.forName(name);
    }
    
    protected Class<?> _findPrimitive(final String className) {
        if ("int".equals(className)) {
            return Integer.TYPE;
        }
        if ("long".equals(className)) {
            return Long.TYPE;
        }
        if ("float".equals(className)) {
            return Float.TYPE;
        }
        if ("double".equals(className)) {
            return Double.TYPE;
        }
        if ("boolean".equals(className)) {
            return Boolean.TYPE;
        }
        if ("byte".equals(className)) {
            return Byte.TYPE;
        }
        if ("char".equals(className)) {
            return Character.TYPE;
        }
        if ("short".equals(className)) {
            return Short.TYPE;
        }
        if ("void".equals(className)) {
            return Void.TYPE;
        }
        return null;
    }
    
    public JavaType constructSpecializedType(final JavaType baseType, final Class<?> subclass) {
        final Class<?> rawBase = baseType.getRawClass();
        if (rawBase == subclass) {
            return baseType;
        }
        JavaType newType;
        if (rawBase == Object.class) {
            newType = this._fromClass(null, subclass, TypeFactory.EMPTY_BINDINGS);
        }
        else {
            if (!rawBase.isAssignableFrom(subclass)) {
                throw new IllegalArgumentException(String.format("Class %s not subtype of %s", subclass.getName(), baseType));
            }
            if (baseType.getBindings().isEmpty()) {
                newType = this._fromClass(null, subclass, TypeFactory.EMPTY_BINDINGS);
            }
            else {
                if (baseType.isContainerType()) {
                    if (baseType.isMapLikeType()) {
                        if (subclass == HashMap.class || subclass == LinkedHashMap.class || subclass == EnumMap.class || subclass == TreeMap.class) {
                            newType = this._fromClass(null, subclass, TypeBindings.create(subclass, baseType.getKeyType(), baseType.getContentType()));
                            return newType.withHandlersFrom(baseType);
                        }
                    }
                    else if (baseType.isCollectionLikeType()) {
                        if (subclass == ArrayList.class || subclass == LinkedList.class || subclass == HashSet.class || subclass == TreeSet.class) {
                            newType = this._fromClass(null, subclass, TypeBindings.create(subclass, baseType.getContentType()));
                            return newType.withHandlersFrom(baseType);
                        }
                        if (rawBase == EnumSet.class) {
                            return baseType;
                        }
                    }
                }
                final int typeParamCount = subclass.getTypeParameters().length;
                if (typeParamCount == 0) {
                    newType = this._fromClass(null, subclass, TypeFactory.EMPTY_BINDINGS);
                }
                else {
                    final TypeBindings tb = this._bindingsForSubtype(baseType, typeParamCount, subclass);
                    newType = this._fromClass(null, subclass, tb);
                }
            }
        }
        newType = newType.withHandlersFrom(baseType);
        return newType;
    }
    
    private TypeBindings _bindingsForSubtype(final JavaType baseType, final int typeParamCount, final Class<?> subclass) {
        final PlaceholderForType[] placeholders = new PlaceholderForType[typeParamCount];
        for (int i = 0; i < typeParamCount; ++i) {
            placeholders[i] = new PlaceholderForType(i);
        }
        final TypeBindings b = TypeBindings.create(subclass, placeholders);
        final JavaType tmpSub = this._fromClass(null, subclass, b);
        final JavaType baseWithPlaceholders = tmpSub.findSuperType(baseType.getRawClass());
        if (baseWithPlaceholders == null) {
            throw new IllegalArgumentException(String.format("Internal error: unable to locate supertype (%s) from resolved subtype %s", baseType.getRawClass().getName(), subclass.getName()));
        }
        final String error = this._resolveTypePlaceholders(baseType, baseWithPlaceholders);
        if (error != null) {
            throw new IllegalArgumentException("Failed to specialize base type " + baseType.toCanonical() + " as " + subclass.getName() + ", problem: " + error);
        }
        final JavaType[] typeParams = new JavaType[typeParamCount];
        for (int j = 0; j < typeParamCount; ++j) {
            JavaType t = placeholders[j].actualType();
            if (t == null) {
                t = unknownType();
            }
            typeParams[j] = t;
        }
        return TypeBindings.create(subclass, typeParams);
    }
    
    private String _resolveTypePlaceholders(final JavaType sourceType, final JavaType actualType) throws IllegalArgumentException {
        final List<JavaType> expectedTypes = sourceType.getBindings().getTypeParameters();
        final List<JavaType> actualTypes = actualType.getBindings().getTypeParameters();
        for (int i = 0, len = expectedTypes.size(); i < len; ++i) {
            final JavaType exp = expectedTypes.get(i);
            final JavaType act = actualTypes.get(i);
            if (!this._verifyAndResolvePlaceholders(exp, act)) {
                if (!exp.hasRawClass(Object.class)) {
                    if (i != 0 || !sourceType.hasRawClass(Map.class) || !act.hasRawClass(Object.class)) {
                        if (!exp.isInterface() || !exp.isTypeOrSuperTypeOf(act.getRawClass())) {
                            return String.format("Type parameter #%d/%d differs; can not specialize %s with %s", i + 1, len, exp.toCanonical(), act.toCanonical());
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private boolean _verifyAndResolvePlaceholders(final JavaType exp, final JavaType act) {
        if (act instanceof PlaceholderForType) {
            ((PlaceholderForType)act).actualType(exp);
            return true;
        }
        if (exp.getRawClass() != act.getRawClass()) {
            return false;
        }
        final List<JavaType> expectedTypes = exp.getBindings().getTypeParameters();
        final List<JavaType> actualTypes = act.getBindings().getTypeParameters();
        for (int i = 0, len = expectedTypes.size(); i < len; ++i) {
            final JavaType exp2 = expectedTypes.get(i);
            final JavaType act2 = actualTypes.get(i);
            if (!this._verifyAndResolvePlaceholders(exp2, act2)) {
                return false;
            }
        }
        return true;
    }
    
    public JavaType constructGeneralizedType(final JavaType baseType, final Class<?> superClass) {
        final Class<?> rawBase = baseType.getRawClass();
        if (rawBase == superClass) {
            return baseType;
        }
        final JavaType superType = baseType.findSuperType(superClass);
        if (superType != null) {
            return superType;
        }
        if (!superClass.isAssignableFrom(rawBase)) {
            throw new IllegalArgumentException(String.format("Class %s not a super-type of %s", superClass.getName(), baseType));
        }
        throw new IllegalArgumentException(String.format("Internal error: class %s not included as super-type for %s", superClass.getName(), baseType));
    }
    
    public JavaType constructFromCanonical(final String canonical) throws IllegalArgumentException {
        return this._parser.parse(canonical);
    }
    
    public JavaType[] findTypeParameters(final JavaType type, final Class<?> expType) {
        final JavaType match = type.findSuperType(expType);
        if (match == null) {
            return TypeFactory.NO_TYPES;
        }
        return match.getBindings().typeParameterArray();
    }
    
    @Deprecated
    public JavaType[] findTypeParameters(final Class<?> clz, final Class<?> expType, final TypeBindings bindings) {
        return this.findTypeParameters(this.constructType(clz, bindings), expType);
    }
    
    @Deprecated
    public JavaType[] findTypeParameters(final Class<?> clz, final Class<?> expType) {
        return this.findTypeParameters(this.constructType(clz), expType);
    }
    
    public JavaType moreSpecificType(final JavaType type1, final JavaType type2) {
        if (type1 == null) {
            return type2;
        }
        if (type2 == null) {
            return type1;
        }
        final Class<?> raw1 = type1.getRawClass();
        final Class<?> raw2 = type2.getRawClass();
        if (raw1 == raw2) {
            return type1;
        }
        if (raw1.isAssignableFrom(raw2)) {
            return type2;
        }
        return type1;
    }
    
    public JavaType constructType(final Type type) {
        return this._fromAny(null, type, TypeFactory.EMPTY_BINDINGS);
    }
    
    public JavaType constructType(final Type type, final TypeBindings bindings) {
        return this._fromAny(null, type, bindings);
    }
    
    public JavaType constructType(final TypeReference<?> typeRef) {
        return this._fromAny(null, typeRef.getType(), TypeFactory.EMPTY_BINDINGS);
    }
    
    @Deprecated
    public JavaType constructType(final Type type, final Class<?> contextClass) {
        final JavaType contextType = (contextClass == null) ? null : this.constructType(contextClass);
        return this.constructType(type, contextType);
    }
    
    @Deprecated
    public JavaType constructType(final Type type, JavaType contextType) {
        TypeBindings bindings;
        if (contextType == null) {
            bindings = TypeFactory.EMPTY_BINDINGS;
        }
        else {
            bindings = contextType.getBindings();
            if (type.getClass() != Class.class) {
                while (bindings.isEmpty()) {
                    contextType = contextType.getSuperClass();
                    if (contextType == null) {
                        break;
                    }
                    bindings = contextType.getBindings();
                }
            }
        }
        return this._fromAny(null, type, bindings);
    }
    
    public ArrayType constructArrayType(final Class<?> elementType) {
        return ArrayType.construct(this._fromAny(null, elementType, null), null);
    }
    
    public ArrayType constructArrayType(final JavaType elementType) {
        return ArrayType.construct(elementType, null);
    }
    
    public CollectionType constructCollectionType(final Class<? extends Collection> collectionClass, final Class<?> elementClass) {
        return this.constructCollectionType(collectionClass, this._fromClass(null, elementClass, TypeFactory.EMPTY_BINDINGS));
    }
    
    public CollectionType constructCollectionType(final Class<? extends Collection> collectionClass, final JavaType elementType) {
        final TypeBindings bindings = TypeBindings.createIfNeeded(collectionClass, elementType);
        final CollectionType result = (CollectionType)this._fromClass(null, collectionClass, bindings);
        if (bindings.isEmpty() && elementType != null) {
            final JavaType t = result.findSuperType(Collection.class);
            final JavaType realET = t.getContentType();
            if (!realET.equals(elementType)) {
                throw new IllegalArgumentException(String.format("Non-generic Collection class %s did not resolve to something with element type %s but %s ", ClassUtil.nameOf(collectionClass), elementType, realET));
            }
        }
        return result;
    }
    
    public CollectionLikeType constructCollectionLikeType(final Class<?> collectionClass, final Class<?> elementClass) {
        return this.constructCollectionLikeType(collectionClass, this._fromClass(null, elementClass, TypeFactory.EMPTY_BINDINGS));
    }
    
    public CollectionLikeType constructCollectionLikeType(final Class<?> collectionClass, final JavaType elementType) {
        final JavaType type = this._fromClass(null, collectionClass, TypeBindings.createIfNeeded(collectionClass, elementType));
        if (type instanceof CollectionLikeType) {
            return (CollectionLikeType)type;
        }
        return CollectionLikeType.upgradeFrom(type, elementType);
    }
    
    public MapType constructMapType(final Class<? extends Map> mapClass, final Class<?> keyClass, final Class<?> valueClass) {
        JavaType kt;
        JavaType vt;
        if (mapClass == Properties.class) {
            vt = (kt = TypeFactory.CORE_TYPE_STRING);
        }
        else {
            kt = this._fromClass(null, keyClass, TypeFactory.EMPTY_BINDINGS);
            vt = this._fromClass(null, valueClass, TypeFactory.EMPTY_BINDINGS);
        }
        return this.constructMapType(mapClass, kt, vt);
    }
    
    public MapType constructMapType(final Class<? extends Map> mapClass, final JavaType keyType, final JavaType valueType) {
        final TypeBindings bindings = TypeBindings.createIfNeeded(mapClass, new JavaType[] { keyType, valueType });
        final MapType result = (MapType)this._fromClass(null, mapClass, bindings);
        if (bindings.isEmpty()) {
            final JavaType t = result.findSuperType(Map.class);
            final JavaType realKT = t.getKeyType();
            if (!realKT.equals(keyType)) {
                throw new IllegalArgumentException(String.format("Non-generic Map class %s did not resolve to something with key type %s but %s ", ClassUtil.nameOf(mapClass), keyType, realKT));
            }
            final JavaType realVT = t.getContentType();
            if (!realVT.equals(valueType)) {
                throw new IllegalArgumentException(String.format("Non-generic Map class %s did not resolve to something with value type %s but %s ", ClassUtil.nameOf(mapClass), valueType, realVT));
            }
        }
        return result;
    }
    
    public MapLikeType constructMapLikeType(final Class<?> mapClass, final Class<?> keyClass, final Class<?> valueClass) {
        return this.constructMapLikeType(mapClass, this._fromClass(null, keyClass, TypeFactory.EMPTY_BINDINGS), this._fromClass(null, valueClass, TypeFactory.EMPTY_BINDINGS));
    }
    
    public MapLikeType constructMapLikeType(final Class<?> mapClass, final JavaType keyType, final JavaType valueType) {
        final JavaType type = this._fromClass(null, mapClass, TypeBindings.createIfNeeded(mapClass, new JavaType[] { keyType, valueType }));
        if (type instanceof MapLikeType) {
            return (MapLikeType)type;
        }
        return MapLikeType.upgradeFrom(type, keyType, valueType);
    }
    
    public JavaType constructSimpleType(final Class<?> rawType, final JavaType[] parameterTypes) {
        return this._fromClass(null, rawType, TypeBindings.create(rawType, parameterTypes));
    }
    
    @Deprecated
    public JavaType constructSimpleType(final Class<?> rawType, final Class<?> parameterTarget, final JavaType[] parameterTypes) {
        return this.constructSimpleType(rawType, parameterTypes);
    }
    
    public JavaType constructReferenceType(final Class<?> rawType, final JavaType referredType) {
        return ReferenceType.construct(rawType, null, null, null, referredType);
    }
    
    @Deprecated
    public JavaType uncheckedSimpleType(final Class<?> cls) {
        return this._constructSimple(cls, TypeFactory.EMPTY_BINDINGS, null, null);
    }
    
    public JavaType constructParametricType(final Class<?> parametrized, final Class<?>... parameterClasses) {
        final int len = parameterClasses.length;
        final JavaType[] pt = new JavaType[len];
        for (int i = 0; i < len; ++i) {
            pt[i] = this._fromClass(null, parameterClasses[i], TypeFactory.EMPTY_BINDINGS);
        }
        return this.constructParametricType(parametrized, pt);
    }
    
    public JavaType constructParametricType(final Class<?> rawType, final JavaType... parameterTypes) {
        return this._fromClass(null, rawType, TypeBindings.create(rawType, parameterTypes));
    }
    
    @Deprecated
    public JavaType constructParametrizedType(final Class<?> parametrized, final Class<?> parametersFor, final JavaType... parameterTypes) {
        return this.constructParametricType(parametrized, parameterTypes);
    }
    
    @Deprecated
    public JavaType constructParametrizedType(final Class<?> parametrized, final Class<?> parametersFor, final Class<?>... parameterClasses) {
        return this.constructParametricType(parametrized, parameterClasses);
    }
    
    public CollectionType constructRawCollectionType(final Class<? extends Collection> collectionClass) {
        return this.constructCollectionType(collectionClass, unknownType());
    }
    
    public CollectionLikeType constructRawCollectionLikeType(final Class<?> collectionClass) {
        return this.constructCollectionLikeType(collectionClass, unknownType());
    }
    
    public MapType constructRawMapType(final Class<? extends Map> mapClass) {
        return this.constructMapType(mapClass, unknownType(), unknownType());
    }
    
    public MapLikeType constructRawMapLikeType(final Class<?> mapClass) {
        return this.constructMapLikeType(mapClass, unknownType(), unknownType());
    }
    
    private JavaType _mapType(final Class<?> rawClass, final TypeBindings bindings, final JavaType superClass, final JavaType[] superInterfaces) {
        JavaType kt = null;
        JavaType vt = null;
        if (rawClass == Properties.class) {
            vt = (kt = TypeFactory.CORE_TYPE_STRING);
        }
        else {
            final List<JavaType> typeParams = bindings.getTypeParameters();
            switch (typeParams.size()) {
                case 0: {
                    vt = (kt = this._unknownType());
                    break;
                }
                case 2: {
                    kt = typeParams.get(0);
                    vt = typeParams.get(1);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Strange Map type " + rawClass.getName() + ": cannot determine type parameters");
                }
            }
        }
        return MapType.construct(rawClass, bindings, superClass, superInterfaces, kt, vt);
    }
    
    private JavaType _collectionType(final Class<?> rawClass, final TypeBindings bindings, final JavaType superClass, final JavaType[] superInterfaces) {
        final List<JavaType> typeParams = bindings.getTypeParameters();
        JavaType ct;
        if (typeParams.isEmpty()) {
            ct = this._unknownType();
        }
        else {
            if (typeParams.size() != 1) {
                throw new IllegalArgumentException("Strange Collection type " + rawClass.getName() + ": cannot determine type parameters");
            }
            ct = typeParams.get(0);
        }
        return CollectionType.construct(rawClass, bindings, superClass, superInterfaces, ct);
    }
    
    private JavaType _referenceType(final Class<?> rawClass, final TypeBindings bindings, final JavaType superClass, final JavaType[] superInterfaces) {
        final List<JavaType> typeParams = bindings.getTypeParameters();
        JavaType ct;
        if (typeParams.isEmpty()) {
            ct = this._unknownType();
        }
        else {
            if (typeParams.size() != 1) {
                throw new IllegalArgumentException("Strange Reference type " + rawClass.getName() + ": cannot determine type parameters");
            }
            ct = typeParams.get(0);
        }
        return ReferenceType.construct(rawClass, bindings, superClass, superInterfaces, ct);
    }
    
    protected JavaType _constructSimple(final Class<?> raw, final TypeBindings bindings, final JavaType superClass, final JavaType[] superInterfaces) {
        if (bindings.isEmpty()) {
            final JavaType result = this._findWellKnownSimple(raw);
            if (result != null) {
                return result;
            }
        }
        return this._newSimpleType(raw, bindings, superClass, superInterfaces);
    }
    
    protected JavaType _newSimpleType(final Class<?> raw, final TypeBindings bindings, final JavaType superClass, final JavaType[] superInterfaces) {
        return new SimpleType(raw, bindings, superClass, superInterfaces);
    }
    
    protected JavaType _unknownType() {
        return TypeFactory.CORE_TYPE_OBJECT;
    }
    
    protected JavaType _findWellKnownSimple(final Class<?> clz) {
        if (clz.isPrimitive()) {
            if (clz == TypeFactory.CLS_BOOL) {
                return TypeFactory.CORE_TYPE_BOOL;
            }
            if (clz == TypeFactory.CLS_INT) {
                return TypeFactory.CORE_TYPE_INT;
            }
            if (clz == TypeFactory.CLS_LONG) {
                return TypeFactory.CORE_TYPE_LONG;
            }
        }
        else {
            if (clz == TypeFactory.CLS_STRING) {
                return TypeFactory.CORE_TYPE_STRING;
            }
            if (clz == TypeFactory.CLS_OBJECT) {
                return TypeFactory.CORE_TYPE_OBJECT;
            }
        }
        return null;
    }
    
    protected JavaType _fromAny(final ClassStack context, final Type type, final TypeBindings bindings) {
        JavaType resultType;
        if (type instanceof Class) {
            resultType = this._fromClass(context, (Class<?>)type, TypeFactory.EMPTY_BINDINGS);
        }
        else if (type instanceof ParameterizedType) {
            resultType = this._fromParamType(context, (ParameterizedType)type, bindings);
        }
        else {
            if (type instanceof JavaType) {
                return (JavaType)type;
            }
            if (type instanceof GenericArrayType) {
                resultType = this._fromArrayType(context, (GenericArrayType)type, bindings);
            }
            else if (type instanceof TypeVariable) {
                resultType = this._fromVariable(context, (TypeVariable<?>)type, bindings);
            }
            else {
                if (!(type instanceof WildcardType)) {
                    throw new IllegalArgumentException("Unrecognized Type: " + ((type == null) ? "[null]" : type.toString()));
                }
                resultType = this._fromWildcard(context, (WildcardType)type, bindings);
            }
        }
        if (this._modifiers != null) {
            TypeBindings b = resultType.getBindings();
            if (b == null) {
                b = TypeFactory.EMPTY_BINDINGS;
            }
            for (final TypeModifier mod : this._modifiers) {
                final JavaType t = mod.modifyType(resultType, type, b, this);
                if (t == null) {
                    throw new IllegalStateException(String.format("TypeModifier %s (of type %s) return null for type %s", mod, mod.getClass().getName(), resultType));
                }
                resultType = t;
            }
        }
        return resultType;
    }
    
    protected JavaType _fromClass(ClassStack context, final Class<?> rawType, final TypeBindings bindings) {
        JavaType result = this._findWellKnownSimple(rawType);
        if (result != null) {
            return result;
        }
        Object key;
        if (bindings == null || bindings.isEmpty()) {
            key = rawType;
        }
        else {
            key = bindings.asKey(rawType);
        }
        result = this._typeCache.get(key);
        if (result != null) {
            return result;
        }
        if (context == null) {
            context = new ClassStack(rawType);
        }
        else {
            final ClassStack prev = context.find(rawType);
            if (prev != null) {
                final ResolvedRecursiveType selfRef = new ResolvedRecursiveType(rawType, TypeFactory.EMPTY_BINDINGS);
                prev.addSelfReference(selfRef);
                return selfRef;
            }
            context = context.child(rawType);
        }
        if (rawType.isArray()) {
            result = ArrayType.construct(this._fromAny(context, rawType.getComponentType(), bindings), bindings);
        }
        else {
            JavaType superClass;
            JavaType[] superInterfaces;
            if (rawType.isInterface()) {
                superClass = null;
                superInterfaces = this._resolveSuperInterfaces(context, rawType, bindings);
            }
            else {
                superClass = this._resolveSuperClass(context, rawType, bindings);
                superInterfaces = this._resolveSuperInterfaces(context, rawType, bindings);
            }
            if (rawType == Properties.class) {
                result = MapType.construct(rawType, bindings, superClass, superInterfaces, TypeFactory.CORE_TYPE_STRING, TypeFactory.CORE_TYPE_STRING);
            }
            else if (superClass != null) {
                result = superClass.refine(rawType, bindings, superClass, superInterfaces);
            }
            if (result == null) {
                result = this._fromWellKnownClass(context, rawType, bindings, superClass, superInterfaces);
                if (result == null) {
                    result = this._fromWellKnownInterface(context, rawType, bindings, superClass, superInterfaces);
                    if (result == null) {
                        result = this._newSimpleType(rawType, bindings, superClass, superInterfaces);
                    }
                }
            }
        }
        context.resolveSelfReferences(result);
        if (!result.hasHandlers()) {
            this._typeCache.putIfAbsent(key, result);
        }
        return result;
    }
    
    protected JavaType _resolveSuperClass(final ClassStack context, final Class<?> rawType, final TypeBindings parentBindings) {
        final Type parent = ClassUtil.getGenericSuperclass(rawType);
        if (parent == null) {
            return null;
        }
        return this._fromAny(context, parent, parentBindings);
    }
    
    protected JavaType[] _resolveSuperInterfaces(final ClassStack context, final Class<?> rawType, final TypeBindings parentBindings) {
        final Type[] types = ClassUtil.getGenericInterfaces(rawType);
        if (types == null || types.length == 0) {
            return TypeFactory.NO_TYPES;
        }
        final int len = types.length;
        final JavaType[] resolved = new JavaType[len];
        for (int i = 0; i < len; ++i) {
            final Type type = types[i];
            resolved[i] = this._fromAny(context, type, parentBindings);
        }
        return resolved;
    }
    
    protected JavaType _fromWellKnownClass(final ClassStack context, final Class<?> rawType, TypeBindings bindings, final JavaType superClass, final JavaType[] superInterfaces) {
        if (bindings == null) {
            bindings = TypeFactory.EMPTY_BINDINGS;
        }
        if (rawType == Map.class) {
            return this._mapType(rawType, bindings, superClass, superInterfaces);
        }
        if (rawType == Collection.class) {
            return this._collectionType(rawType, bindings, superClass, superInterfaces);
        }
        if (rawType == AtomicReference.class) {
            return this._referenceType(rawType, bindings, superClass, superInterfaces);
        }
        return null;
    }
    
    protected JavaType _fromWellKnownInterface(final ClassStack context, final Class<?> rawType, final TypeBindings bindings, final JavaType superClass, final JavaType[] superInterfaces) {
        for (int intCount = superInterfaces.length, i = 0; i < intCount; ++i) {
            final JavaType result = superInterfaces[i].refine(rawType, bindings, superClass, superInterfaces);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
    
    protected JavaType _fromParamType(final ClassStack context, final ParameterizedType ptype, final TypeBindings parentBindings) {
        final Class<?> rawType = (Class<?>)ptype.getRawType();
        if (rawType == TypeFactory.CLS_ENUM) {
            return TypeFactory.CORE_TYPE_ENUM;
        }
        if (rawType == TypeFactory.CLS_COMPARABLE) {
            return TypeFactory.CORE_TYPE_COMPARABLE;
        }
        if (rawType == TypeFactory.CLS_CLASS) {
            return TypeFactory.CORE_TYPE_CLASS;
        }
        final Type[] args = ptype.getActualTypeArguments();
        final int paramCount = (args == null) ? 0 : args.length;
        TypeBindings newBindings;
        if (paramCount == 0) {
            newBindings = TypeFactory.EMPTY_BINDINGS;
        }
        else {
            final JavaType[] pt = new JavaType[paramCount];
            for (int i = 0; i < paramCount; ++i) {
                pt[i] = this._fromAny(context, args[i], parentBindings);
            }
            newBindings = TypeBindings.create(rawType, pt);
        }
        return this._fromClass(context, rawType, newBindings);
    }
    
    protected JavaType _fromArrayType(final ClassStack context, final GenericArrayType type, final TypeBindings bindings) {
        final JavaType elementType = this._fromAny(context, type.getGenericComponentType(), bindings);
        return ArrayType.construct(elementType, bindings);
    }
    
    protected JavaType _fromVariable(final ClassStack context, final TypeVariable<?> var, TypeBindings bindings) {
        final String name = var.getName();
        if (bindings == null) {
            throw new Error("No Bindings!");
        }
        final JavaType type = bindings.findBoundType(name);
        if (type != null) {
            return type;
        }
        if (bindings.hasUnbound(name)) {
            return TypeFactory.CORE_TYPE_OBJECT;
        }
        bindings = bindings.withUnboundVariable(name);
        final Type[] bounds = var.getBounds();
        return this._fromAny(context, bounds[0], bindings);
    }
    
    protected JavaType _fromWildcard(final ClassStack context, final WildcardType type, final TypeBindings bindings) {
        return this._fromAny(context, type.getUpperBounds()[0], bindings);
    }
    
    static {
        NO_TYPES = new JavaType[0];
        instance = new TypeFactory();
        EMPTY_BINDINGS = TypeBindings.emptyBindings();
        CLS_STRING = String.class;
        CLS_OBJECT = Object.class;
        CLS_COMPARABLE = Comparable.class;
        CLS_CLASS = Class.class;
        CLS_ENUM = Enum.class;
        CLS_BOOL = Boolean.TYPE;
        CLS_INT = Integer.TYPE;
        CLS_LONG = Long.TYPE;
        CORE_TYPE_BOOL = new SimpleType(TypeFactory.CLS_BOOL);
        CORE_TYPE_INT = new SimpleType(TypeFactory.CLS_INT);
        CORE_TYPE_LONG = new SimpleType(TypeFactory.CLS_LONG);
        CORE_TYPE_STRING = new SimpleType(TypeFactory.CLS_STRING);
        CORE_TYPE_OBJECT = new SimpleType(TypeFactory.CLS_OBJECT);
        CORE_TYPE_COMPARABLE = new SimpleType(TypeFactory.CLS_COMPARABLE);
        CORE_TYPE_ENUM = new SimpleType(TypeFactory.CLS_ENUM);
        CORE_TYPE_CLASS = new SimpleType(TypeFactory.CLS_CLASS);
    }
}
