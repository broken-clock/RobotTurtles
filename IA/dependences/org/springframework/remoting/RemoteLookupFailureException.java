// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting;

public class RemoteLookupFailureException extends RemoteAccessException
{
    public RemoteLookupFailureException(final String msg) {
        super(msg);
    }
    
    public RemoteLookupFailureException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
