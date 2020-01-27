// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.aspectj;

import org.springframework.aop.support.ClassFilters;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.springframework.aop.support.DelegatePerTargetObjectIntroductionInterceptor;
import org.aopalliance.aop.Advice;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.IntroductionAdvisor;

public class DeclareParentsAdvisor implements IntroductionAdvisor
{
    private final Class<?> introducedInterface;
    private final ClassFilter typePatternClassFilter;
    private final Advice advice;
    
    public DeclareParentsAdvisor(final Class<?> interfaceType, final String typePattern, final Class<?> defaultImpl) {
        this(interfaceType, typePattern, defaultImpl, new DelegatePerTargetObjectIntroductionInterceptor(defaultImpl, interfaceType));
    }
    
    public DeclareParentsAdvisor(final Class<?> interfaceType, final String typePattern, final Object delegateRef) {
        this(interfaceType, typePattern, delegateRef.getClass(), new DelegatingIntroductionInterceptor(delegateRef));
    }
    
    private DeclareParentsAdvisor(final Class<?> interfaceType, final String typePattern, final Class<?> implementationClass, final Advice advice) {
        this.introducedInterface = interfaceType;
        final ClassFilter typePatternFilter = new TypePatternClassFilter(typePattern);
        final ClassFilter exclusion = new ClassFilter() {
            @Override
            public boolean matches(final Class<?> clazz) {
                return !DeclareParentsAdvisor.this.introducedInterface.isAssignableFrom(clazz);
            }
        };
        this.typePatternClassFilter = ClassFilters.intersection(typePatternFilter, exclusion);
        this.advice = advice;
    }
    
    @Override
    public ClassFilter getClassFilter() {
        return this.typePatternClassFilter;
    }
    
    @Override
    public void validateInterfaces() throws IllegalArgumentException {
    }
    
    @Override
    public boolean isPerInstance() {
        return true;
    }
    
    @Override
    public Advice getAdvice() {
        return this.advice;
    }
    
    @Override
    public Class<?>[] getInterfaces() {
        return (Class<?>[])new Class[] { this.introducedInterface };
    }
}
