// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel;

public class SpelParserConfiguration
{
    private final boolean autoGrowNullReferences;
    private final boolean autoGrowCollections;
    private final int maximumAutoGrowSize;
    
    public SpelParserConfiguration(final boolean autoGrowNullReferences, final boolean autoGrowCollections) {
        this(autoGrowNullReferences, autoGrowCollections, Integer.MAX_VALUE);
    }
    
    public SpelParserConfiguration(final boolean autoGrowNullReferences, final boolean autoGrowCollections, final int maximumAutoGrowSize) {
        this.autoGrowNullReferences = autoGrowNullReferences;
        this.autoGrowCollections = autoGrowCollections;
        this.maximumAutoGrowSize = maximumAutoGrowSize;
    }
    
    public boolean isAutoGrowNullReferences() {
        return this.autoGrowNullReferences;
    }
    
    public boolean isAutoGrowCollections() {
        return this.autoGrowCollections;
    }
    
    public int getMaximumAutoGrowSize() {
        return this.maximumAutoGrowSize;
    }
}
