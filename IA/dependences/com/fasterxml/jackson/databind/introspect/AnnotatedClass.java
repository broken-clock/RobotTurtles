// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.introspect;

import java.lang.reflect.AnnotatedElement;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import java.util.Collections;
import com.fasterxml.jackson.databind.util.Annotations;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import java.util.List;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.JavaType;

public final class AnnotatedClass extends Annotated implements TypeResolutionContext
{
    private static final Creators NO_CREATORS;
    protected final JavaType _type;
    protected final Class<?> _class;
    protected final TypeBindings _bindings;
    protected final List<JavaType> _superTypes;
    protected final AnnotationIntrospector _annotationIntrospector;
    protected final TypeFactory _typeFactory;
    protected final ClassIntrospector.MixInResolver _mixInResolver;
    protected final Class<?> _primaryMixIn;
    protected final Annotations _classAnnotations;
    protected Creators _creators;
    protected AnnotatedMethodMap _memberMethods;
    protected List<AnnotatedField> _fields;
    protected transient Boolean _nonStaticInnerClass;
    
    AnnotatedClass(final JavaType type, final Class<?> rawType, final List<JavaType> superTypes, final Class<?> primaryMixIn, final Annotations classAnnotations, final TypeBindings bindings, final AnnotationIntrospector aintr, final ClassIntrospector.MixInResolver mir, final TypeFactory tf) {
        this._type = type;
        this._class = rawType;
        this._superTypes = superTypes;
        this._primaryMixIn = primaryMixIn;
        this._classAnnotations = classAnnotations;
        this._bindings = bindings;
        this._annotationIntrospector = aintr;
        this._mixInResolver = mir;
        this._typeFactory = tf;
    }
    
    AnnotatedClass(final Class<?> rawType) {
        this._type = null;
        this._class = rawType;
        this._superTypes = Collections.emptyList();
        this._primaryMixIn = null;
        this._classAnnotations = AnnotationCollector.emptyAnnotations();
        this._bindings = TypeBindings.emptyBindings();
        this._annotationIntrospector = null;
        this._mixInResolver = null;
        this._typeFactory = null;
    }
    
    @Deprecated
    public static AnnotatedClass construct(final JavaType type, final MapperConfig<?> config) {
        return construct(type, config, config);
    }
    
    @Deprecated
    public static AnnotatedClass construct(final JavaType type, final MapperConfig<?> config, final ClassIntrospector.MixInResolver mir) {
        return AnnotatedClassResolver.resolve(config, type, mir);
    }
    
    @Deprecated
    public static AnnotatedClass constructWithoutSuperTypes(final Class<?> raw, final MapperConfig<?> config) {
        return constructWithoutSuperTypes(raw, config, config);
    }
    
    @Deprecated
    public static AnnotatedClass constructWithoutSuperTypes(final Class<?> raw, final MapperConfig<?> config, final ClassIntrospector.MixInResolver mir) {
        return AnnotatedClassResolver.resolveWithoutSuperTypes(config, raw, mir);
    }
    
    @Override
    public JavaType resolveType(final Type type) {
        return this._typeFactory.constructType(type, this._bindings);
    }
    
    @Override
    public Class<?> getAnnotated() {
        return this._class;
    }
    
    public int getModifiers() {
        return this._class.getModifiers();
    }
    
    @Override
    public String getName() {
        return this._class.getName();
    }
    
    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> acls) {
        return this._classAnnotations.get(acls);
    }
    
    @Override
    public boolean hasAnnotation(final Class<?> acls) {
        return this._classAnnotations.has(acls);
    }
    
    @Override
    public boolean hasOneOf(final Class<? extends Annotation>[] annoClasses) {
        return this._classAnnotations.hasOneOf(annoClasses);
    }
    
    @Override
    public Class<?> getRawType() {
        return this._class;
    }
    
    @Deprecated
    @Override
    public Iterable<Annotation> annotations() {
        if (this._classAnnotations instanceof AnnotationMap) {
            return ((AnnotationMap)this._classAnnotations).annotations();
        }
        if (this._classAnnotations instanceof AnnotationCollector.OneAnnotation || this._classAnnotations instanceof AnnotationCollector.TwoAnnotations) {
            throw new UnsupportedOperationException("please use getAnnotations/ hasAnnotation to check for Annotations");
        }
        return (Iterable<Annotation>)Collections.emptyList();
    }
    
    @Override
    public JavaType getType() {
        return this._type;
    }
    
    public Annotations getAnnotations() {
        return this._classAnnotations;
    }
    
    public boolean hasAnnotations() {
        return this._classAnnotations.size() > 0;
    }
    
    public AnnotatedConstructor getDefaultConstructor() {
        return this._creators().defaultConstructor;
    }
    
    public List<AnnotatedConstructor> getConstructors() {
        return this._creators().constructors;
    }
    
    public List<AnnotatedMethod> getFactoryMethods() {
        return this._creators().creatorMethods;
    }
    
    @Deprecated
    public List<AnnotatedMethod> getStaticMethods() {
        return this.getFactoryMethods();
    }
    
    public Iterable<AnnotatedMethod> memberMethods() {
        return this._methods();
    }
    
    public int getMemberMethodCount() {
        return this._methods().size();
    }
    
    public AnnotatedMethod findMethod(final String name, final Class<?>[] paramTypes) {
        return this._methods().find(name, paramTypes);
    }
    
    public int getFieldCount() {
        return this._fields().size();
    }
    
    public Iterable<AnnotatedField> fields() {
        return this._fields();
    }
    
    public boolean isNonStaticInnerClass() {
        Boolean B = this._nonStaticInnerClass;
        if (B == null) {
            B = (this._nonStaticInnerClass = ClassUtil.isNonStaticInnerClass(this._class));
        }
        return B;
    }
    
    private final List<AnnotatedField> _fields() {
        List<AnnotatedField> f = this._fields;
        if (f == null) {
            if (this._type == null) {
                f = Collections.emptyList();
            }
            else {
                f = AnnotatedFieldCollector.collectFields(this._annotationIntrospector, this, this._mixInResolver, this._typeFactory, this._type);
            }
            this._fields = f;
        }
        return f;
    }
    
    private final AnnotatedMethodMap _methods() {
        AnnotatedMethodMap m = this._memberMethods;
        if (m == null) {
            if (this._type == null) {
                m = new AnnotatedMethodMap();
            }
            else {
                m = AnnotatedMethodCollector.collectMethods(this._annotationIntrospector, this, this._mixInResolver, this._typeFactory, this._type, this._superTypes, this._primaryMixIn);
            }
            this._memberMethods = m;
        }
        return m;
    }
    
    private final Creators _creators() {
        Creators c = this._creators;
        if (c == null) {
            if (this._type == null) {
                c = AnnotatedClass.NO_CREATORS;
            }
            else {
                c = AnnotatedCreatorCollector.collectCreators(this._annotationIntrospector, this, this._type, this._primaryMixIn);
            }
            this._creators = c;
        }
        return c;
    }
    
    @Override
    public String toString() {
        return "[AnnotedClass " + this._class.getName() + "]";
    }
    
    @Override
    public int hashCode() {
        return this._class.getName().hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (ClassUtil.hasClass(o, this.getClass()) && ((AnnotatedClass)o)._class == this._class);
    }
    
    static {
        NO_CREATORS = new Creators(null, Collections.emptyList(), Collections.emptyList());
    }
    
    public static final class Creators
    {
        public final AnnotatedConstructor defaultConstructor;
        public final List<AnnotatedConstructor> constructors;
        public final List<AnnotatedMethod> creatorMethods;
        
        public Creators(final AnnotatedConstructor defCtor, final List<AnnotatedConstructor> ctors, final List<AnnotatedMethod> ctorMethods) {
            this.defaultConstructor = defCtor;
            this.constructors = ctors;
            this.creatorMethods = ctorMethods;
        }
    }
}
