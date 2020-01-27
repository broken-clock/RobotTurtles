// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.aspectj;

import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractGenericPointcutAdvisor;

public class AspectJExpressionPointcutAdvisor extends AbstractGenericPointcutAdvisor
{
    private final AspectJExpressionPointcut pointcut;
    
    public AspectJExpressionPointcutAdvisor() {
        this.pointcut = new AspectJExpressionPointcut();
    }
    
    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }
    
    public void setExpression(final String expression) {
        this.pointcut.setExpression(expression);
    }
    
    public void setLocation(final String location) {
        this.pointcut.setLocation(location);
    }
    
    public void setParameterTypes(final Class<?>[] types) {
        this.pointcut.setParameterTypes(types);
    }
    
    public void setParameterNames(final String[] names) {
        this.pointcut.setParameterNames(names);
    }
    
    public String getLocation() {
        return this.pointcut.getLocation();
    }
    
    public String getExpression() {
        return this.pointcut.getExpression();
    }
}
