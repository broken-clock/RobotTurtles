// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web;

import javax.servlet.ServletException;

public class HttpSessionRequiredException extends ServletException
{
    public HttpSessionRequiredException(final String msg) {
        super(msg);
    }
}
