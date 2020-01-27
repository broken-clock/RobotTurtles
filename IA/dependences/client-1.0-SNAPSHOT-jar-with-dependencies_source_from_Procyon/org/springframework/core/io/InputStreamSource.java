// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.io;

import java.io.IOException;
import java.io.InputStream;

public interface InputStreamSource
{
    InputStream getInputStream() throws IOException;
}
