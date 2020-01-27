// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.parsing;

import org.springframework.util.StringUtils;

public class QualifierEntry implements ParseState.Entry
{
    private String typeName;
    
    public QualifierEntry(final String typeName) {
        if (!StringUtils.hasText(typeName)) {
            throw new IllegalArgumentException("Invalid qualifier type '" + typeName + "'.");
        }
        this.typeName = typeName;
    }
    
    @Override
    public String toString() {
        return "Qualifier '" + this.typeName + "'";
    }
}
