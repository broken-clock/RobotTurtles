// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.instrument.classloading;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import org.springframework.instrument.InstrumentationSavingAgent;
import org.springframework.util.Assert;
import java.util.ArrayList;
import org.springframework.util.ClassUtils;
import java.lang.instrument.ClassFileTransformer;
import java.util.List;
import java.lang.instrument.Instrumentation;

public class InstrumentationLoadTimeWeaver implements LoadTimeWeaver
{
    private static final boolean AGENT_CLASS_PRESENT;
    private final ClassLoader classLoader;
    private final Instrumentation instrumentation;
    private final List<ClassFileTransformer> transformers;
    
    public InstrumentationLoadTimeWeaver() {
        this(ClassUtils.getDefaultClassLoader());
    }
    
    public InstrumentationLoadTimeWeaver(final ClassLoader classLoader) {
        this.transformers = new ArrayList<ClassFileTransformer>(4);
        Assert.notNull(classLoader, "ClassLoader must not be null");
        this.classLoader = classLoader;
        this.instrumentation = getInstrumentation();
    }
    
    @Override
    public void addTransformer(final ClassFileTransformer transformer) {
        Assert.notNull(transformer, "Transformer must not be null");
        final FilteringClassFileTransformer actualTransformer = new FilteringClassFileTransformer(transformer, this.classLoader);
        synchronized (this.transformers) {
            if (this.instrumentation == null) {
                throw new IllegalStateException("Must start with Java agent to use InstrumentationLoadTimeWeaver. See Spring documentation.");
            }
            this.instrumentation.addTransformer(actualTransformer);
            this.transformers.add(actualTransformer);
        }
    }
    
    @Override
    public ClassLoader getInstrumentableClassLoader() {
        return this.classLoader;
    }
    
    @Override
    public ClassLoader getThrowawayClassLoader() {
        return new SimpleThrowawayClassLoader(this.getInstrumentableClassLoader());
    }
    
    public void removeTransformers() {
        synchronized (this.transformers) {
            if (!this.transformers.isEmpty()) {
                for (int i = this.transformers.size() - 1; i >= 0; --i) {
                    this.instrumentation.removeTransformer(this.transformers.get(i));
                }
                this.transformers.clear();
            }
        }
    }
    
    public static boolean isInstrumentationAvailable() {
        return getInstrumentation() != null;
    }
    
    private static Instrumentation getInstrumentation() {
        if (InstrumentationLoadTimeWeaver.AGENT_CLASS_PRESENT) {
            return InstrumentationAccessor.getInstrumentation();
        }
        return null;
    }
    
    static {
        AGENT_CLASS_PRESENT = ClassUtils.isPresent("org.springframework.instrument.InstrumentationSavingAgent", InstrumentationLoadTimeWeaver.class.getClassLoader());
    }
    
    private static class InstrumentationAccessor
    {
        public static Instrumentation getInstrumentation() {
            return InstrumentationSavingAgent.getInstrumentation();
        }
    }
    
    private static class FilteringClassFileTransformer implements ClassFileTransformer
    {
        private final ClassFileTransformer targetTransformer;
        private final ClassLoader targetClassLoader;
        
        public FilteringClassFileTransformer(final ClassFileTransformer targetTransformer, final ClassLoader targetClassLoader) {
            this.targetTransformer = targetTransformer;
            this.targetClassLoader = targetClassLoader;
        }
        
        @Override
        public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer) throws IllegalClassFormatException {
            if (!this.targetClassLoader.equals(loader)) {
                return null;
            }
            return this.targetTransformer.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
        }
        
        @Override
        public String toString() {
            return "FilteringClassFileTransformer for: " + this.targetTransformer.toString();
        }
    }
}
