// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.objenesis;

public class ObjenesisException extends RuntimeException
{
    private static final long serialVersionUID = -2677230016262426968L;
    
    public ObjenesisException(final String msg) {
        super(msg);
    }
    
    public ObjenesisException(final Throwable cause) {
        super(cause);
    }
    
    public ObjenesisException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
