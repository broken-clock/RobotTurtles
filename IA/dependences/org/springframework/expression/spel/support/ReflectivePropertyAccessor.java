// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.support;

import org.springframework.core.style.ToStringCreator;
import java.util.Collections;
import java.util.HashSet;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import java.util.Arrays;
import java.util.Comparator;
import java.lang.reflect.Modifier;
import org.springframework.expression.EvaluationException;
import java.lang.reflect.Array;
import org.springframework.util.ReflectionUtils;
import org.springframework.expression.TypedValue;
import org.springframework.expression.AccessException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.springframework.core.convert.Property;
import org.springframework.expression.EvaluationContext;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.convert.TypeDescriptor;
import java.lang.reflect.Member;
import java.util.Map;
import java.util.Set;
import org.springframework.expression.PropertyAccessor;

public class ReflectivePropertyAccessor implements PropertyAccessor
{
    private static final Set<Class<?>> BOOLEAN_TYPES;
    private static final Set<Class<?>> ANY_TYPES;
    private final Map<CacheKey, InvokerPair> readerCache;
    private final Map<CacheKey, Member> writerCache;
    private final Map<CacheKey, TypeDescriptor> typeDescriptorCache;
    
    public ReflectivePropertyAccessor() {
        this.readerCache = new ConcurrentHashMap<CacheKey, InvokerPair>(64);
        this.writerCache = new ConcurrentHashMap<CacheKey, Member>(64);
        this.typeDescriptorCache = new ConcurrentHashMap<CacheKey, TypeDescriptor>(64);
    }
    
    @Override
    public Class<?>[] getSpecificTargetClasses() {
        return null;
    }
    
    @Override
    public boolean canRead(final EvaluationContext context, final Object target, final String name) throws AccessException {
        if (target == null) {
            return false;
        }
        final Class<?> type = (Class<?>)((target instanceof Class) ? ((Class)target) : target.getClass());
        if (type.isArray() && name.equals("length")) {
            return true;
        }
        final CacheKey cacheKey = new CacheKey(type, name, target instanceof Class);
        if (this.readerCache.containsKey(cacheKey)) {
            return true;
        }
        final Method method = this.findGetterForProperty(name, type, target);
        if (method != null) {
            final Property property = new Property(type, method, null);
            final TypeDescriptor typeDescriptor = new TypeDescriptor(property);
            this.readerCache.put(cacheKey, new InvokerPair(method, typeDescriptor));
            this.typeDescriptorCache.put(cacheKey, typeDescriptor);
            return true;
        }
        final Field field = this.findField(name, type, target);
        if (field != null) {
            final TypeDescriptor typeDescriptor = new TypeDescriptor(field);
            this.readerCache.put(cacheKey, new InvokerPair(field, typeDescriptor));
            this.typeDescriptorCache.put(cacheKey, typeDescriptor);
            return true;
        }
        return false;
    }
    
    @Override
    public TypedValue read(final EvaluationContext context, final Object target, final String name) throws AccessException {
        if (target == null) {
            throw new AccessException("Cannot read property of null target");
        }
        final Class<?> type = (Class<?>)((target instanceof Class) ? ((Class)target) : target.getClass());
        if (!type.isArray() || !name.equals("length")) {
            final CacheKey cacheKey = new CacheKey(type, name, target instanceof Class);
            InvokerPair invoker = this.readerCache.get(cacheKey);
            if (invoker == null || invoker.member instanceof Method) {
                Method method = (Method)((invoker != null) ? invoker.member : null);
                if (method == null) {
                    method = this.findGetterForProperty(name, type, target);
                    if (method != null) {
                        final Property property = new Property(type, method, null);
                        final TypeDescriptor typeDescriptor = new TypeDescriptor(property);
                        invoker = new InvokerPair(method, typeDescriptor);
                        this.readerCache.put(cacheKey, invoker);
                    }
                }
                if (method != null) {
                    try {
                        ReflectionUtils.makeAccessible(method);
                        final Object value = method.invoke(target, new Object[0]);
                        return new TypedValue(value, invoker.typeDescriptor.narrow(value));
                    }
                    catch (Exception ex) {
                        throw new AccessException("Unable to access property '" + name + "' through getter", ex);
                    }
                }
            }
            if (invoker == null || invoker.member instanceof Field) {
                Field field = (Field)((invoker == null) ? null : invoker.member);
                if (field == null) {
                    field = this.findField(name, type, target);
                    if (field != null) {
                        invoker = new InvokerPair(field, new TypeDescriptor(field));
                        this.readerCache.put(cacheKey, invoker);
                    }
                }
                if (field != null) {
                    try {
                        ReflectionUtils.makeAccessible(field);
                        final Object value = field.get(target);
                        return new TypedValue(value, invoker.typeDescriptor.narrow(value));
                    }
                    catch (Exception ex) {
                        throw new AccessException("Unable to access field: " + name, ex);
                    }
                }
            }
            throw new AccessException("Neither getter nor field found for property '" + name + "'");
        }
        if (target instanceof Class) {
            throw new AccessException("Cannot access length on array class itself");
        }
        return new TypedValue(Array.getLength(target));
    }
    
    @Override
    public boolean canWrite(final EvaluationContext context, final Object target, final String name) throws AccessException {
        if (target == null) {
            return false;
        }
        final Class<?> type = (Class<?>)((target instanceof Class) ? ((Class)target) : target.getClass());
        final CacheKey cacheKey = new CacheKey(type, name, target instanceof Class);
        if (this.writerCache.containsKey(cacheKey)) {
            return true;
        }
        final Method method = this.findSetterForProperty(name, type, target);
        if (method != null) {
            final Property property = new Property(type, null, method);
            final TypeDescriptor typeDescriptor = new TypeDescriptor(property);
            this.writerCache.put(cacheKey, method);
            this.typeDescriptorCache.put(cacheKey, typeDescriptor);
            return true;
        }
        final Field field = this.findField(name, type, target);
        if (field != null) {
            this.writerCache.put(cacheKey, field);
            this.typeDescriptorCache.put(cacheKey, new TypeDescriptor(field));
            return true;
        }
        return false;
    }
    
    @Override
    public void write(final EvaluationContext context, final Object target, final String name, final Object newValue) throws AccessException {
        if (target == null) {
            throw new AccessException("Cannot write property on null target");
        }
        final Class<?> type = (Class<?>)((target instanceof Class) ? ((Class)target) : target.getClass());
        Object possiblyConvertedNewValue = newValue;
        final TypeDescriptor typeDescriptor = this.getTypeDescriptor(context, target, name);
        if (typeDescriptor != null) {
            try {
                possiblyConvertedNewValue = context.getTypeConverter().convertValue(newValue, TypeDescriptor.forObject(newValue), typeDescriptor);
            }
            catch (EvaluationException evaluationException) {
                throw new AccessException("Type conversion failure", evaluationException);
            }
        }
        final CacheKey cacheKey = new CacheKey(type, name, target instanceof Class);
        Member cachedMember = this.writerCache.get(cacheKey);
        if (cachedMember == null || cachedMember instanceof Method) {
            Method method = (Method)cachedMember;
            if (method == null) {
                method = this.findSetterForProperty(name, type, target);
                if (method != null) {
                    cachedMember = method;
                    this.writerCache.put(cacheKey, cachedMember);
                }
            }
            if (method != null) {
                try {
                    ReflectionUtils.makeAccessible(method);
                    method.invoke(target, possiblyConvertedNewValue);
                    return;
                }
                catch (Exception ex) {
                    throw new AccessException("Unable to access property '" + name + "' through setter", ex);
                }
            }
        }
        if (cachedMember == null || cachedMember instanceof Field) {
            Field field = (Field)cachedMember;
            if (field == null) {
                field = this.findField(name, type, target);
                if (field != null) {
                    cachedMember = field;
                    this.writerCache.put(cacheKey, cachedMember);
                }
            }
            if (field != null) {
                try {
                    ReflectionUtils.makeAccessible(field);
                    field.set(target, possiblyConvertedNewValue);
                    return;
                }
                catch (Exception ex) {
                    throw new AccessException("Unable to access field: " + name, ex);
                }
            }
        }
        throw new AccessException("Neither setter nor field found for property '" + name + "'");
    }
    
    private TypeDescriptor getTypeDescriptor(final EvaluationContext context, final Object target, final String name) {
        if (target == null) {
            return null;
        }
        final Class<?> type = (Class<?>)((target instanceof Class) ? ((Class)target) : target.getClass());
        if (type.isArray() && name.equals("length")) {
            return TypeDescriptor.valueOf(Integer.TYPE);
        }
        final CacheKey cacheKey = new CacheKey(type, name, target instanceof Class);
        TypeDescriptor typeDescriptor = this.typeDescriptorCache.get(cacheKey);
        if (typeDescriptor == null) {
            try {
                if (this.canRead(context, target, name)) {
                    typeDescriptor = this.typeDescriptorCache.get(cacheKey);
                }
                else if (this.canWrite(context, target, name)) {
                    typeDescriptor = this.typeDescriptorCache.get(cacheKey);
                }
            }
            catch (AccessException ex) {}
        }
        return typeDescriptor;
    }
    
    private Method findGetterForProperty(final String propertyName, final Class<?> clazz, final Object target) {
        Method method = this.findGetterForProperty(propertyName, clazz, target instanceof Class);
        if (method == null && target instanceof Class) {
            method = this.findGetterForProperty(propertyName, target.getClass(), false);
        }
        return method;
    }
    
    private Method findSetterForProperty(final String propertyName, final Class<?> clazz, final Object target) {
        Method method = this.findSetterForProperty(propertyName, clazz, target instanceof Class);
        if (method == null && target instanceof Class) {
            method = this.findSetterForProperty(propertyName, target.getClass(), false);
        }
        return method;
    }
    
    private Field findField(final String name, final Class<?> clazz, final Object target) {
        Field field = this.findField(name, clazz, target instanceof Class);
        if (field == null && target instanceof Class) {
            field = this.findField(name, target.getClass(), false);
        }
        return field;
    }
    
    protected Method findGetterForProperty(final String propertyName, final Class<?> clazz, final boolean mustBeStatic) {
        Method method = this.findMethodForProperty(this.getPropertyMethodSuffixes(propertyName), "get", clazz, mustBeStatic, 0, ReflectivePropertyAccessor.ANY_TYPES);
        if (method == null) {
            method = this.findMethodForProperty(this.getPropertyMethodSuffixes(propertyName), "is", clazz, mustBeStatic, 0, ReflectivePropertyAccessor.BOOLEAN_TYPES);
        }
        return method;
    }
    
    protected Method findSetterForProperty(final String propertyName, final Class<?> clazz, final boolean mustBeStatic) {
        return this.findMethodForProperty(this.getPropertyMethodSuffixes(propertyName), "set", clazz, mustBeStatic, 1, ReflectivePropertyAccessor.ANY_TYPES);
    }
    
    private Method findMethodForProperty(final String[] methodSuffixes, final String prefix, final Class<?> clazz, final boolean mustBeStatic, final int numberOfParams, final Set<Class<?>> requiredReturnTypes) {
        final Method[] methods = this.getSortedClassMethods(clazz);
        for (final String methodSuffix : methodSuffixes) {
            for (final Method method : methods) {
                if (method.getName().equals(prefix + methodSuffix) && method.getParameterTypes().length == numberOfParams && (!mustBeStatic || Modifier.isStatic(method.getModifiers())) && (requiredReturnTypes.isEmpty() || requiredReturnTypes.contains(method.getReturnType()))) {
                    return method;
                }
            }
        }
        return null;
    }
    
    private Method[] getSortedClassMethods(final Class<?> clazz) {
        final Method[] methods = clazz.getMethods();
        Arrays.sort(methods, new Comparator<Method>() {
            @Override
            public int compare(final Method o1, final Method o2) {
                return (o1.isBridge() == o2.isBridge()) ? 0 : (o1.isBridge() ? 1 : -1);
            }
        });
        return methods;
    }
    
    protected String[] getPropertyMethodSuffixes(final String propertyName) {
        final String suffix = this.getPropertyMethodSuffix(propertyName);
        if (suffix.length() > 0 && Character.isUpperCase(suffix.charAt(0))) {
            return new String[] { suffix };
        }
        return new String[] { suffix, StringUtils.capitalize(suffix) };
    }
    
    protected String getPropertyMethodSuffix(final String propertyName) {
        if (propertyName.length() > 1 && Character.isUpperCase(propertyName.charAt(1))) {
            return propertyName;
        }
        return StringUtils.capitalize(propertyName);
    }
    
    protected Field findField(final String name, final Class<?> clazz, final boolean mustBeStatic) {
        final Field[] fields2;
        final Field[] fields = fields2 = clazz.getFields();
        for (final Field field : fields2) {
            if (field.getName().equals(name) && (!mustBeStatic || Modifier.isStatic(field.getModifiers()))) {
                return field;
            }
        }
        if (clazz.getSuperclass() != null) {
            final Field field2 = this.findField(name, clazz.getSuperclass(), mustBeStatic);
            if (field2 != null) {
                return field2;
            }
        }
        for (final Class<?> implementedInterface : clazz.getInterfaces()) {
            final Field field3 = this.findField(name, implementedInterface, mustBeStatic);
            if (field3 != null) {
                return field3;
            }
        }
        return null;
    }
    
    public PropertyAccessor createOptimalAccessor(final EvaluationContext eContext, final Object target, final String name) {
        if (target == null) {
            return this;
        }
        final Class<?> type = (Class<?>)((target instanceof Class) ? ((Class)target) : target.getClass());
        if (type.isArray()) {
            return this;
        }
        final CacheKey cacheKey = new CacheKey(type, name, target instanceof Class);
        InvokerPair invocationTarget = this.readerCache.get(cacheKey);
        if (invocationTarget == null || invocationTarget.member instanceof Method) {
            Method method = (Method)((invocationTarget == null) ? null : invocationTarget.member);
            if (method == null) {
                method = this.findGetterForProperty(name, type, target);
                if (method != null) {
                    invocationTarget = new InvokerPair(method, new TypeDescriptor(new MethodParameter(method, -1)));
                    ReflectionUtils.makeAccessible(method);
                    this.readerCache.put(cacheKey, invocationTarget);
                }
            }
            if (method != null) {
                return new OptimalPropertyAccessor(invocationTarget);
            }
        }
        if (invocationTarget == null || invocationTarget.member instanceof Field) {
            Field field = (Field)((invocationTarget == null) ? null : invocationTarget.member);
            if (field == null) {
                field = this.findField(name, type, target instanceof Class);
                if (field != null) {
                    invocationTarget = new InvokerPair(field, new TypeDescriptor(field));
                    ReflectionUtils.makeAccessible(field);
                    this.readerCache.put(cacheKey, invocationTarget);
                }
            }
            if (field != null) {
                return new OptimalPropertyAccessor(invocationTarget);
            }
        }
        return this;
    }
    
    static {
        final Set<Class<?>> booleanTypes = new HashSet<Class<?>>();
        booleanTypes.add(Boolean.class);
        booleanTypes.add(Boolean.TYPE);
        BOOLEAN_TYPES = Collections.unmodifiableSet((Set<? extends Class<?>>)booleanTypes);
        ANY_TYPES = Collections.emptySet();
    }
    
    private static class InvokerPair
    {
        final Member member;
        final TypeDescriptor typeDescriptor;
        
        public InvokerPair(final Member member, final TypeDescriptor typeDescriptor) {
            this.member = member;
            this.typeDescriptor = typeDescriptor;
        }
    }
    
    private static class CacheKey
    {
        private final Class<?> clazz;
        private final String name;
        private boolean targetIsClass;
        
        public CacheKey(final Class<?> clazz, final String name, final boolean targetIsClass) {
            this.clazz = clazz;
            this.name = name;
            this.targetIsClass = targetIsClass;
        }
        
        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof CacheKey)) {
                return false;
            }
            final CacheKey otherKey = (CacheKey)other;
            boolean rtn = true;
            rtn &= this.clazz.equals(otherKey.clazz);
            rtn &= this.name.equals(otherKey.name);
            rtn &= (this.targetIsClass == otherKey.targetIsClass);
            return rtn;
        }
        
        @Override
        public int hashCode() {
            return this.clazz.hashCode() * 29 + this.name.hashCode();
        }
        
        @Override
        public String toString() {
            return new ToStringCreator(this).append("clazz", this.clazz).append("name", this.name).append("targetIsClass", this.targetIsClass).toString();
        }
    }
    
    private static class OptimalPropertyAccessor implements PropertyAccessor
    {
        private final Member member;
        private final TypeDescriptor typeDescriptor;
        private final boolean needsToBeMadeAccessible;
        
        OptimalPropertyAccessor(final InvokerPair target) {
            this.member = target.member;
            this.typeDescriptor = target.typeDescriptor;
            if (this.member instanceof Field) {
                final Field field = (Field)this.member;
                this.needsToBeMadeAccessible = ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())) && !field.isAccessible());
            }
            else {
                final Method method = (Method)this.member;
                this.needsToBeMadeAccessible = ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers())) && !method.isAccessible());
            }
        }
        
        @Override
        public Class<?>[] getSpecificTargetClasses() {
            throw new UnsupportedOperationException("Should not be called on an OptimalPropertyAccessor");
        }
        
        @Override
        public boolean canRead(final EvaluationContext context, final Object target, final String name) throws AccessException {
            if (target == null) {
                return false;
            }
            final Class<?> type = (Class<?>)((target instanceof Class) ? ((Class)target) : target.getClass());
            if (type.isArray()) {
                return false;
            }
            if (!(this.member instanceof Method)) {
                final Field field = (Field)this.member;
                return field.getName().equals(name);
            }
            final Method method = (Method)this.member;
            String getterName = "get" + StringUtils.capitalize(name);
            if (getterName.equals(method.getName())) {
                return true;
            }
            getterName = "is" + StringUtils.capitalize(name);
            return getterName.equals(method.getName());
        }
        
        @Override
        public TypedValue read(final EvaluationContext context, final Object target, final String name) throws AccessException {
            if (this.member instanceof Method) {
                try {
                    if (this.needsToBeMadeAccessible) {
                        ReflectionUtils.makeAccessible((Method)this.member);
                    }
                    final Object value = ((Method)this.member).invoke(target, new Object[0]);
                    return new TypedValue(value, this.typeDescriptor.narrow(value));
                }
                catch (Exception ex) {
                    throw new AccessException("Unable to access property '" + name + "' through getter", ex);
                }
            }
            if (this.member instanceof Field) {
                try {
                    if (this.needsToBeMadeAccessible) {
                        ReflectionUtils.makeAccessible((Field)this.member);
                    }
                    final Object value = ((Field)this.member).get(target);
                    return new TypedValue(value, this.typeDescriptor.narrow(value));
                }
                catch (Exception ex) {
                    throw new AccessException("Unable to access field: " + name, ex);
                }
            }
            throw new AccessException("Neither getter nor field found for property '" + name + "'");
        }
        
        @Override
        public boolean canWrite(final EvaluationContext context, final Object target, final String name) {
            throw new UnsupportedOperationException("Should not be called on an OptimalPropertyAccessor");
        }
        
        @Override
        public void write(final EvaluationContext context, final Object target, final String name, final Object newValue) {
            throw new UnsupportedOperationException("Should not be called on an OptimalPropertyAccessor");
        }
    }
}
