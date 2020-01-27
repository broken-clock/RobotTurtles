// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import java.security.AccessController;
import java.security.AccessControlContext;

public class SimpleSecurityContextProvider implements SecurityContextProvider
{
    private final AccessControlContext acc;
    
    public SimpleSecurityContextProvider() {
        this(null);
    }
    
    public SimpleSecurityContextProvider(final AccessControlContext acc) {
        this.acc = acc;
    }
    
    @Override
    public AccessControlContext getAccessControlContext() {
        return (this.acc != null) ? this.acc : AccessController.getContext();
    }
}
