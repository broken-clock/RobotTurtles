// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.multipart;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;

public interface MultipartFile
{
    String getName();
    
    String getOriginalFilename();
    
    String getContentType();
    
    boolean isEmpty();
    
    long getSize();
    
    byte[] getBytes() throws IOException;
    
    InputStream getInputStream() throws IOException;
    
    void transferTo(final File p0) throws IOException, IllegalStateException;
}
