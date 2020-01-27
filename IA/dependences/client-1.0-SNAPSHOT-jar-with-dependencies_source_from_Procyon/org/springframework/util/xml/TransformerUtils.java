// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util.xml;

import org.springframework.util.Assert;
import javax.xml.transform.Transformer;

public abstract class TransformerUtils
{
    public static final int DEFAULT_INDENT_AMOUNT = 2;
    
    public static void enableIndenting(final Transformer transformer) {
        enableIndenting(transformer, 2);
    }
    
    public static void enableIndenting(final Transformer transformer, final int indentAmount) {
        Assert.notNull(transformer, "Transformer must not be null");
        Assert.isTrue(indentAmount > -1, "The indent amount cannot be less than zero : got " + indentAmount);
        transformer.setOutputProperty("indent", "yes");
        try {
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(indentAmount));
        }
        catch (IllegalArgumentException ex) {}
    }
    
    public static void disableIndenting(final Transformer transformer) {
        Assert.notNull(transformer, "Transformer must not be null");
        transformer.setOutputProperty("indent", "no");
    }
}
