// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

import org.springframework.asm.Label;
import org.springframework.asm.Type;
import org.springframework.asm.MethodVisitor;
import java.util.Collections;
import org.apache.commons.logging.LogFactory;
import java.io.InputStream;
import java.io.IOException;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.ClassReader;
import org.springframework.util.ClassUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.Member;
import java.util.Map;
import org.apache.commons.logging.Log;

public class LocalVariableTableParameterNameDiscoverer implements ParameterNameDiscoverer
{
    private static Log logger;
    private static final Map<Member, String[]> NO_DEBUG_INFO_MAP;
    private final Map<Class<?>, Map<Member, String[]>> parameterNamesCache;
    
    public LocalVariableTableParameterNameDiscoverer() {
        this.parameterNamesCache = new ConcurrentHashMap<Class<?>, Map<Member, String[]>>(32);
    }
    
    @Override
    public String[] getParameterNames(final Method method) {
        final Method originalMethod = BridgeMethodResolver.findBridgedMethod(method);
        final Class<?> declaringClass = originalMethod.getDeclaringClass();
        Map<Member, String[]> map = this.parameterNamesCache.get(declaringClass);
        if (map == null) {
            map = this.inspectClass(declaringClass);
            this.parameterNamesCache.put(declaringClass, map);
        }
        if (map != LocalVariableTableParameterNameDiscoverer.NO_DEBUG_INFO_MAP) {
            return map.get(originalMethod);
        }
        return null;
    }
    
    @Override
    public String[] getParameterNames(final Constructor<?> ctor) {
        final Class<?> declaringClass = ctor.getDeclaringClass();
        Map<Member, String[]> map = this.parameterNamesCache.get(declaringClass);
        if (map == null) {
            map = this.inspectClass(declaringClass);
            this.parameterNamesCache.put(declaringClass, map);
        }
        if (map != LocalVariableTableParameterNameDiscoverer.NO_DEBUG_INFO_MAP) {
            return map.get(ctor);
        }
        return null;
    }
    
    private Map<Member, String[]> inspectClass(final Class<?> clazz) {
        final InputStream is = clazz.getResourceAsStream(ClassUtils.getClassFileName(clazz));
        if (is == null) {
            if (LocalVariableTableParameterNameDiscoverer.logger.isDebugEnabled()) {
                LocalVariableTableParameterNameDiscoverer.logger.debug("Cannot find '.class' file for class [" + clazz + "] - unable to determine constructors/methods parameter names");
            }
            return LocalVariableTableParameterNameDiscoverer.NO_DEBUG_INFO_MAP;
        }
        try {
            final ClassReader classReader = new ClassReader(is);
            final Map<Member, String[]> map = new ConcurrentHashMap<Member, String[]>(32);
            classReader.accept(new ParameterNameDiscoveringVisitor(clazz, map), 0);
            return map;
        }
        catch (IOException ex) {
            if (LocalVariableTableParameterNameDiscoverer.logger.isDebugEnabled()) {
                LocalVariableTableParameterNameDiscoverer.logger.debug("Exception thrown while reading '.class' file for class [" + clazz + "] - unable to determine constructors/methods parameter names", ex);
            }
        }
        catch (IllegalArgumentException ex2) {
            if (LocalVariableTableParameterNameDiscoverer.logger.isDebugEnabled()) {
                LocalVariableTableParameterNameDiscoverer.logger.debug("ASM ClassReader failed to parse class file [" + clazz + "], probably due to a new Java class file version that isn't supported yet " + "- unable to determine constructors/methods parameter names", ex2);
            }
        }
        finally {
            try {
                is.close();
            }
            catch (IOException ex3) {}
        }
        return LocalVariableTableParameterNameDiscoverer.NO_DEBUG_INFO_MAP;
    }
    
    static {
        LocalVariableTableParameterNameDiscoverer.logger = LogFactory.getLog(LocalVariableTableParameterNameDiscoverer.class);
        NO_DEBUG_INFO_MAP = Collections.emptyMap();
    }
    
    private static class ParameterNameDiscoveringVisitor extends ClassVisitor
    {
        private static final String STATIC_CLASS_INIT = "<clinit>";
        private final Class<?> clazz;
        private final Map<Member, String[]> memberMap;
        
        public ParameterNameDiscoveringVisitor(final Class<?> clazz, final Map<Member, String[]> memberMap) {
            super(262144);
            this.clazz = clazz;
            this.memberMap = memberMap;
        }
        
        @Override
        public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
            if (!isSyntheticOrBridged(access) && !"<clinit>".equals(name)) {
                return new LocalVariableTableVisitor(this.clazz, this.memberMap, name, desc, isStatic(access));
            }
            return null;
        }
        
        private static boolean isSyntheticOrBridged(final int access) {
            return ((access & 0x1000) | (access & 0x40)) > 0;
        }
        
        private static boolean isStatic(final int access) {
            return (access & 0x8) > 0;
        }
    }
    
    private static class LocalVariableTableVisitor extends MethodVisitor
    {
        private static final String CONSTRUCTOR = "<init>";
        private final Class<?> clazz;
        private final Map<Member, String[]> memberMap;
        private final String name;
        private final Type[] args;
        private final boolean isStatic;
        private String[] parameterNames;
        private boolean hasLvtInfo;
        private final int[] lvtSlotIndex;
        
        public LocalVariableTableVisitor(final Class<?> clazz, final Map<Member, String[]> map, final String name, final String desc, final boolean isStatic) {
            super(262144);
            this.hasLvtInfo = false;
            this.clazz = clazz;
            this.memberMap = map;
            this.name = name;
            this.args = Type.getArgumentTypes(desc);
            this.parameterNames = new String[this.args.length];
            this.isStatic = isStatic;
            this.lvtSlotIndex = computeLvtSlotIndices(isStatic, this.args);
        }
        
        @Override
        public void visitLocalVariable(final String name, final String description, final String signature, final Label start, final Label end, final int index) {
            this.hasLvtInfo = true;
            for (int i = 0; i < this.lvtSlotIndex.length; ++i) {
                if (this.lvtSlotIndex[i] == index) {
                    this.parameterNames[i] = name;
                }
            }
        }
        
        @Override
        public void visitEnd() {
            if (this.hasLvtInfo || (this.isStatic && this.parameterNames.length == 0)) {
                this.memberMap.put(this.resolveMember(), this.parameterNames);
            }
        }
        
        private Member resolveMember() {
            final ClassLoader loader = this.clazz.getClassLoader();
            final Class<?>[] classes = (Class<?>[])new Class[this.args.length];
            for (int i = 0; i < this.args.length; ++i) {
                classes[i] = ClassUtils.resolveClassName(this.args[i].getClassName(), loader);
            }
            try {
                if ("<init>".equals(this.name)) {
                    return this.clazz.getDeclaredConstructor(classes);
                }
                return this.clazz.getDeclaredMethod(this.name, classes);
            }
            catch (NoSuchMethodException ex) {
                throw new IllegalStateException("Method [" + this.name + "] was discovered in the .class file but cannot be resolved in the class object", ex);
            }
        }
        
        private static int[] computeLvtSlotIndices(final boolean isStatic, final Type[] paramTypes) {
            final int[] lvtIndex = new int[paramTypes.length];
            int nextIndex = isStatic ? 0 : 1;
            for (int i = 0; i < paramTypes.length; ++i) {
                lvtIndex[i] = nextIndex;
                if (isWideType(paramTypes[i])) {
                    nextIndex += 2;
                }
                else {
                    ++nextIndex;
                }
            }
            return lvtIndex;
        }
        
        private static boolean isWideType(final Type aType) {
            return aType == Type.LONG_TYPE || aType == Type.DOUBLE_TYPE;
        }
    }
}
