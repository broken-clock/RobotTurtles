// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.type.filter;

import org.springframework.core.type.ClassMetadata;
import java.io.IOException;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;

public abstract class AbstractClassTestingTypeFilter implements TypeFilter
{
    @Override
    public final boolean match(final MetadataReader metadataReader, final MetadataReaderFactory metadataReaderFactory) throws IOException {
        return this.match(metadataReader.getClassMetadata());
    }
    
    protected abstract boolean match(final ClassMetadata p0);
}
