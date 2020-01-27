// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.parsing;

import org.springframework.beans.factory.BeanDefinitionStoreException;

public class BeanDefinitionParsingException extends BeanDefinitionStoreException
{
    public BeanDefinitionParsingException(final Problem problem) {
        super(problem.getResourceDescription(), problem.toString(), problem.getRootCause());
    }
}
