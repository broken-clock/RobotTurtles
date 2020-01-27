// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.framework;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.TargetSource;
import org.springframework.aop.TargetClassAware;

public interface Advised extends TargetClassAware
{
    boolean isFrozen();
    
    boolean isProxyTargetClass();
    
    Class<?>[] getProxiedInterfaces();
    
    boolean isInterfaceProxied(final Class<?> p0);
    
    void setTargetSource(final TargetSource p0);
    
    TargetSource getTargetSource();
    
    void setExposeProxy(final boolean p0);
    
    boolean isExposeProxy();
    
    void setPreFiltered(final boolean p0);
    
    boolean isPreFiltered();
    
    Advisor[] getAdvisors();
    
    void addAdvisor(final Advisor p0) throws AopConfigException;
    
    void addAdvisor(final int p0, final Advisor p1) throws AopConfigException;
    
    boolean removeAdvisor(final Advisor p0);
    
    void removeAdvisor(final int p0) throws AopConfigException;
    
    int indexOf(final Advisor p0);
    
    boolean replaceAdvisor(final Advisor p0, final Advisor p1) throws AopConfigException;
    
    void addAdvice(final Advice p0) throws AopConfigException;
    
    void addAdvice(final int p0, final Advice p1) throws AopConfigException;
    
    boolean removeAdvice(final Advice p0);
    
    int indexOf(final Advice p0);
    
    String toProxyConfigString();
}
