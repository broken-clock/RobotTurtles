// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.multipart.support;

import javax.servlet.ServletException;

public class MissingServletRequestPartException extends ServletException
{
    private static final long serialVersionUID = -1255077391966870705L;
    private final String partName;
    
    public MissingServletRequestPartException(final String partName) {
        super("Required request part '" + partName + "' is not present.");
        this.partName = partName;
    }
    
    public String getRequestPartName() {
        return this.partName;
    }
}
