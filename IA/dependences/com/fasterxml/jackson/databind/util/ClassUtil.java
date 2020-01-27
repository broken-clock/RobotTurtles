// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.util;

import java.lang.reflect.Type;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.EnumSet;
import java.lang.reflect.AccessibleObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.lang.reflect.Constructor;
import java.io.Closeable;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.IOException;
import java.lang.reflect.Method;
import com.fasterxml.jackson.databind.annotation.NoClass;
import java.util.Map;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.databind.JavaType;
import java.util.Iterator;
import java.lang.annotation.Annotation;

public final class ClassUtil
{
    private static final Class<?> CLS_OBJECT;
    private static final Annotation[] NO_ANNOTATIONS;
    private static final Ctor[] NO_CTORS;
    private static final Iterator<?> EMPTY_ITERATOR;
    
    public static <T> Iterator<T> emptyIterator() {
        return (Iterator<T>)ClassUtil.EMPTY_ITERATOR;
    }
    
    public static List<JavaType> findSuperTypes(final JavaType type, final Class<?> endBefore, final boolean addClassItself) {
        if (type == null || type.hasRawClass(endBefore) || type.hasRawClass(Object.class)) {
            return Collections.emptyList();
        }
        final List<JavaType> result = new ArrayList<JavaType>(8);
        _addSuperTypes(type, endBefore, result, addClassItself);
        return result;
    }
    
    public static List<Class<?>> findRawSuperTypes(final Class<?> cls, final Class<?> endBefore, final boolean addClassItself) {
        if (cls == null || cls == endBefore || cls == Object.class) {
            return Collections.emptyList();
        }
        final List<Class<?>> result = new ArrayList<Class<?>>(8);
        _addRawSuperTypes(cls, endBefore, result, addClassItself);
        return result;
    }
    
    public static List<Class<?>> findSuperClasses(Class<?> cls, final Class<?> endBefore, final boolean addClassItself) {
        final List<Class<?>> result = new LinkedList<Class<?>>();
        if (cls != null && cls != endBefore) {
            if (addClassItself) {
                result.add(cls);
            }
            while ((cls = cls.getSuperclass()) != null) {
                if (cls == endBefore) {
                    break;
                }
                result.add(cls);
            }
        }
        return result;
    }
    
    @Deprecated
    public static List<Class<?>> findSuperTypes(final Class<?> cls, final Class<?> endBefore) {
        return findSuperTypes(cls, endBefore, new ArrayList<Class<?>>(8));
    }
    
    @Deprecated
    public static List<Class<?>> findSuperTypes(final Class<?> cls, final Class<?> endBefore, final List<Class<?>> result) {
        _addRawSuperTypes(cls, endBefore, result, false);
        return result;
    }
    
    private static void _addSuperTypes(final JavaType type, final Class<?> endBefore, final Collection<JavaType> result, final boolean addClassItself) {
        if (type == null) {
            return;
        }
        final Class<?> cls = type.getRawClass();
        if (cls == endBefore || cls == Object.class) {
            return;
        }
        if (addClassItself) {
            if (result.contains(type)) {
                return;
            }
            result.add(type);
        }
        for (final JavaType intCls : type.getInterfaces()) {
            _addSuperTypes(intCls, endBefore, result, true);
        }
        _addSuperTypes(type.getSuperClass(), endBefore, result, true);
    }
    
    private static void _addRawSuperTypes(final Class<?> cls, final Class<?> endBefore, final Collection<Class<?>> result, final boolean addClassItself) {
        if (cls == endBefore || cls == null || cls == Object.class) {
            return;
        }
        if (addClassItself) {
            if (result.contains(cls)) {
                return;
            }
            result.add(cls);
        }
        for (final Class<?> intCls : _interfaces(cls)) {
            _addRawSuperTypes(intCls, endBefore, result, true);
        }
        _addRawSuperTypes(cls.getSuperclass(), endBefore, result, true);
    }
    
    public static String canBeABeanType(final Class<?> type) {
        if (type.isAnnotation()) {
            return "annotation";
        }
        if (type.isArray()) {
            return "array";
        }
        if (type.isEnum()) {
            return "enum";
        }
        if (type.isPrimitive()) {
            return "primitive";
        }
        return null;
    }
    
    public static String isLocalType(final Class<?> type, final boolean allowNonStatic) {
        try {
            if (hasEnclosingMethod(type)) {
                return "local/anonymous";
            }
            if (!allowNonStatic && !Modifier.isStatic(type.getModifiers()) && getEnclosingClass(type) != null) {
                return "non-static member class";
            }
        }
        catch (SecurityException ex) {}
        catch (NullPointerException ex2) {}
        return null;
    }
    
    public static Class<?> getOuterClass(final Class<?> type) {
        try {
            if (hasEnclosingMethod(type)) {
                return null;
            }
            if (!Modifier.isStatic(type.getModifiers())) {
                return getEnclosingClass(type);
            }
        }
        catch (SecurityException ex) {}
        return null;
    }
    
    public static boolean isProxyType(final Class<?> type) {
        final String name = type.getName();
        return name.startsWith("net.sf.cglib.proxy.") || name.startsWith("org.hibernate.proxy.");
    }
    
    public static boolean isConcrete(final Class<?> type) {
        final int mod = type.getModifiers();
        return (mod & 0x600) == 0x0;
    }
    
    public static boolean isConcrete(final Member member) {
        final int mod = member.getModifiers();
        return (mod & 0x600) == 0x0;
    }
    
    public static boolean isCollectionMapOrArray(final Class<?> type) {
        return type.isArray() || Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type);
    }
    
    public static boolean isBogusClass(final Class<?> cls) {
        return cls == Void.class || cls == Void.TYPE || cls == NoClass.class;
    }
    
    public static boolean isNonStaticInnerClass(final Class<?> cls) {
        return !Modifier.isStatic(cls.getModifiers()) && getEnclosingClass(cls) != null;
    }
    
    public static boolean isObjectOrPrimitive(final Class<?> cls) {
        return cls == ClassUtil.CLS_OBJECT || cls.isPrimitive();
    }
    
    public static boolean hasClass(final Object inst, final Class<?> raw) {
        return inst != null && inst.getClass() == raw;
    }
    
    public static void verifyMustOverride(final Class<?> expType, final Object instance, final String method) {
        if (instance.getClass() != expType) {
            throw new IllegalStateException(String.format("Sub-class %s (of class %s) must override method '%s'", instance.getClass().getName(), expType.getName(), method));
        }
    }
    
    @Deprecated
    public static boolean hasGetterSignature(final Method m) {
        if (Modifier.isStatic(m.getModifiers())) {
            return false;
        }
        final Class<?>[] pts = m.getParameterTypes();
        return (pts == null || pts.length == 0) && Void.TYPE != m.getReturnType();
    }
    
    public static Throwable throwIfError(final Throwable t) {
        if (t instanceof Error) {
            throw (Error)t;
        }
        return t;
    }
    
    public static Throwable throwIfRTE(final Throwable t) {
        if (t instanceof RuntimeException) {
            throw (RuntimeException)t;
        }
        return t;
    }
    
    public static Throwable throwIfIOE(final Throwable t) throws IOException {
        if (t instanceof IOException) {
            throw (IOException)t;
        }
        return t;
    }
    
    public static Throwable getRootCause(Throwable t) {
        while (t.getCause() != null) {
            t = t.getCause();
        }
        return t;
    }
    
    public static Throwable throwRootCauseIfIOE(final Throwable t) throws IOException {
        return throwIfIOE(getRootCause(t));
    }
    
    public static void throwAsIAE(final Throwable t) {
        throwAsIAE(t, t.getMessage());
    }
    
    public static void throwAsIAE(final Throwable t, final String msg) {
        throwIfRTE(t);
        throwIfError(t);
        throw new IllegalArgumentException(msg, t);
    }
    
    public static <T> T throwAsMappingException(final DeserializationContext ctxt, final IOException e0) throws JsonMappingException {
        if (e0 instanceof JsonMappingException) {
            throw (JsonMappingException)e0;
        }
        final JsonMappingException e = JsonMappingException.from(ctxt, e0.getMessage());
        e.initCause(e0);
        throw e;
    }
    
    public static void unwrapAndThrowAsIAE(final Throwable t) {
        throwAsIAE(getRootCause(t));
    }
    
    public static void unwrapAndThrowAsIAE(final Throwable t, final String msg) {
        throwAsIAE(getRootCause(t), msg);
    }
    
    public static void closeOnFailAndThrowAsIOE(final JsonGenerator g, final Exception fail) throws IOException {
        g.disable(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT);
        try {
            g.close();
        }
        catch (Exception e) {
            fail.addSuppressed(e);
        }
        throwIfIOE(fail);
        throwIfRTE(fail);
        throw new RuntimeException(fail);
    }
    
    public static void closeOnFailAndThrowAsIOE(final JsonGenerator g, final Closeable toClose, final Exception fail) throws IOException {
        if (g != null) {
            g.disable(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT);
            try {
                g.close();
            }
            catch (Exception e) {
                fail.addSuppressed(e);
            }
        }
        if (toClose != null) {
            try {
                toClose.close();
            }
            catch (Exception e) {
                fail.addSuppressed(e);
            }
        }
        throwIfIOE(fail);
        throwIfRTE(fail);
        throw new RuntimeException(fail);
    }
    
    public static <T> T createInstance(final Class<T> cls, final boolean canFixAccess) throws IllegalArgumentException {
        final Constructor<T> ctor = findConstructor(cls, canFixAccess);
        if (ctor == null) {
            throw new IllegalArgumentException("Class " + cls.getName() + " has no default (no arg) constructor");
        }
        try {
            return ctor.newInstance(new Object[0]);
        }
        catch (Exception e) {
            unwrapAndThrowAsIAE(e, "Failed to instantiate class " + cls.getName() + ", problem: " + e.getMessage());
            return null;
        }
    }
    
    public static <T> Constructor<T> findConstructor(final Class<T> cls, final boolean forceAccess) throws IllegalArgumentException {
        try {
            final Constructor<T> ctor = cls.getDeclaredConstructor((Class<?>[])new Class[0]);
            if (forceAccess) {
                checkAndFixAccess(ctor, forceAccess);
            }
            else if (!Modifier.isPublic(ctor.getModifiers())) {
                throw new IllegalArgumentException("Default constructor for " + cls.getName() + " is not accessible (non-public?): not allowed to try modify access via Reflection: cannot instantiate type");
            }
            return ctor;
        }
        catch (NoSuchMethodException ex) {}
        catch (Exception e) {
            unwrapAndThrowAsIAE(e, "Failed to find default constructor of class " + cls.getName() + ", problem: " + e.getMessage());
        }
        return null;
    }
    
    public static Class<?> classOf(final Object inst) {
        if (inst == null) {
            return null;
        }
        return inst.getClass();
    }
    
    public static Class<?> rawClass(final JavaType t) {
        if (t == null) {
            return null;
        }
        return t.getRawClass();
    }
    
    public static <T> T nonNull(final T valueOrNull, final T defaultValue) {
        return (valueOrNull == null) ? defaultValue : valueOrNull;
    }
    
    public static String nullOrToString(final Object value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }
    
    public static String nonNullString(final String str) {
        if (str == null) {
            return "";
        }
        return str;
    }
    
    public static String quotedOr(final Object str, final String forNull) {
        if (str == null) {
            return forNull;
        }
        return String.format("\"%s\"", str);
    }
    
    public static String getClassDescription(final Object classOrInstance) {
        if (classOrInstance == null) {
            return "unknown";
        }
        final Class<?> cls = (Class<?>)((classOrInstance instanceof Class) ? ((Class)classOrInstance) : classOrInstance.getClass());
        return nameOf(cls);
    }
    
    public static String classNameOf(final Object inst) {
        if (inst == null) {
            return "[null]";
        }
        return nameOf(inst.getClass());
    }
    
    public static String nameOf(Class<?> cls) {
        if (cls == null) {
            return "[null]";
        }
        int index = 0;
        while (cls.isArray()) {
            ++index;
            cls = cls.getComponentType();
        }
        String base = cls.isPrimitive() ? cls.getSimpleName() : cls.getName();
        if (index > 0) {
            final StringBuilder sb = new StringBuilder(base);
            do {
                sb.append("[]");
            } while (--index > 0);
            base = sb.toString();
        }
        return backticked(base);
    }
    
    public static String nameOf(final Named named) {
        if (named == null) {
            return "[null]";
        }
        return backticked(named.getName());
    }
    
    public static String backticked(final String text) {
        if (text == null) {
            return "[null]";
        }
        return new StringBuilder(text.length() + 2).append('`').append(text).append('`').toString();
    }
    
    public static String exceptionMessage(final Throwable t) {
        if (t instanceof JsonProcessingException) {
            return ((JsonProcessingException)t).getOriginalMessage();
        }
        return t.getMessage();
    }
    
    public static Object defaultValue(final Class<?> cls) {
        if (cls == Integer.TYPE) {
            return 0;
        }
        if (cls == Long.TYPE) {
            return 0L;
        }
        if (cls == Boolean.TYPE) {
            return Boolean.FALSE;
        }
        if (cls == Double.TYPE) {
            return 0.0;
        }
        if (cls == Float.TYPE) {
            return 0.0f;
        }
        if (cls == Byte.TYPE) {
            return 0;
        }
        if (cls == Short.TYPE) {
            return 0;
        }
        if (cls == Character.TYPE) {
            return '\0';
        }
        throw new IllegalArgumentException("Class " + cls.getName() + " is not a primitive type");
    }
    
    public static Class<?> wrapperType(final Class<?> primitiveType) {
        if (primitiveType == Integer.TYPE) {
            return Integer.class;
        }
        if (primitiveType == Long.TYPE) {
            return Long.class;
        }
        if (primitiveType == Boolean.TYPE) {
            return Boolean.class;
        }
        if (primitiveType == Double.TYPE) {
            return Double.class;
        }
        if (primitiveType == Float.TYPE) {
            return Float.class;
        }
        if (primitiveType == Byte.TYPE) {
            return Byte.class;
        }
        if (primitiveType == Short.TYPE) {
            return Short.class;
        }
        if (primitiveType == Character.TYPE) {
            return Character.class;
        }
        throw new IllegalArgumentException("Class " + primitiveType.getName() + " is not a primitive type");
    }
    
    public static Class<?> primitiveType(final Class<?> type) {
        if (type.isPrimitive()) {
            return type;
        }
        if (type == Integer.class) {
            return Integer.TYPE;
        }
        if (type == Long.class) {
            return Long.TYPE;
        }
        if (type == Boolean.class) {
            return Boolean.TYPE;
        }
        if (type == Double.class) {
            return Double.TYPE;
        }
        if (type == Float.class) {
            return Float.TYPE;
        }
        if (type == Byte.class) {
            return Byte.TYPE;
        }
        if (type == Short.class) {
            return Short.TYPE;
        }
        if (type == Character.class) {
            return Character.TYPE;
        }
        return null;
    }
    
    @Deprecated
    public static void checkAndFixAccess(final Member member) {
        checkAndFixAccess(member, false);
    }
    
    public static void checkAndFixAccess(final Member member, final boolean force) {
        final AccessibleObject ao = (AccessibleObject)member;
        try {
            if (force || !Modifier.isPublic(member.getModifiers()) || !Modifier.isPublic(member.getDeclaringClass().getModifiers())) {
                ao.setAccessible(true);
            }
        }
        catch (SecurityException se) {
            if (!ao.isAccessible()) {
                final Class<?> declClass = member.getDeclaringClass();
                throw new IllegalArgumentException("Cannot access " + member + " (from class " + declClass.getName() + "; failed to set access: " + se.getMessage());
            }
        }
    }
    
    public static Class<? extends Enum<?>> findEnumType(final EnumSet<?> s) {
        if (!s.isEmpty()) {
            return findEnumType(s.iterator().next());
        }
        return EnumTypeLocator.instance.enumTypeFor(s);
    }
    
    public static Class<? extends Enum<?>> findEnumType(final EnumMap<?, ?> m) {
        if (!m.isEmpty()) {
            return findEnumType((Enum<?>)m.keySet().iterator().next());
        }
        return EnumTypeLocator.instance.enumTypeFor(m);
    }
    
    public static Class<? extends Enum<?>> findEnumType(final Enum<?> en) {
        Class<?> ec = en.getClass();
        if (ec.getSuperclass() != Enum.class) {
            ec = ec.getSuperclass();
        }
        return (Class<? extends Enum<?>>)ec;
    }
    
    public static Class<? extends Enum<?>> findEnumType(Class<?> cls) {
        if (cls.getSuperclass() != Enum.class) {
            cls = cls.getSuperclass();
        }
        return (Class<? extends Enum<?>>)cls;
    }
    
    public static <T extends Annotation> Enum<?> findFirstAnnotatedEnumValue(final Class<Enum<?>> enumClass, final Class<T> annotationClass) {
        final Field[] declaredFields;
        final Field[] fields = declaredFields = getDeclaredFields(enumClass);
        for (final Field field : declaredFields) {
            if (field.isEnumConstant()) {
                final Annotation defaultValueAnnotation = field.getAnnotation(annotationClass);
                if (defaultValueAnnotation != null) {
                    final String name = field.getName();
                    for (final Enum<?> enumValue : enumClass.getEnumConstants()) {
                        if (name.equals(enumValue.name())) {
                            return enumValue;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public static boolean isJacksonStdImpl(final Object impl) {
        return impl == null || isJacksonStdImpl(impl.getClass());
    }
    
    public static boolean isJacksonStdImpl(final Class<?> implClass) {
        return implClass.getAnnotation(JacksonStdImpl.class) != null;
    }
    
    public static String getPackageName(final Class<?> cls) {
        final Package pkg = cls.getPackage();
        return (pkg == null) ? null : pkg.getName();
    }
    
    public static boolean hasEnclosingMethod(final Class<?> cls) {
        return !isObjectOrPrimitive(cls) && cls.getEnclosingMethod() != null;
    }
    
    public static Field[] getDeclaredFields(final Class<?> cls) {
        return cls.getDeclaredFields();
    }
    
    public static Method[] getDeclaredMethods(final Class<?> cls) {
        return cls.getDeclaredMethods();
    }
    
    public static Annotation[] findClassAnnotations(final Class<?> cls) {
        if (isObjectOrPrimitive(cls)) {
            return ClassUtil.NO_ANNOTATIONS;
        }
        return cls.getDeclaredAnnotations();
    }
    
    public static Method[] getClassMethods(final Class<?> cls) {
        try {
            return getDeclaredMethods(cls);
        }
        catch (NoClassDefFoundError ex) {
            final ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if (loader == null) {
                throw ex;
            }
            Class<?> contextClass;
            try {
                contextClass = loader.loadClass(cls.getName());
            }
            catch (ClassNotFoundException e) {
                ex.addSuppressed(e);
                throw ex;
            }
            return contextClass.getDeclaredMethods();
        }
    }
    
    public static Ctor[] getConstructors(final Class<?> cls) {
        if (cls.isInterface() || isObjectOrPrimitive(cls)) {
            return ClassUtil.NO_CTORS;
        }
        final Constructor<?>[] rawCtors = cls.getDeclaredConstructors();
        final int len = rawCtors.length;
        final Ctor[] result = new Ctor[len];
        for (int i = 0; i < len; ++i) {
            result[i] = new Ctor(rawCtors[i]);
        }
        return result;
    }
    
    public static Class<?> getDeclaringClass(final Class<?> cls) {
        return isObjectOrPrimitive(cls) ? null : cls.getDeclaringClass();
    }
    
    public static Type getGenericSuperclass(final Class<?> cls) {
        return cls.getGenericSuperclass();
    }
    
    public static Type[] getGenericInterfaces(final Class<?> cls) {
        return cls.getGenericInterfaces();
    }
    
    public static Class<?> getEnclosingClass(final Class<?> cls) {
        return isObjectOrPrimitive(cls) ? null : cls.getEnclosingClass();
    }
    
    private static Class<?>[] _interfaces(final Class<?> cls) {
        return cls.getInterfaces();
    }
    
    static {
        CLS_OBJECT = Object.class;
        NO_ANNOTATIONS = new Annotation[0];
        NO_CTORS = new Ctor[0];
        EMPTY_ITERATOR = Collections.emptyIterator();
    }
    
    private static class EnumTypeLocator
    {
        static final EnumTypeLocator instance;
        private final Field enumSetTypeField;
        private final Field enumMapTypeField;
        
        private EnumTypeLocator() {
            this.enumSetTypeField = locateField(EnumSet.class, "elementType", Class.class);
            this.enumMapTypeField = locateField(EnumMap.class, "elementType", Class.class);
        }
        
        public Class<? extends Enum<?>> enumTypeFor(final EnumSet<?> set) {
            if (this.enumSetTypeField != null) {
                return (Class<? extends Enum<?>>)this.get(set, this.enumSetTypeField);
            }
            throw new IllegalStateException("Cannot figure out type for EnumSet (odd JDK platform?)");
        }
        
        public Class<? extends Enum<?>> enumTypeFor(final EnumMap<?, ?> set) {
            if (this.enumMapTypeField != null) {
                return (Class<? extends Enum<?>>)this.get(set, this.enumMapTypeField);
            }
            throw new IllegalStateException("Cannot figure out type for EnumMap (odd JDK platform?)");
        }
        
        private Object get(final Object bean, final Field field) {
            try {
                return field.get(bean);
            }
            catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
        
        private static Field locateField(final Class<?> fromClass, final String expectedName, final Class<?> type) {
            Field found = null;
            final Field[] declaredFields;
            final Field[] fields = declaredFields = ClassUtil.getDeclaredFields(fromClass);
            for (final Field f : declaredFields) {
                if (expectedName.equals(f.getName()) && f.getType() == type) {
                    found = f;
                    break;
                }
            }
            if (found == null) {
                for (final Field f : fields) {
                    if (f.getType() == type) {
                        if (found != null) {
                            return null;
                        }
                        found = f;
                    }
                }
            }
            if (found != null) {
                try {
                    found.setAccessible(true);
                }
                catch (Throwable t) {}
            }
            return found;
        }
        
        static {
            instance = new EnumTypeLocator();
        }
    }
    
    public static final class Ctor
    {
        public final Constructor<?> _ctor;
        private Annotation[] _annotations;
        private Annotation[][] _paramAnnotations;
        private int _paramCount;
        
        public Ctor(final Constructor<?> ctor) {
            this._paramCount = -1;
            this._ctor = ctor;
        }
        
        public Constructor<?> getConstructor() {
            return this._ctor;
        }
        
        public int getParamCount() {
            int c = this._paramCount;
            if (c < 0) {
                c = this._ctor.getParameterTypes().length;
                this._paramCount = c;
            }
            return c;
        }
        
        public Class<?> getDeclaringClass() {
            return this._ctor.getDeclaringClass();
        }
        
        public Annotation[] getDeclaredAnnotations() {
            Annotation[] result = this._annotations;
            if (result == null) {
                result = this._ctor.getDeclaredAnnotations();
                this._annotations = result;
            }
            return result;
        }
        
        public Annotation[][] getParameterAnnotations() {
            Annotation[][] result = this._paramAnnotations;
            if (result == null) {
                result = this._ctor.getParameterAnnotations();
                this._paramAnnotations = result;
            }
            return result;
        }
    }
}
