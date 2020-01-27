// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jndi;

import javax.naming.NamingException;
import javax.naming.Context;

public interface JndiCallback<T>
{
    T doInContext(final Context p0) throws NamingException;
}
