// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.support;

import org.springframework.util.StringUtils;
import java.io.Serializable;

public class MutableSortDefinition implements SortDefinition, Serializable
{
    private String property;
    private boolean ignoreCase;
    private boolean ascending;
    private boolean toggleAscendingOnProperty;
    
    public MutableSortDefinition() {
        this.property = "";
        this.ignoreCase = true;
        this.ascending = true;
        this.toggleAscendingOnProperty = false;
    }
    
    public MutableSortDefinition(final SortDefinition source) {
        this.property = "";
        this.ignoreCase = true;
        this.ascending = true;
        this.toggleAscendingOnProperty = false;
        this.property = source.getProperty();
        this.ignoreCase = source.isIgnoreCase();
        this.ascending = source.isAscending();
    }
    
    public MutableSortDefinition(final String property, final boolean ignoreCase, final boolean ascending) {
        this.property = "";
        this.ignoreCase = true;
        this.ascending = true;
        this.toggleAscendingOnProperty = false;
        this.property = property;
        this.ignoreCase = ignoreCase;
        this.ascending = ascending;
    }
    
    public MutableSortDefinition(final boolean toggleAscendingOnSameProperty) {
        this.property = "";
        this.ignoreCase = true;
        this.ascending = true;
        this.toggleAscendingOnProperty = false;
        this.toggleAscendingOnProperty = toggleAscendingOnSameProperty;
    }
    
    public void setProperty(final String property) {
        if (!StringUtils.hasLength(property)) {
            this.property = "";
        }
        else {
            if (this.isToggleAscendingOnProperty()) {
                this.ascending = (!property.equals(this.property) || !this.ascending);
            }
            this.property = property;
        }
    }
    
    @Override
    public String getProperty() {
        return this.property;
    }
    
    public void setIgnoreCase(final boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }
    
    @Override
    public boolean isIgnoreCase() {
        return this.ignoreCase;
    }
    
    public void setAscending(final boolean ascending) {
        this.ascending = ascending;
    }
    
    @Override
    public boolean isAscending() {
        return this.ascending;
    }
    
    public void setToggleAscendingOnProperty(final boolean toggleAscendingOnProperty) {
        this.toggleAscendingOnProperty = toggleAscendingOnProperty;
    }
    
    public boolean isToggleAscendingOnProperty() {
        return this.toggleAscendingOnProperty;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SortDefinition)) {
            return false;
        }
        final SortDefinition otherSd = (SortDefinition)other;
        return this.getProperty().equals(otherSd.getProperty()) && this.isAscending() == otherSd.isAscending() && this.isIgnoreCase() == otherSd.isIgnoreCase();
    }
    
    @Override
    public int hashCode() {
        int hashCode = this.getProperty().hashCode();
        hashCode = 29 * hashCode + (this.isIgnoreCase() ? 1 : 0);
        hashCode = 29 * hashCode + (this.isAscending() ? 1 : 0);
        return hashCode;
    }
}
