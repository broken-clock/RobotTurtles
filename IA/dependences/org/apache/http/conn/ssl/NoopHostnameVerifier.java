// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.http.conn.ssl;

import javax.net.ssl.SSLSession;
import org.apache.http.annotation.Immutable;
import javax.net.ssl.HostnameVerifier;

@Immutable
public class NoopHostnameVerifier implements HostnameVerifier
{
    public static final NoopHostnameVerifier INSTANCE;
    
    @Override
    public boolean verify(final String s, final SSLSession sslSession) {
        return true;
    }
    
    @Override
    public final String toString() {
        return "NO_OP";
    }
    
    static {
        INSTANCE = new NoopHostnameVerifier();
    }
}
