// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.core;

import java.util.WeakHashMap;
import java.lang.ref.WeakReference;
import org.springframework.asm.ClassReader;
import java.lang.ref.Reference;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class AbstractClassGenerator implements ClassGenerator
{
    private static final Object NAME_KEY;
    private static final ThreadLocal CURRENT;
    private GeneratorStrategy strategy;
    private NamingPolicy namingPolicy;
    private Source source;
    private ClassLoader classLoader;
    private String namePrefix;
    private Object key;
    private boolean useCache;
    private String className;
    private boolean attemptLoad;
    
    protected AbstractClassGenerator(final Source source) {
        this.strategy = DefaultGeneratorStrategy.INSTANCE;
        this.namingPolicy = DefaultNamingPolicy.INSTANCE;
        this.useCache = true;
        this.source = source;
    }
    
    protected void setNamePrefix(final String namePrefix) {
        this.namePrefix = namePrefix;
    }
    
    protected final String getClassName() {
        if (this.className == null) {
            this.className = this.getClassName(this.getClassLoader());
        }
        return this.className;
    }
    
    private String getClassName(final ClassLoader loader) {
        final Set nameCache = this.getClassNameCache(loader);
        return this.namingPolicy.getClassName(this.namePrefix, this.source.name, this.key, new Predicate() {
            public boolean evaluate(final Object arg) {
                return nameCache.contains(arg);
            }
        });
    }
    
    private Set getClassNameCache(final ClassLoader loader) {
        return this.source.cache.get(loader).get(AbstractClassGenerator.NAME_KEY);
    }
    
    public void setClassLoader(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    public void setNamingPolicy(NamingPolicy namingPolicy) {
        if (namingPolicy == null) {
            namingPolicy = DefaultNamingPolicy.INSTANCE;
        }
        this.namingPolicy = namingPolicy;
    }
    
    public NamingPolicy getNamingPolicy() {
        return this.namingPolicy;
    }
    
    public void setUseCache(final boolean useCache) {
        this.useCache = useCache;
    }
    
    public boolean getUseCache() {
        return this.useCache;
    }
    
    public void setAttemptLoad(final boolean attemptLoad) {
        this.attemptLoad = attemptLoad;
    }
    
    public boolean getAttemptLoad() {
        return this.attemptLoad;
    }
    
    public void setStrategy(GeneratorStrategy strategy) {
        if (strategy == null) {
            strategy = DefaultGeneratorStrategy.INSTANCE;
        }
        this.strategy = strategy;
    }
    
    public GeneratorStrategy getStrategy() {
        return this.strategy;
    }
    
    public static AbstractClassGenerator getCurrent() {
        return AbstractClassGenerator.CURRENT.get();
    }
    
    public ClassLoader getClassLoader() {
        ClassLoader t = this.classLoader;
        if (t == null) {
            t = this.getDefaultClassLoader();
        }
        if (t == null) {
            t = this.getClass().getClassLoader();
        }
        if (t == null) {
            t = Thread.currentThread().getContextClassLoader();
        }
        if (t == null) {
            throw new IllegalStateException("Cannot determine classloader");
        }
        return t;
    }
    
    protected abstract ClassLoader getDefaultClassLoader();
    
    protected Object create(final Object key) {
        try {
            Class gen = null;
            synchronized (this.source) {
                final ClassLoader loader = this.getClassLoader();
                Map cache2 = null;
                cache2 = this.source.cache.get(loader);
                if (cache2 == null) {
                    cache2 = new HashMap();
                    cache2.put(AbstractClassGenerator.NAME_KEY, new HashSet());
                    this.source.cache.put(loader, cache2);
                }
                else if (this.useCache) {
                    final Reference ref = cache2.get(key);
                    gen = ((ref == null) ? null : ref.get());
                }
                if (gen == null) {
                    final Object save = AbstractClassGenerator.CURRENT.get();
                    AbstractClassGenerator.CURRENT.set(this);
                    try {
                        this.key = key;
                        if (this.attemptLoad) {
                            try {
                                gen = loader.loadClass(this.getClassName());
                            }
                            catch (ClassNotFoundException ex) {}
                        }
                        if (gen == null) {
                            final byte[] b = this.strategy.generate(this);
                            final String className = ClassNameReader.getClassName(new ClassReader(b));
                            this.getClassNameCache(loader).add(className);
                            gen = ReflectUtils.defineClass(className, b, loader);
                        }
                        if (this.useCache) {
                            cache2.put(key, new WeakReference<Class>(gen));
                        }
                        return this.firstInstance(gen);
                    }
                    finally {
                        AbstractClassGenerator.CURRENT.set(save);
                    }
                }
            }
            return this.firstInstance(gen);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Error e2) {
            throw e2;
        }
        catch (Exception e3) {
            throw new CodeGenerationException(e3);
        }
    }
    
    protected abstract Object firstInstance(final Class p0) throws Exception;
    
    protected abstract Object nextInstance(final Object p0) throws Exception;
    
    static {
        NAME_KEY = new Object();
        CURRENT = new ThreadLocal();
    }
    
    protected static class Source
    {
        String name;
        Map cache;
        
        public Source(final String name) {
            this.cache = new WeakHashMap();
            this.name = name;
        }
    }
}
