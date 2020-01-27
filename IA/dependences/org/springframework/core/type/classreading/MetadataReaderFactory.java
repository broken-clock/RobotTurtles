// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.type.classreading;

import org.springframework.core.io.Resource;
import java.io.IOException;

public interface MetadataReaderFactory
{
    MetadataReader getMetadataReader(final String p0) throws IOException;
    
    MetadataReader getMetadataReader(final Resource p0) throws IOException;
}
