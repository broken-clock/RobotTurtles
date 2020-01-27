// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web;

import java.util.Iterator;
import java.util.List;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import javax.servlet.ServletException;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import javax.servlet.ServletContext;
import java.util.Set;
import javax.servlet.annotation.HandlesTypes;
import javax.servlet.ServletContainerInitializer;

@HandlesTypes({ WebApplicationInitializer.class })
public class SpringServletContainerInitializer implements ServletContainerInitializer
{
    public void onStartup(final Set<Class<?>> webAppInitializerClasses, final ServletContext servletContext) throws ServletException {
        final List<WebApplicationInitializer> initializers = new LinkedList<WebApplicationInitializer>();
        if (webAppInitializerClasses != null) {
            for (final Class<?> waiClass : webAppInitializerClasses) {
                if (!waiClass.isInterface() && !Modifier.isAbstract(waiClass.getModifiers()) && WebApplicationInitializer.class.isAssignableFrom(waiClass)) {
                    try {
                        initializers.add((WebApplicationInitializer)waiClass.newInstance());
                    }
                    catch (Throwable ex) {
                        throw new ServletException("Failed to instantiate WebApplicationInitializer class", ex);
                    }
                }
            }
        }
        if (initializers.isEmpty()) {
            servletContext.log("No Spring WebApplicationInitializer types detected on classpath");
            return;
        }
        AnnotationAwareOrderComparator.sort(initializers);
        servletContext.log("Spring WebApplicationInitializers detected on classpath: " + initializers);
        for (final WebApplicationInitializer initializer : initializers) {
            initializer.onStartup(servletContext);
        }
    }
}
