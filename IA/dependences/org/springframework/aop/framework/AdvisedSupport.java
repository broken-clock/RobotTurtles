// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.framework;

import org.springframework.util.ClassUtils;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.DynamicIntroductionAdvice;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.springframework.aop.IntroductionInfo;
import org.aopalliance.aop.Advice;
import org.springframework.util.CollectionUtils;
import java.util.Collection;
import java.util.Arrays;
import org.springframework.aop.IntroductionAdvisor;
import java.util.Iterator;
import org.springframework.util.Assert;
import org.springframework.aop.target.EmptyTargetSource;
import org.springframework.aop.target.SingletonTargetSource;
import java.util.concurrent.ConcurrentHashMap;
import java.util.LinkedList;
import java.util.ArrayList;
import org.springframework.aop.Advisor;
import java.util.List;
import java.util.Map;
import org.springframework.aop.TargetSource;

public class AdvisedSupport extends ProxyConfig implements Advised
{
    private static final long serialVersionUID = 2651364800145442165L;
    public static final TargetSource EMPTY_TARGET_SOURCE;
    TargetSource targetSource;
    private boolean preFiltered;
    AdvisorChainFactory advisorChainFactory;
    private transient Map<MethodCacheKey, List<Object>> methodCache;
    private List<Class<?>> interfaces;
    private List<Advisor> advisors;
    private Advisor[] advisorArray;
    
    public AdvisedSupport() {
        this.targetSource = AdvisedSupport.EMPTY_TARGET_SOURCE;
        this.preFiltered = false;
        this.advisorChainFactory = new DefaultAdvisorChainFactory();
        this.interfaces = new ArrayList<Class<?>>();
        this.advisors = new LinkedList<Advisor>();
        this.advisorArray = new Advisor[0];
        this.initMethodCache();
    }
    
    public AdvisedSupport(final Class<?>[] interfaces) {
        this();
        this.setInterfaces(interfaces);
    }
    
    private void initMethodCache() {
        this.methodCache = new ConcurrentHashMap<MethodCacheKey, List<Object>>(32);
    }
    
    public void setTarget(final Object target) {
        this.setTargetSource(new SingletonTargetSource(target));
    }
    
    @Override
    public void setTargetSource(final TargetSource targetSource) {
        this.targetSource = ((targetSource != null) ? targetSource : AdvisedSupport.EMPTY_TARGET_SOURCE);
    }
    
    @Override
    public TargetSource getTargetSource() {
        return this.targetSource;
    }
    
    public void setTargetClass(final Class<?> targetClass) {
        this.targetSource = EmptyTargetSource.forClass(targetClass);
    }
    
    @Override
    public Class<?> getTargetClass() {
        return this.targetSource.getTargetClass();
    }
    
    @Override
    public void setPreFiltered(final boolean preFiltered) {
        this.preFiltered = preFiltered;
    }
    
    @Override
    public boolean isPreFiltered() {
        return this.preFiltered;
    }
    
    public void setAdvisorChainFactory(final AdvisorChainFactory advisorChainFactory) {
        Assert.notNull(advisorChainFactory, "AdvisorChainFactory must not be null");
        this.advisorChainFactory = advisorChainFactory;
    }
    
    public AdvisorChainFactory getAdvisorChainFactory() {
        return this.advisorChainFactory;
    }
    
    public void setInterfaces(final Class<?>... interfaces) {
        Assert.notNull(interfaces, "Interfaces must not be null");
        this.interfaces.clear();
        for (final Class<?> ifc : interfaces) {
            this.addInterface(ifc);
        }
    }
    
    public void addInterface(final Class<?> intf) {
        Assert.notNull(intf, "Interface must not be null");
        if (!intf.isInterface()) {
            throw new IllegalArgumentException("[" + intf.getName() + "] is not an interface");
        }
        if (!this.interfaces.contains(intf)) {
            this.interfaces.add(intf);
            this.adviceChanged();
        }
    }
    
    public boolean removeInterface(final Class<?> intf) {
        return this.interfaces.remove(intf);
    }
    
    @Override
    public Class<?>[] getProxiedInterfaces() {
        return this.interfaces.toArray(new Class[this.interfaces.size()]);
    }
    
    @Override
    public boolean isInterfaceProxied(final Class<?> intf) {
        for (final Class<?> proxyIntf : this.interfaces) {
            if (intf.isAssignableFrom(proxyIntf)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public final Advisor[] getAdvisors() {
        return this.advisorArray;
    }
    
    @Override
    public void addAdvisor(final Advisor advisor) {
        final int pos = this.advisors.size();
        this.addAdvisor(pos, advisor);
    }
    
    @Override
    public void addAdvisor(final int pos, final Advisor advisor) throws AopConfigException {
        if (advisor instanceof IntroductionAdvisor) {
            this.validateIntroductionAdvisor((IntroductionAdvisor)advisor);
        }
        this.addAdvisorInternal(pos, advisor);
    }
    
    @Override
    public boolean removeAdvisor(final Advisor advisor) {
        final int index = this.indexOf(advisor);
        if (index == -1) {
            return false;
        }
        this.removeAdvisor(index);
        return true;
    }
    
    @Override
    public void removeAdvisor(final int index) throws AopConfigException {
        if (this.isFrozen()) {
            throw new AopConfigException("Cannot remove Advisor: Configuration is frozen.");
        }
        if (index < 0 || index > this.advisors.size() - 1) {
            throw new AopConfigException("Advisor index " + index + " is out of bounds: " + "This configuration only has " + this.advisors.size() + " advisors.");
        }
        final Advisor advisor = this.advisors.get(index);
        if (advisor instanceof IntroductionAdvisor) {
            final IntroductionAdvisor ia = (IntroductionAdvisor)advisor;
            for (int j = 0; j < ia.getInterfaces().length; ++j) {
                this.removeInterface(ia.getInterfaces()[j]);
            }
        }
        this.advisors.remove(index);
        this.updateAdvisorArray();
        this.adviceChanged();
    }
    
    @Override
    public int indexOf(final Advisor advisor) {
        Assert.notNull(advisor, "Advisor must not be null");
        return this.advisors.indexOf(advisor);
    }
    
    @Override
    public boolean replaceAdvisor(final Advisor a, final Advisor b) throws AopConfigException {
        Assert.notNull(a, "Advisor a must not be null");
        Assert.notNull(b, "Advisor b must not be null");
        final int index = this.indexOf(a);
        if (index == -1) {
            return false;
        }
        this.removeAdvisor(index);
        this.addAdvisor(index, b);
        return true;
    }
    
    public void addAdvisors(final Advisor... advisors) {
        this.addAdvisors(Arrays.asList(advisors));
    }
    
    public void addAdvisors(final Collection<Advisor> advisors) {
        if (this.isFrozen()) {
            throw new AopConfigException("Cannot add advisor: Configuration is frozen.");
        }
        if (!CollectionUtils.isEmpty(advisors)) {
            for (final Advisor advisor : advisors) {
                if (advisor instanceof IntroductionAdvisor) {
                    this.validateIntroductionAdvisor((IntroductionAdvisor)advisor);
                }
                Assert.notNull(advisor, "Advisor must not be null");
                this.advisors.add(advisor);
            }
            this.updateAdvisorArray();
            this.adviceChanged();
        }
    }
    
    private void validateIntroductionAdvisor(final IntroductionAdvisor advisor) {
        advisor.validateInterfaces();
        final Class<?>[] interfaces;
        final Class<?>[] ifcs = interfaces = advisor.getInterfaces();
        for (final Class<?> ifc : interfaces) {
            this.addInterface(ifc);
        }
    }
    
    private void addAdvisorInternal(final int pos, final Advisor advisor) throws AopConfigException {
        Assert.notNull(advisor, "Advisor must not be null");
        if (this.isFrozen()) {
            throw new AopConfigException("Cannot add advisor: Configuration is frozen.");
        }
        if (pos > this.advisors.size()) {
            throw new IllegalArgumentException("Illegal position " + pos + " in advisor list with size " + this.advisors.size());
        }
        this.advisors.add(pos, advisor);
        this.updateAdvisorArray();
        this.adviceChanged();
    }
    
    protected final void updateAdvisorArray() {
        this.advisorArray = this.advisors.toArray(new Advisor[this.advisors.size()]);
    }
    
    protected final List<Advisor> getAdvisorsInternal() {
        return this.advisors;
    }
    
    @Override
    public void addAdvice(final Advice advice) throws AopConfigException {
        final int pos = this.advisors.size();
        this.addAdvice(pos, advice);
    }
    
    @Override
    public void addAdvice(final int pos, final Advice advice) throws AopConfigException {
        Assert.notNull(advice, "Advice must not be null");
        if (advice instanceof IntroductionInfo) {
            this.addAdvisor(pos, new DefaultIntroductionAdvisor(advice, (IntroductionInfo)advice));
        }
        else {
            if (advice instanceof DynamicIntroductionAdvice) {
                throw new AopConfigException("DynamicIntroductionAdvice may only be added as part of IntroductionAdvisor");
            }
            this.addAdvisor(pos, new DefaultPointcutAdvisor(advice));
        }
    }
    
    @Override
    public boolean removeAdvice(final Advice advice) throws AopConfigException {
        final int index = this.indexOf(advice);
        if (index == -1) {
            return false;
        }
        this.removeAdvisor(index);
        return true;
    }
    
    @Override
    public int indexOf(final Advice advice) {
        Assert.notNull(advice, "Advice must not be null");
        for (int i = 0; i < this.advisors.size(); ++i) {
            final Advisor advisor = this.advisors.get(i);
            if (advisor.getAdvice() == advice) {
                return i;
            }
        }
        return -1;
    }
    
    public boolean adviceIncluded(final Advice advice) {
        if (advice != null) {
            for (final Advisor advisor : this.advisors) {
                if (advisor.getAdvice() == advice) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public int countAdvicesOfType(final Class<?> adviceClass) {
        int count = 0;
        if (adviceClass != null) {
            for (final Advisor advisor : this.advisors) {
                if (adviceClass.isInstance(advisor.getAdvice())) {
                    ++count;
                }
            }
        }
        return count;
    }
    
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(final Method method, final Class<?> targetClass) {
        final MethodCacheKey cacheKey = new MethodCacheKey(method);
        List<Object> cached = this.methodCache.get(cacheKey);
        if (cached == null) {
            cached = this.advisorChainFactory.getInterceptorsAndDynamicInterceptionAdvice(this, method, targetClass);
            this.methodCache.put(cacheKey, cached);
        }
        return cached;
    }
    
    protected void adviceChanged() {
        this.methodCache.clear();
    }
    
    protected void copyConfigurationFrom(final AdvisedSupport other) {
        this.copyConfigurationFrom(other, other.targetSource, new ArrayList<Advisor>(other.advisors));
    }
    
    protected void copyConfigurationFrom(final AdvisedSupport other, final TargetSource targetSource, final List<Advisor> advisors) {
        this.copyFrom(other);
        this.targetSource = targetSource;
        this.advisorChainFactory = other.advisorChainFactory;
        this.interfaces = new ArrayList<Class<?>>(other.interfaces);
        for (final Advisor advisor : advisors) {
            if (advisor instanceof IntroductionAdvisor) {
                this.validateIntroductionAdvisor((IntroductionAdvisor)advisor);
            }
            Assert.notNull(advisor, "Advisor must not be null");
            this.advisors.add(advisor);
        }
        this.updateAdvisorArray();
        this.adviceChanged();
    }
    
    AdvisedSupport getConfigurationOnlyCopy() {
        final AdvisedSupport copy = new AdvisedSupport();
        copy.copyFrom(this);
        copy.targetSource = EmptyTargetSource.forClass(this.getTargetClass(), this.getTargetSource().isStatic());
        copy.advisorChainFactory = this.advisorChainFactory;
        copy.interfaces = this.interfaces;
        copy.advisors = this.advisors;
        copy.updateAdvisorArray();
        return copy;
    }
    
    private void readObject(final ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.initMethodCache();
    }
    
    @Override
    public String toProxyConfigString() {
        return this.toString();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.getClass().getName());
        sb.append(": ").append(this.interfaces.size()).append(" interfaces ");
        sb.append(ClassUtils.classNamesToString(this.interfaces)).append("; ");
        sb.append(this.advisors.size()).append(" advisors ");
        sb.append(this.advisors).append("; ");
        sb.append("targetSource [").append(this.targetSource).append("]; ");
        sb.append(super.toString());
        return sb.toString();
    }
    
    static {
        EMPTY_TARGET_SOURCE = EmptyTargetSource.INSTANCE;
    }
    
    private static class MethodCacheKey
    {
        private final Method method;
        private final int hashCode;
        
        public MethodCacheKey(final Method method) {
            this.method = method;
            this.hashCode = method.hashCode();
        }
        
        @Override
        public boolean equals(final Object other) {
            if (other == this) {
                return true;
            }
            final MethodCacheKey otherKey = (MethodCacheKey)other;
            return this.method == otherKey.method;
        }
        
        @Override
        public int hashCode() {
            return this.hashCode;
        }
    }
}
