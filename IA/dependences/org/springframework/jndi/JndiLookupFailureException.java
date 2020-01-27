// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jndi;

import javax.naming.NamingException;
import org.springframework.core.NestedRuntimeException;

public class JndiLookupFailureException extends NestedRuntimeException
{
    public JndiLookupFailureException(final String msg, final NamingException cause) {
        super(msg, cause);
    }
}
