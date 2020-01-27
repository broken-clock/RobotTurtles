// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import java.util.Collection;
import org.springframework.util.StringUtils;
import java.util.Iterator;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCreationNotAllowedException;
import org.springframework.util.Assert;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.LogFactory;
import java.util.Set;
import org.springframework.beans.factory.ObjectFactory;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.core.SimpleAliasRegistry;

public class DefaultSingletonBeanRegistry extends SimpleAliasRegistry implements SingletonBeanRegistry
{
    protected static final Object NULL_OBJECT;
    protected final Log logger;
    private final Map<String, Object> singletonObjects;
    private final Map<String, ObjectFactory<?>> singletonFactories;
    private final Map<String, Object> earlySingletonObjects;
    private final Set<String> registeredSingletons;
    private final Set<String> singletonsCurrentlyInCreation;
    private final Set<String> inCreationCheckExclusions;
    private Set<Exception> suppressedExceptions;
    private boolean singletonsCurrentlyInDestruction;
    private final Map<String, Object> disposableBeans;
    private final Map<String, Set<String>> containedBeanMap;
    private final Map<String, Set<String>> dependentBeanMap;
    private final Map<String, Set<String>> dependenciesForBeanMap;
    
    public DefaultSingletonBeanRegistry() {
        this.logger = LogFactory.getLog(this.getClass());
        this.singletonObjects = new ConcurrentHashMap<String, Object>(64);
        this.singletonFactories = new HashMap<String, ObjectFactory<?>>(16);
        this.earlySingletonObjects = new HashMap<String, Object>(16);
        this.registeredSingletons = new LinkedHashSet<String>(64);
        this.singletonsCurrentlyInCreation = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>(16));
        this.inCreationCheckExclusions = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>(16));
        this.singletonsCurrentlyInDestruction = false;
        this.disposableBeans = new LinkedHashMap<String, Object>();
        this.containedBeanMap = new ConcurrentHashMap<String, Set<String>>(16);
        this.dependentBeanMap = new ConcurrentHashMap<String, Set<String>>(64);
        this.dependenciesForBeanMap = new ConcurrentHashMap<String, Set<String>>(64);
    }
    
    @Override
    public void registerSingleton(final String beanName, final Object singletonObject) throws IllegalStateException {
        Assert.notNull(beanName, "'beanName' must not be null");
        synchronized (this.singletonObjects) {
            final Object oldObject = this.singletonObjects.get(beanName);
            if (oldObject != null) {
                throw new IllegalStateException("Could not register object [" + singletonObject + "] under bean name '" + beanName + "': there is already object [" + oldObject + "] bound");
            }
            this.addSingleton(beanName, singletonObject);
        }
    }
    
    protected void addSingleton(final String beanName, final Object singletonObject) {
        synchronized (this.singletonObjects) {
            this.singletonObjects.put(beanName, (singletonObject != null) ? singletonObject : DefaultSingletonBeanRegistry.NULL_OBJECT);
            this.singletonFactories.remove(beanName);
            this.earlySingletonObjects.remove(beanName);
            this.registeredSingletons.add(beanName);
        }
    }
    
    protected void addSingletonFactory(final String beanName, final ObjectFactory<?> singletonFactory) {
        Assert.notNull(singletonFactory, "Singleton factory must not be null");
        synchronized (this.singletonObjects) {
            if (!this.singletonObjects.containsKey(beanName)) {
                this.singletonFactories.put(beanName, singletonFactory);
                this.earlySingletonObjects.remove(beanName);
                this.registeredSingletons.add(beanName);
            }
        }
    }
    
    @Override
    public Object getSingleton(final String beanName) {
        return this.getSingleton(beanName, true);
    }
    
    protected Object getSingleton(final String beanName, final boolean allowEarlyReference) {
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject == null && this.isSingletonCurrentlyInCreation(beanName)) {
            synchronized (this.singletonObjects) {
                singletonObject = this.earlySingletonObjects.get(beanName);
                if (singletonObject == null && allowEarlyReference) {
                    final ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
                    if (singletonFactory != null) {
                        singletonObject = singletonFactory.getObject();
                        this.earlySingletonObjects.put(beanName, singletonObject);
                        this.singletonFactories.remove(beanName);
                    }
                }
            }
        }
        return (singletonObject != DefaultSingletonBeanRegistry.NULL_OBJECT) ? singletonObject : null;
    }
    
    public Object getSingleton(final String beanName, final ObjectFactory<?> singletonFactory) {
        Assert.notNull(beanName, "'beanName' must not be null");
        synchronized (this.singletonObjects) {
            Object singletonObject = this.singletonObjects.get(beanName);
            if (singletonObject == null) {
                if (this.singletonsCurrentlyInDestruction) {
                    throw new BeanCreationNotAllowedException(beanName, "Singleton bean creation not allowed while the singletons of this factory are in destruction (Do not request a bean from a BeanFactory in a destroy method implementation!)");
                }
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Creating shared instance of singleton bean '" + beanName + "'");
                }
                this.beforeSingletonCreation(beanName);
                final boolean recordSuppressedExceptions = this.suppressedExceptions == null;
                if (recordSuppressedExceptions) {
                    this.suppressedExceptions = new LinkedHashSet<Exception>();
                }
                try {
                    singletonObject = singletonFactory.getObject();
                }
                catch (BeanCreationException ex) {
                    if (recordSuppressedExceptions) {
                        for (final Exception suppressedException : this.suppressedExceptions) {
                            ex.addRelatedCause(suppressedException);
                        }
                    }
                    throw ex;
                }
                finally {
                    if (recordSuppressedExceptions) {
                        this.suppressedExceptions = null;
                    }
                    this.afterSingletonCreation(beanName);
                }
                this.addSingleton(beanName, singletonObject);
            }
            return (singletonObject != DefaultSingletonBeanRegistry.NULL_OBJECT) ? singletonObject : null;
        }
    }
    
    protected void onSuppressedException(final Exception ex) {
        synchronized (this.singletonObjects) {
            if (this.suppressedExceptions != null) {
                this.suppressedExceptions.add(ex);
            }
        }
    }
    
    protected void removeSingleton(final String beanName) {
        synchronized (this.singletonObjects) {
            this.singletonObjects.remove(beanName);
            this.singletonFactories.remove(beanName);
            this.earlySingletonObjects.remove(beanName);
            this.registeredSingletons.remove(beanName);
        }
    }
    
    @Override
    public boolean containsSingleton(final String beanName) {
        return this.singletonObjects.containsKey(beanName);
    }
    
    @Override
    public String[] getSingletonNames() {
        synchronized (this.singletonObjects) {
            return StringUtils.toStringArray(this.registeredSingletons);
        }
    }
    
    @Override
    public int getSingletonCount() {
        synchronized (this.singletonObjects) {
            return this.registeredSingletons.size();
        }
    }
    
    public void setCurrentlyInCreation(final String beanName, final boolean inCreation) {
        Assert.notNull(beanName, "Bean name must not be null");
        if (!inCreation) {
            this.inCreationCheckExclusions.add(beanName);
        }
        else {
            this.inCreationCheckExclusions.remove(beanName);
        }
    }
    
    public boolean isCurrentlyInCreation(final String beanName) {
        Assert.notNull(beanName, "Bean name must not be null");
        return !this.inCreationCheckExclusions.contains(beanName) && this.isActuallyInCreation(beanName);
    }
    
    protected boolean isActuallyInCreation(final String beanName) {
        return this.isSingletonCurrentlyInCreation(beanName);
    }
    
    public boolean isSingletonCurrentlyInCreation(final String beanName) {
        return this.singletonsCurrentlyInCreation.contains(beanName);
    }
    
    protected void beforeSingletonCreation(final String beanName) {
        if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.add(beanName)) {
            throw new BeanCurrentlyInCreationException(beanName);
        }
    }
    
    protected void afterSingletonCreation(final String beanName) {
        if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.remove(beanName)) {
            throw new IllegalStateException("Singleton '" + beanName + "' isn't currently in creation");
        }
    }
    
    public void registerDisposableBean(final String beanName, final DisposableBean bean) {
        synchronized (this.disposableBeans) {
            this.disposableBeans.put(beanName, bean);
        }
    }
    
    public void registerContainedBean(final String containedBeanName, final String containingBeanName) {
        synchronized (this.containedBeanMap) {
            Set<String> containedBeans = this.containedBeanMap.get(containingBeanName);
            if (containedBeans == null) {
                containedBeans = new LinkedHashSet<String>(8);
                this.containedBeanMap.put(containingBeanName, containedBeans);
            }
            containedBeans.add(containedBeanName);
        }
        this.registerDependentBean(containedBeanName, containingBeanName);
    }
    
    public void registerDependentBean(final String beanName, final String dependentBeanName) {
        final String canonicalName = this.canonicalName(beanName);
        synchronized (this.dependentBeanMap) {
            Set<String> dependentBeans = this.dependentBeanMap.get(canonicalName);
            if (dependentBeans == null) {
                dependentBeans = new LinkedHashSet<String>(8);
                this.dependentBeanMap.put(canonicalName, dependentBeans);
            }
            dependentBeans.add(dependentBeanName);
        }
        synchronized (this.dependenciesForBeanMap) {
            Set<String> dependenciesForBean = this.dependenciesForBeanMap.get(dependentBeanName);
            if (dependenciesForBean == null) {
                dependenciesForBean = new LinkedHashSet<String>(8);
                this.dependenciesForBeanMap.put(dependentBeanName, dependenciesForBean);
            }
            dependenciesForBean.add(canonicalName);
        }
    }
    
    protected boolean isDependent(final String beanName, final String dependentBeanName) {
        final Set<String> dependentBeans = this.dependentBeanMap.get(beanName);
        if (dependentBeans == null) {
            return false;
        }
        if (dependentBeans.contains(dependentBeanName)) {
            return true;
        }
        for (final String transitiveDependency : dependentBeans) {
            if (this.isDependent(transitiveDependency, dependentBeanName)) {
                return true;
            }
        }
        return false;
    }
    
    protected boolean hasDependentBean(final String beanName) {
        return this.dependentBeanMap.containsKey(beanName);
    }
    
    public String[] getDependentBeans(final String beanName) {
        final Set<String> dependentBeans = this.dependentBeanMap.get(beanName);
        if (dependentBeans == null) {
            return new String[0];
        }
        return StringUtils.toStringArray(dependentBeans);
    }
    
    public String[] getDependenciesForBean(final String beanName) {
        final Set<String> dependenciesForBean = this.dependenciesForBeanMap.get(beanName);
        if (dependenciesForBean == null) {
            return new String[0];
        }
        return dependenciesForBean.toArray(new String[dependenciesForBean.size()]);
    }
    
    public void destroySingletons() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        org/springframework/beans/factory/support/DefaultSingletonBeanRegistry.logger:Lorg/apache/commons/logging/Log;
        //     4: invokeinterface org/apache/commons/logging/Log.isDebugEnabled:()Z
        //     9: ifeq            40
        //    12: aload_0         /* this */
        //    13: getfield        org/springframework/beans/factory/support/DefaultSingletonBeanRegistry.logger:Lorg/apache/commons/logging/Log;
        //    16: new             Ljava/lang/StringBuilder;
        //    19: dup            
        //    20: invokespecial   java/lang/StringBuilder.<init>:()V
        //    23: ldc             "Destroying singletons in "
        //    25: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    28: aload_0         /* this */
        //    29: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //    32: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //    35: invokeinterface org/apache/commons/logging/Log.debug:(Ljava/lang/Object;)V
        //    40: aload_0         /* this */
        //    41: getfield        org/springframework/beans/factory/support/DefaultSingletonBeanRegistry.singletonObjects:Ljava/util/Map;
        //    44: dup            
        //    45: astore_1       
        //    46: monitorenter   
        //    47: aload_0         /* this */
        //    48: iconst_1       
        //    49: putfield        org/springframework/beans/factory/support/DefaultSingletonBeanRegistry.singletonsCurrentlyInDestruction:Z
        //    52: aload_1        
        //    53: monitorexit    
        //    54: goto            62
        //    57: astore_2       
        //    58: aload_1        
        //    59: monitorexit    
        //    60: aload_2        
        //    61: athrow         
        //    62: aload_0         /* this */
        //    63: getfield        org/springframework/beans/factory/support/DefaultSingletonBeanRegistry.disposableBeans:Ljava/util/Map;
        //    66: dup            
        //    67: astore_2       
        //    68: monitorenter   
        //    69: aload_0         /* this */
        //    70: getfield        org/springframework/beans/factory/support/DefaultSingletonBeanRegistry.disposableBeans:Ljava/util/Map;
        //    73: invokeinterface java/util/Map.keySet:()Ljava/util/Set;
        //    78: invokestatic    org/springframework/util/StringUtils.toStringArray:(Ljava/util/Collection;)[Ljava/lang/String;
        //    81: astore_1        /* disposableBeanNames */
        //    82: aload_2        
        //    83: monitorexit    
        //    84: goto            92
        //    87: astore_3       
        //    88: aload_2        
        //    89: monitorexit    
        //    90: aload_3        
        //    91: athrow         
        //    92: aload_1         /* disposableBeanNames */
        //    93: arraylength    
        //    94: iconst_1       
        //    95: isub           
        //    96: istore_2        /* i */
        //    97: iload_2         /* i */
        //    98: iflt            114
        //   101: aload_0         /* this */
        //   102: aload_1         /* disposableBeanNames */
        //   103: iload_2         /* i */
        //   104: aaload         
        //   105: invokevirtual   org/springframework/beans/factory/support/DefaultSingletonBeanRegistry.destroySingleton:(Ljava/lang/String;)V
        //   108: iinc            i, -1
        //   111: goto            97
        //   114: aload_0         /* this */
        //   115: getfield        org/springframework/beans/factory/support/DefaultSingletonBeanRegistry.containedBeanMap:Ljava/util/Map;
        //   118: invokeinterface java/util/Map.clear:()V
        //   123: aload_0         /* this */
        //   124: getfield        org/springframework/beans/factory/support/DefaultSingletonBeanRegistry.dependentBeanMap:Ljava/util/Map;
        //   127: invokeinterface java/util/Map.clear:()V
        //   132: aload_0         /* this */
        //   133: getfield        org/springframework/beans/factory/support/DefaultSingletonBeanRegistry.dependenciesForBeanMap:Ljava/util/Map;
        //   136: invokeinterface java/util/Map.clear:()V
        //   141: aload_0         /* this */
        //   142: getfield        org/springframework/beans/factory/support/DefaultSingletonBeanRegistry.singletonObjects:Ljava/util/Map;
        //   145: dup            
        //   146: astore_2       
        //   147: monitorenter   
        //   148: aload_0         /* this */
        //   149: getfield        org/springframework/beans/factory/support/DefaultSingletonBeanRegistry.singletonObjects:Ljava/util/Map;
        //   152: invokeinterface java/util/Map.clear:()V
        //   157: aload_0         /* this */
        //   158: getfield        org/springframework/beans/factory/support/DefaultSingletonBeanRegistry.singletonFactories:Ljava/util/Map;
        //   161: invokeinterface java/util/Map.clear:()V
        //   166: aload_0         /* this */
        //   167: getfield        org/springframework/beans/factory/support/DefaultSingletonBeanRegistry.earlySingletonObjects:Ljava/util/Map;
        //   170: invokeinterface java/util/Map.clear:()V
        //   175: aload_0         /* this */
        //   176: getfield        org/springframework/beans/factory/support/DefaultSingletonBeanRegistry.registeredSingletons:Ljava/util/Set;
        //   179: invokeinterface java/util/Set.clear:()V
        //   184: aload_0         /* this */
        //   185: iconst_0       
        //   186: putfield        org/springframework/beans/factory/support/DefaultSingletonBeanRegistry.singletonsCurrentlyInDestruction:Z
        //   189: aload_2        
        //   190: monitorexit    
        //   191: goto            201
        //   194: astore          4
        //   196: aload_2        
        //   197: monitorexit    
        //   198: aload           4
        //   200: athrow         
        //   201: return         
        //    StackMapTable: 00 09 28 FF 00 10 00 02 07 00 95 07 00 94 00 01 07 00 97 FA 00 04 FF 00 18 00 03 07 00 95 00 07 00 94 00 01 07 00 97 FF 00 04 00 02 07 00 95 07 00 53 00 00 FC 00 04 01 FA 00 10 FF 00 4F 00 03 07 00 95 07 00 53 07 00 94 00 01 07 00 97 FA 00 06
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  47     54     57     62     Any
        //  57     60     57     62     Any
        //  69     84     87     92     Any
        //  87     90     87     92     Any
        //  148    191    194    201    Any
        //  194    198    194    201    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        //     at com.strobel.assembler.ir.StackMappingVisitor.push(StackMappingVisitor.java:290)
        //     at com.strobel.assembler.ir.StackMappingVisitor$InstructionAnalyzer.execute(StackMappingVisitor.java:833)
        //     at com.strobel.assembler.ir.StackMappingVisitor$InstructionAnalyzer.visit(StackMappingVisitor.java:398)
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2030)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:211)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void destroySingleton(final String beanName) {
        this.removeSingleton(beanName);
        final DisposableBean disposableBean;
        synchronized (this.disposableBeans) {
            disposableBean = this.disposableBeans.remove(beanName);
        }
        this.destroyBean(beanName, disposableBean);
    }
    
    protected void destroyBean(final String beanName, final DisposableBean bean) {
        final Set<String> dependencies = this.dependentBeanMap.remove(beanName);
        if (dependencies != null) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Retrieved dependent beans for bean '" + beanName + "': " + dependencies);
            }
            for (final String dependentBeanName : dependencies) {
                this.destroySingleton(dependentBeanName);
            }
        }
        if (bean != null) {
            try {
                bean.destroy();
            }
            catch (Throwable ex) {
                this.logger.error("Destroy method on bean with name '" + beanName + "' threw an exception", ex);
            }
        }
        final Set<String> containedBeans = this.containedBeanMap.remove(beanName);
        if (containedBeans != null) {
            for (final String containedBeanName : containedBeans) {
                this.destroySingleton(containedBeanName);
            }
        }
        synchronized (this.dependentBeanMap) {
            final Iterator<Map.Entry<String, Set<String>>> it = this.dependentBeanMap.entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry<String, Set<String>> entry = it.next();
                final Set<String> dependenciesToClean = entry.getValue();
                dependenciesToClean.remove(beanName);
                if (dependenciesToClean.isEmpty()) {
                    it.remove();
                }
            }
        }
        this.dependenciesForBeanMap.remove(beanName);
    }
    
    protected final Object getSingletonMutex() {
        return this.singletonObjects;
    }
    
    static {
        NULL_OBJECT = new Object();
    }
}
