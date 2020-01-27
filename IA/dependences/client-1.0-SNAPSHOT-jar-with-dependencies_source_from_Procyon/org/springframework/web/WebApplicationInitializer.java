// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web;

import javax.servlet.ServletException;
import javax.servlet.ServletContext;

public interface WebApplicationInitializer
{
    void onStartup(final ServletContext p0) throws ServletException;
}
