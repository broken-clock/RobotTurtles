// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.type.filter;

import java.io.IOException;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;

public abstract class AbstractTypeHierarchyTraversingFilter implements TypeFilter
{
    private final boolean considerInherited;
    private final boolean considerInterfaces;
    
    protected AbstractTypeHierarchyTraversingFilter(final boolean considerInherited, final boolean considerInterfaces) {
        this.considerInherited = considerInherited;
        this.considerInterfaces = considerInterfaces;
    }
    
    @Override
    public boolean match(final MetadataReader metadataReader, final MetadataReaderFactory metadataReaderFactory) throws IOException {
        if (this.matchSelf(metadataReader)) {
            return true;
        }
        final ClassMetadata metadata = metadataReader.getClassMetadata();
        if (this.matchClassName(metadata.getClassName())) {
            return true;
        }
        if (!this.considerInherited) {
            return false;
        }
        if (metadata.hasSuperClass()) {
            final Boolean superClassMatch = this.matchSuperClass(metadata.getSuperClassName());
            if (superClassMatch != null) {
                if (superClassMatch) {
                    return true;
                }
            }
            else if (this.match(metadata.getSuperClassName(), metadataReaderFactory)) {
                return true;
            }
        }
        if (!this.considerInterfaces) {
            return false;
        }
        for (final String ifc : metadata.getInterfaceNames()) {
            final Boolean interfaceMatch = this.matchInterface(ifc);
            if (interfaceMatch != null) {
                if (interfaceMatch) {
                    return true;
                }
            }
            else if (this.match(ifc, metadataReaderFactory)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean match(final String className, final MetadataReaderFactory metadataReaderFactory) throws IOException {
        return this.match(metadataReaderFactory.getMetadataReader(className), metadataReaderFactory);
    }
    
    protected boolean matchSelf(final MetadataReader metadataReader) {
        return false;
    }
    
    protected boolean matchClassName(final String className) {
        return false;
    }
    
    protected Boolean matchSuperClass(final String superClassName) {
        return null;
    }
    
    protected Boolean matchInterface(final String interfaceNames) {
        return null;
    }
}
