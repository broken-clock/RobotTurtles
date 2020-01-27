// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public interface HttpRequestHandler
{
    void handleRequest(final HttpServletRequest p0, final HttpServletResponse p1) throws ServletException, IOException;
}
