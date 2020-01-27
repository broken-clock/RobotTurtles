// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http;

import java.io.IOException;
import java.io.OutputStream;

public interface HttpOutputMessage extends HttpMessage
{
    OutputStream getBody() throws IOException;
}
