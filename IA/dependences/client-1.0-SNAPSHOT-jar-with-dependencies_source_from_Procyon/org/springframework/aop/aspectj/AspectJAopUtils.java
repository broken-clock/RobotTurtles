// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.aspectj;

import org.aopalliance.aop.Advice;
import org.springframework.aop.AfterAdvice;
import org.springframework.aop.BeforeAdvice;
import org.springframework.aop.Advisor;

public abstract class AspectJAopUtils
{
    public static boolean isBeforeAdvice(final Advisor anAdvisor) {
        final AspectJPrecedenceInformation precedenceInfo = getAspectJPrecedenceInformationFor(anAdvisor);
        if (precedenceInfo != null) {
            return precedenceInfo.isBeforeAdvice();
        }
        return anAdvisor.getAdvice() instanceof BeforeAdvice;
    }
    
    public static boolean isAfterAdvice(final Advisor anAdvisor) {
        final AspectJPrecedenceInformation precedenceInfo = getAspectJPrecedenceInformationFor(anAdvisor);
        if (precedenceInfo != null) {
            return precedenceInfo.isAfterAdvice();
        }
        return anAdvisor.getAdvice() instanceof AfterAdvice;
    }
    
    public static AspectJPrecedenceInformation getAspectJPrecedenceInformationFor(final Advisor anAdvisor) {
        if (anAdvisor instanceof AspectJPrecedenceInformation) {
            return (AspectJPrecedenceInformation)anAdvisor;
        }
        final Advice advice = anAdvisor.getAdvice();
        if (advice instanceof AspectJPrecedenceInformation) {
            return (AspectJPrecedenceInformation)advice;
        }
        return null;
    }
}
