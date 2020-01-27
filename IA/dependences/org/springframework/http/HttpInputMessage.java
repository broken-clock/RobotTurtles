// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http;

import java.io.IOException;
import java.io.InputStream;

public interface HttpInputMessage extends HttpMessage
{
    InputStream getBody() throws IOException;
}
