// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop;

public interface IntroductionAdvisor extends Advisor, IntroductionInfo
{
    ClassFilter getClassFilter();
    
    void validateInterfaces() throws IllegalArgumentException;
}
