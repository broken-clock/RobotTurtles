// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.validation.beanvalidation;

import javax.validation.ValidationException;
import org.apache.commons.logging.LogFactory;

public class OptionalValidatorFactoryBean extends LocalValidatorFactoryBean
{
    @Override
    public void afterPropertiesSet() {
        try {
            super.afterPropertiesSet();
        }
        catch (ValidationException ex) {
            LogFactory.getLog(this.getClass()).debug("Failed to set up a Bean Validation provider", (Throwable)ex);
        }
    }
}
