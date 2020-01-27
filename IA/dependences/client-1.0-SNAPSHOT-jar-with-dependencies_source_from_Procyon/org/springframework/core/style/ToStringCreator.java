// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.style;

import org.springframework.util.Assert;

public class ToStringCreator
{
    private static final ToStringStyler DEFAULT_TO_STRING_STYLER;
    private StringBuilder buffer;
    private ToStringStyler styler;
    private Object object;
    private boolean styledFirstField;
    
    public ToStringCreator(final Object obj) {
        this(obj, (ToStringStyler)null);
    }
    
    public ToStringCreator(final Object obj, final ValueStyler styler) {
        this(obj, new DefaultToStringStyler((styler != null) ? styler : StylerUtils.DEFAULT_VALUE_STYLER));
    }
    
    public ToStringCreator(final Object obj, final ToStringStyler styler) {
        this.buffer = new StringBuilder(512);
        Assert.notNull(obj, "The object to be styled must not be null");
        this.object = obj;
        (this.styler = ((styler != null) ? styler : ToStringCreator.DEFAULT_TO_STRING_STYLER)).styleStart(this.buffer, this.object);
    }
    
    public ToStringCreator append(final String fieldName, final byte value) {
        return this.append(fieldName, new Byte(value));
    }
    
    public ToStringCreator append(final String fieldName, final short value) {
        return this.append(fieldName, new Short(value));
    }
    
    public ToStringCreator append(final String fieldName, final int value) {
        return this.append(fieldName, new Integer(value));
    }
    
    public ToStringCreator append(final String fieldName, final long value) {
        return this.append(fieldName, new Long(value));
    }
    
    public ToStringCreator append(final String fieldName, final float value) {
        return this.append(fieldName, new Float(value));
    }
    
    public ToStringCreator append(final String fieldName, final double value) {
        return this.append(fieldName, new Double(value));
    }
    
    public ToStringCreator append(final String fieldName, final boolean value) {
        return this.append(fieldName, (Object)value);
    }
    
    public ToStringCreator append(final String fieldName, final Object value) {
        this.printFieldSeparatorIfNecessary();
        this.styler.styleField(this.buffer, fieldName, value);
        return this;
    }
    
    private void printFieldSeparatorIfNecessary() {
        if (this.styledFirstField) {
            this.styler.styleFieldSeparator(this.buffer);
        }
        else {
            this.styledFirstField = true;
        }
    }
    
    public ToStringCreator append(final Object value) {
        this.styler.styleValue(this.buffer, value);
        return this;
    }
    
    @Override
    public String toString() {
        this.styler.styleEnd(this.buffer, this.object);
        return this.buffer.toString();
    }
    
    static {
        DEFAULT_TO_STRING_STYLER = new DefaultToStringStyler(StylerUtils.DEFAULT_VALUE_STYLER);
    }
}
