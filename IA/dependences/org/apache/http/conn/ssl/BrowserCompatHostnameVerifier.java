// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.http.conn.ssl;

import javax.net.ssl.SSLException;
import org.apache.http.annotation.Immutable;

@Deprecated
@Immutable
public class BrowserCompatHostnameVerifier extends AbstractVerifier
{
    public static final BrowserCompatHostnameVerifier INSTANCE;
    
    @Override
    public final void verify(final String host, final String[] cns, final String[] subjectAlts) throws SSLException {
        this.verify(host, cns, subjectAlts, false);
    }
    
    @Override
    public final String toString() {
        return "BROWSER_COMPATIBLE";
    }
    
    static {
        INSTANCE = new BrowserCompatHostnameVerifier();
    }
}
