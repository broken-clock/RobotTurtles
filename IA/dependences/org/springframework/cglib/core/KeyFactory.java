// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.core;

import org.springframework.asm.Label;
import java.lang.reflect.Method;
import java.lang.reflect.Member;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Type;

public abstract class KeyFactory
{
    private static final Signature GET_NAME;
    private static final Signature GET_CLASS;
    private static final Signature HASH_CODE;
    private static final Signature EQUALS;
    private static final Signature TO_STRING;
    private static final Signature APPEND_STRING;
    private static final Type KEY_FACTORY;
    private static final int[] PRIMES;
    public static final Customizer CLASS_BY_NAME;
    public static final Customizer OBJECT_BY_CLASS;
    
    protected KeyFactory() {
    }
    
    public static KeyFactory create(final Class keyInterface) {
        return create(keyInterface, null);
    }
    
    public static KeyFactory create(final Class keyInterface, final Customizer customizer) {
        return create(keyInterface.getClassLoader(), keyInterface, customizer);
    }
    
    public static KeyFactory create(final ClassLoader loader, final Class keyInterface, final Customizer customizer) {
        final Generator gen = new Generator();
        gen.setInterface(keyInterface);
        gen.setCustomizer(customizer);
        gen.setClassLoader(loader);
        return gen.create();
    }
    
    static {
        GET_NAME = TypeUtils.parseSignature("String getName()");
        GET_CLASS = TypeUtils.parseSignature("Class getClass()");
        HASH_CODE = TypeUtils.parseSignature("int hashCode()");
        EQUALS = TypeUtils.parseSignature("boolean equals(Object)");
        TO_STRING = TypeUtils.parseSignature("String toString()");
        APPEND_STRING = TypeUtils.parseSignature("StringBuffer append(String)");
        KEY_FACTORY = TypeUtils.parseType("org.springframework.cglib.core.KeyFactory");
        PRIMES = new int[] { 11, 73, 179, 331, 521, 787, 1213, 1823, 2609, 3691, 5189, 7247, 10037, 13931, 19289, 26627, 36683, 50441, 69403, 95401, 131129, 180179, 247501, 340057, 467063, 641371, 880603, 1209107, 1660097, 2279161, 3129011, 4295723, 5897291, 8095873, 11114263, 15257791, 20946017, 28754629, 39474179, 54189869, 74391461, 102123817, 140194277, 192456917, 264202273, 362693231, 497900099, 683510293, 938313161, 1288102441, 1768288259 };
        CLASS_BY_NAME = new Customizer() {
            public void customize(final CodeEmitter e, final Type type) {
                if (type.equals(Constants.TYPE_CLASS)) {
                    e.invoke_virtual(Constants.TYPE_CLASS, KeyFactory.GET_NAME);
                }
            }
        };
        OBJECT_BY_CLASS = new Customizer() {
            public void customize(final CodeEmitter e, final Type type) {
                e.invoke_virtual(Constants.TYPE_OBJECT, KeyFactory.GET_CLASS);
            }
        };
    }
    
    public static class Generator extends AbstractClassGenerator
    {
        private static final Source SOURCE;
        private Class keyInterface;
        private Customizer customizer;
        private int constant;
        private int multiplier;
        
        public Generator() {
            super(Generator.SOURCE);
        }
        
        protected ClassLoader getDefaultClassLoader() {
            return this.keyInterface.getClassLoader();
        }
        
        public void setCustomizer(final Customizer customizer) {
            this.customizer = customizer;
        }
        
        public void setInterface(final Class keyInterface) {
            this.keyInterface = keyInterface;
        }
        
        public KeyFactory create() {
            this.setNamePrefix(this.keyInterface.getName());
            return (KeyFactory)super.create(this.keyInterface.getName());
        }
        
        public void setHashConstant(final int constant) {
            this.constant = constant;
        }
        
        public void setHashMultiplier(final int multiplier) {
            this.multiplier = multiplier;
        }
        
        protected Object firstInstance(final Class type) {
            return ReflectUtils.newInstance(type);
        }
        
        protected Object nextInstance(final Object instance) {
            return instance;
        }
        
        public void generateClass(final ClassVisitor v) {
            final ClassEmitter ce = new ClassEmitter(v);
            final Method newInstance = ReflectUtils.findNewInstance(this.keyInterface);
            if (!newInstance.getReturnType().equals(Object.class)) {
                throw new IllegalArgumentException("newInstance method must return Object");
            }
            final Type[] parameterTypes = TypeUtils.getTypes(newInstance.getParameterTypes());
            ce.begin_class(46, 1, this.getClassName(), KeyFactory.KEY_FACTORY, new Type[] { Type.getType(this.keyInterface) }, "<generated>");
            EmitUtils.null_constructor(ce);
            EmitUtils.factory_method(ce, ReflectUtils.getSignature(newInstance));
            int seed = 0;
            CodeEmitter e = ce.begin_method(1, TypeUtils.parseConstructor(parameterTypes), null);
            e.load_this();
            e.super_invoke_constructor();
            e.load_this();
            for (int i = 0; i < parameterTypes.length; ++i) {
                seed += parameterTypes[i].hashCode();
                ce.declare_field(18, this.getFieldName(i), parameterTypes[i], null);
                e.dup();
                e.load_arg(i);
                e.putfield(this.getFieldName(i));
            }
            e.return_value();
            e.end_method();
            e = ce.begin_method(1, KeyFactory.HASH_CODE, null);
            final int hc = (this.constant != 0) ? this.constant : KeyFactory.PRIMES[Math.abs(seed) % KeyFactory.PRIMES.length];
            final int hm = (this.multiplier != 0) ? this.multiplier : KeyFactory.PRIMES[Math.abs(seed * 13) % KeyFactory.PRIMES.length];
            e.push(hc);
            for (int j = 0; j < parameterTypes.length; ++j) {
                e.load_this();
                e.getfield(this.getFieldName(j));
                EmitUtils.hash_code(e, parameterTypes[j], hm, this.customizer);
            }
            e.return_value();
            e.end_method();
            e = ce.begin_method(1, KeyFactory.EQUALS, null);
            final Label fail = e.make_label();
            e.load_arg(0);
            e.instance_of_this();
            e.if_jump(153, fail);
            for (int k = 0; k < parameterTypes.length; ++k) {
                e.load_this();
                e.getfield(this.getFieldName(k));
                e.load_arg(0);
                e.checkcast_this();
                e.getfield(this.getFieldName(k));
                EmitUtils.not_equals(e, parameterTypes[k], fail, this.customizer);
            }
            e.push(1);
            e.return_value();
            e.mark(fail);
            e.push(0);
            e.return_value();
            e.end_method();
            e = ce.begin_method(1, KeyFactory.TO_STRING, null);
            e.new_instance(Constants.TYPE_STRING_BUFFER);
            e.dup();
            e.invoke_constructor(Constants.TYPE_STRING_BUFFER);
            for (int k = 0; k < parameterTypes.length; ++k) {
                if (k > 0) {
                    e.push(", ");
                    e.invoke_virtual(Constants.TYPE_STRING_BUFFER, KeyFactory.APPEND_STRING);
                }
                e.load_this();
                e.getfield(this.getFieldName(k));
                EmitUtils.append_string(e, parameterTypes[k], EmitUtils.DEFAULT_DELIMITERS, this.customizer);
            }
            e.invoke_virtual(Constants.TYPE_STRING_BUFFER, KeyFactory.TO_STRING);
            e.return_value();
            e.end_method();
            ce.end_class();
        }
        
        private String getFieldName(final int arg) {
            return "FIELD_" + arg;
        }
        
        static {
            SOURCE = new Source(KeyFactory.class.getName());
        }
    }
}
