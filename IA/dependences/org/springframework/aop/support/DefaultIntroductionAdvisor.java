// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.support;

import java.util.Collection;
import org.springframework.util.ClassUtils;
import java.util.Iterator;
import org.springframework.aop.DynamicIntroductionAdvice;
import org.springframework.util.Assert;
import java.util.HashSet;
import org.springframework.aop.IntroductionInfo;
import java.util.Set;
import org.aopalliance.aop.Advice;
import java.io.Serializable;
import org.springframework.core.Ordered;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.IntroductionAdvisor;

public class DefaultIntroductionAdvisor implements IntroductionAdvisor, ClassFilter, Ordered, Serializable
{
    private final Advice advice;
    private final Set<Class<?>> interfaces;
    private int order;
    
    public DefaultIntroductionAdvisor(final Advice advice) {
        this(advice, (advice instanceof IntroductionInfo) ? advice : null);
    }
    
    public DefaultIntroductionAdvisor(final Advice advice, final IntroductionInfo introductionInfo) {
        this.interfaces = new HashSet<Class<?>>();
        this.order = Integer.MAX_VALUE;
        Assert.notNull(advice, "Advice must not be null");
        this.advice = advice;
        if (introductionInfo != null) {
            final Class<?>[] introducedInterfaces = introductionInfo.getInterfaces();
            if (introducedInterfaces.length == 0) {
                throw new IllegalArgumentException("IntroductionAdviceSupport implements no interfaces");
            }
            for (final Class<?> ifc : introducedInterfaces) {
                this.addInterface(ifc);
            }
        }
    }
    
    public DefaultIntroductionAdvisor(final DynamicIntroductionAdvice advice, final Class<?> intf) {
        this.interfaces = new HashSet<Class<?>>();
        this.order = Integer.MAX_VALUE;
        Assert.notNull(advice, "Advice must not be null");
        this.advice = advice;
        this.addInterface(intf);
    }
    
    public void addInterface(final Class<?> intf) {
        Assert.notNull(intf, "Interface must not be null");
        if (!intf.isInterface()) {
            throw new IllegalArgumentException("Specified class [" + intf.getName() + "] must be an interface");
        }
        this.interfaces.add(intf);
    }
    
    @Override
    public Class<?>[] getInterfaces() {
        return this.interfaces.toArray(new Class[this.interfaces.size()]);
    }
    
    @Override
    public void validateInterfaces() throws IllegalArgumentException {
        for (final Class<?> ifc : this.interfaces) {
            if (this.advice instanceof DynamicIntroductionAdvice && !((DynamicIntroductionAdvice)this.advice).implementsInterface(ifc)) {
                throw new IllegalArgumentException("DynamicIntroductionAdvice [" + this.advice + "] " + "does not implement interface [" + ifc.getName() + "] specified for introduction");
            }
        }
    }
    
    public void setOrder(final int order) {
        this.order = order;
    }
    
    @Override
    public int getOrder() {
        return this.order;
    }
    
    @Override
    public Advice getAdvice() {
        return this.advice;
    }
    
    @Override
    public boolean isPerInstance() {
        return true;
    }
    
    @Override
    public ClassFilter getClassFilter() {
        return this;
    }
    
    @Override
    public boolean matches(final Class<?> clazz) {
        return true;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DefaultIntroductionAdvisor)) {
            return false;
        }
        final DefaultIntroductionAdvisor otherAdvisor = (DefaultIntroductionAdvisor)other;
        return this.advice.equals(otherAdvisor.advice) && this.interfaces.equals(otherAdvisor.interfaces);
    }
    
    @Override
    public int hashCode() {
        return this.advice.hashCode() * 13 + this.interfaces.hashCode();
    }
    
    @Override
    public String toString() {
        return ClassUtils.getShortName(this.getClass()) + ": advice [" + this.advice + "]; interfaces " + ClassUtils.classNamesToString(this.interfaces);
    }
}
