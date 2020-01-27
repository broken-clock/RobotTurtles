// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.support;

import java.io.Serializable;
import org.springframework.util.ObjectUtils;
import org.springframework.aop.Pointcut;
import org.aopalliance.aop.Advice;

public class RegexpMethodPointcutAdvisor extends AbstractGenericPointcutAdvisor
{
    private String[] patterns;
    private AbstractRegexpMethodPointcut pointcut;
    private final Object pointcutMonitor;
    
    public RegexpMethodPointcutAdvisor() {
        this.pointcutMonitor = new SerializableMonitor();
    }
    
    public RegexpMethodPointcutAdvisor(final Advice advice) {
        this.pointcutMonitor = new SerializableMonitor();
        this.setAdvice(advice);
    }
    
    public RegexpMethodPointcutAdvisor(final String pattern, final Advice advice) {
        this.pointcutMonitor = new SerializableMonitor();
        this.setPattern(pattern);
        this.setAdvice(advice);
    }
    
    public RegexpMethodPointcutAdvisor(final String[] patterns, final Advice advice) {
        this.pointcutMonitor = new SerializableMonitor();
        this.setPatterns(patterns);
        this.setAdvice(advice);
    }
    
    public void setPattern(final String pattern) {
        this.setPatterns(new String[] { pattern });
    }
    
    public void setPatterns(final String[] patterns) {
        this.patterns = patterns;
    }
    
    @Override
    public Pointcut getPointcut() {
        synchronized (this.pointcutMonitor) {
            if (this.pointcut == null) {
                (this.pointcut = this.createPointcut()).setPatterns(this.patterns);
            }
            return this.pointcut;
        }
    }
    
    protected AbstractRegexpMethodPointcut createPointcut() {
        return new JdkRegexpMethodPointcut();
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + ": advice [" + this.getAdvice() + "], pointcut patterns " + ObjectUtils.nullSafeToString(this.patterns);
    }
    
    private static class SerializableMonitor implements Serializable
    {
    }
}
