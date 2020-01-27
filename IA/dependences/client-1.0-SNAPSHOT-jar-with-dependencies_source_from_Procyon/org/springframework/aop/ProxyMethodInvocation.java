// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop;

import org.aopalliance.intercept.MethodInvocation;

public interface ProxyMethodInvocation extends MethodInvocation
{
    Object getProxy();
    
    MethodInvocation invocableClone();
    
    MethodInvocation invocableClone(final Object[] p0);
    
    void setArguments(final Object[] p0);
    
    void setUserAttribute(final String p0, final Object p1);
    
    Object getUserAttribute(final String p0);
}
