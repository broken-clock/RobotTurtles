// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import java.security.AccessControlContext;

public interface SecurityContextProvider
{
    AccessControlContext getAccessControlContext();
}
