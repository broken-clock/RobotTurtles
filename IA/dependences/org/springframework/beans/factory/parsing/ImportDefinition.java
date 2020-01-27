// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.parsing;

import org.springframework.util.Assert;
import org.springframework.core.io.Resource;
import org.springframework.beans.BeanMetadataElement;

public class ImportDefinition implements BeanMetadataElement
{
    private final String importedResource;
    private final Resource[] actualResources;
    private final Object source;
    
    public ImportDefinition(final String importedResource) {
        this(importedResource, null, null);
    }
    
    public ImportDefinition(final String importedResource, final Object source) {
        this(importedResource, null, source);
    }
    
    public ImportDefinition(final String importedResource, final Resource[] actualResources, final Object source) {
        Assert.notNull(importedResource, "Imported resource must not be null");
        this.importedResource = importedResource;
        this.actualResources = actualResources;
        this.source = source;
    }
    
    public final String getImportedResource() {
        return this.importedResource;
    }
    
    public final Resource[] getActualResources() {
        return this.actualResources;
    }
    
    @Override
    public final Object getSource() {
        return this.source;
    }
}
