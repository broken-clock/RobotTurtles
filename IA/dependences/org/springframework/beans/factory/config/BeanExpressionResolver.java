// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;

public interface BeanExpressionResolver
{
    Object evaluate(final String p0, final BeanExpressionContext p1) throws BeansException;
}
