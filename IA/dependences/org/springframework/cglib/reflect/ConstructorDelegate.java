// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.reflect;

import org.springframework.cglib.core.TypeUtils;
import org.springframework.cglib.core.CodeEmitter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Member;
import org.springframework.cglib.core.EmitUtils;
import org.springframework.cglib.core.ClassEmitter;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Type;
import org.springframework.cglib.core.AbstractClassGenerator;
import org.springframework.cglib.core.KeyFactory;

public abstract class ConstructorDelegate
{
    private static final ConstructorKey KEY_FACTORY;
    
    protected ConstructorDelegate() {
    }
    
    public static ConstructorDelegate create(final Class targetClass, final Class iface) {
        final Generator gen = new Generator();
        gen.setTargetClass(targetClass);
        gen.setInterface(iface);
        return gen.create();
    }
    
    static {
        KEY_FACTORY = (ConstructorKey)KeyFactory.create(ConstructorKey.class, KeyFactory.CLASS_BY_NAME);
    }
    
    public static class Generator extends AbstractClassGenerator
    {
        private static final Source SOURCE;
        private static final Type CONSTRUCTOR_DELEGATE;
        private Class iface;
        private Class targetClass;
        
        public Generator() {
            super(Generator.SOURCE);
        }
        
        public void setInterface(final Class iface) {
            this.iface = iface;
        }
        
        public void setTargetClass(final Class targetClass) {
            this.targetClass = targetClass;
        }
        
        public ConstructorDelegate create() {
            this.setNamePrefix(this.targetClass.getName());
            final Object key = ConstructorDelegate.KEY_FACTORY.newInstance(this.iface.getName(), this.targetClass.getName());
            return (ConstructorDelegate)super.create(key);
        }
        
        protected ClassLoader getDefaultClassLoader() {
            return this.targetClass.getClassLoader();
        }
        
        public void generateClass(final ClassVisitor v) {
            this.setNamePrefix(this.targetClass.getName());
            final Method newInstance = ReflectUtils.findNewInstance(this.iface);
            if (!newInstance.getReturnType().isAssignableFrom(this.targetClass)) {
                throw new IllegalArgumentException("incompatible return type");
            }
            Constructor constructor;
            try {
                constructor = this.targetClass.getDeclaredConstructor((Class[])newInstance.getParameterTypes());
            }
            catch (NoSuchMethodException e2) {
                throw new IllegalArgumentException("interface does not match any known constructor");
            }
            final ClassEmitter ce = new ClassEmitter(v);
            ce.begin_class(46, 1, this.getClassName(), Generator.CONSTRUCTOR_DELEGATE, new Type[] { Type.getType(this.iface) }, "<generated>");
            final Type declaring = Type.getType(constructor.getDeclaringClass());
            EmitUtils.null_constructor(ce);
            final CodeEmitter e = ce.begin_method(1, ReflectUtils.getSignature(newInstance), ReflectUtils.getExceptionTypes(newInstance));
            e.new_instance(declaring);
            e.dup();
            e.load_args();
            e.invoke_constructor(declaring, ReflectUtils.getSignature(constructor));
            e.return_value();
            e.end_method();
            ce.end_class();
        }
        
        protected Object firstInstance(final Class type) {
            return ReflectUtils.newInstance(type);
        }
        
        protected Object nextInstance(final Object instance) {
            return instance;
        }
        
        static {
            SOURCE = new Source(ConstructorDelegate.class.getName());
            CONSTRUCTOR_DELEGATE = TypeUtils.parseType("org.springframework.cglib.reflect.ConstructorDelegate");
        }
    }
    
    interface ConstructorKey
    {
        Object newInstance(final String p0, final String p1);
    }
}
