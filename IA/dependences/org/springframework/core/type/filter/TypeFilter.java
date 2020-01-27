// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.type.filter;

import java.io.IOException;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;

public interface TypeFilter
{
    boolean match(final MetadataReader p0, final MetadataReaderFactory p1) throws IOException;
}
