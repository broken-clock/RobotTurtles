// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.support;

import org.springframework.aop.Pointcut;
import org.springframework.aop.ClassFilter;
import org.aopalliance.aop.Advice;

public class NameMatchMethodPointcutAdvisor extends AbstractGenericPointcutAdvisor
{
    private final NameMatchMethodPointcut pointcut;
    
    public NameMatchMethodPointcutAdvisor() {
        this.pointcut = new NameMatchMethodPointcut();
    }
    
    public NameMatchMethodPointcutAdvisor(final Advice advice) {
        this.pointcut = new NameMatchMethodPointcut();
        this.setAdvice(advice);
    }
    
    public void setClassFilter(final ClassFilter classFilter) {
        this.pointcut.setClassFilter(classFilter);
    }
    
    public void setMappedName(final String mappedName) {
        this.pointcut.setMappedName(mappedName);
    }
    
    public void setMappedNames(final String[] mappedNames) {
        this.pointcut.setMappedNames(mappedNames);
    }
    
    public NameMatchMethodPointcut addMethodName(final String name) {
        return this.pointcut.addMethodName(name);
    }
    
    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }
}
