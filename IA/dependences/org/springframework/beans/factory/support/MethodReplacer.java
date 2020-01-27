// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import java.lang.reflect.Method;

public interface MethodReplacer
{
    Object reimplement(final Object p0, final Method p1, final Object[] p2) throws Throwable;
}
