// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory;

import java.util.Arrays;
import org.springframework.util.StringUtils;
import java.util.Collection;

public class NoUniqueBeanDefinitionException extends NoSuchBeanDefinitionException
{
    private int numberOfBeansFound;
    
    public NoUniqueBeanDefinitionException(final Class<?> type, final int numberOfBeansFound, final String message) {
        super(type, message);
        this.numberOfBeansFound = numberOfBeansFound;
    }
    
    public NoUniqueBeanDefinitionException(final Class<?> type, final Collection<String> beanNamesFound) {
        this(type, beanNamesFound.size(), "expected single matching bean but found " + beanNamesFound.size() + ": " + StringUtils.collectionToCommaDelimitedString(beanNamesFound));
    }
    
    public NoUniqueBeanDefinitionException(final Class<?> type, final String... beanNamesFound) {
        this(type, Arrays.asList(beanNamesFound));
    }
    
    @Override
    public int getNumberOfBeansFound() {
        return this.numberOfBeansFound;
    }
}
