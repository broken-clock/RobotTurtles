// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.multipart;

import org.springframework.util.MultiValueMap;
import java.util.Map;
import java.util.List;
import java.util.Iterator;

public interface MultipartRequest
{
    Iterator<String> getFileNames();
    
    MultipartFile getFile(final String p0);
    
    List<MultipartFile> getFiles(final String p0);
    
    Map<String, MultipartFile> getFileMap();
    
    MultiValueMap<String, MultipartFile> getMultiFileMap();
    
    String getMultipartContentType(final String p0);
}
