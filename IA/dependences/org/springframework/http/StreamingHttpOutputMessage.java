// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http;

import java.io.IOException;
import java.io.OutputStream;

public interface StreamingHttpOutputMessage extends HttpOutputMessage
{
    void setBody(final Body p0);
    
    public interface Body
    {
        void writeTo(final OutputStream p0) throws IOException;
    }
}
