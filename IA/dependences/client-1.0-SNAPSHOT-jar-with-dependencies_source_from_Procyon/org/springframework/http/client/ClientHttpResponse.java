// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.client;

import java.io.IOException;
import org.springframework.http.HttpStatus;
import java.io.Closeable;
import org.springframework.http.HttpInputMessage;

public interface ClientHttpResponse extends HttpInputMessage, Closeable
{
    HttpStatus getStatusCode() throws IOException;
    
    int getRawStatusCode() throws IOException;
    
    String getStatusText() throws IOException;
    
    void close();
}
