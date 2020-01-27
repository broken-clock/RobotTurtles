// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.support;

import org.springframework.beans.PropertyEditorRegistrySupport;
import org.springframework.core.io.support.ResourceArrayPropertyEditor;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.beans.propertyeditors.ClassArrayEditor;
import org.springframework.beans.propertyeditors.ClassEditor;
import org.springframework.beans.propertyeditors.URIEditor;
import java.net.URI;
import org.springframework.beans.propertyeditors.URLEditor;
import java.net.URL;
import org.springframework.beans.propertyeditors.FileEditor;
import java.io.File;
import org.springframework.beans.propertyeditors.InputSourceEditor;
import org.xml.sax.InputSource;
import org.springframework.beans.propertyeditors.InputStreamEditor;
import java.io.InputStream;
import org.springframework.core.io.ContextResource;
import java.beans.PropertyEditor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.env.PropertyResolver;
import org.springframework.beans.PropertyEditorRegistrar;

public class ResourceEditorRegistrar implements PropertyEditorRegistrar
{
    private final PropertyResolver propertyResolver;
    private final ResourceLoader resourceLoader;
    
    @Deprecated
    public ResourceEditorRegistrar(final ResourceLoader resourceLoader) {
        this(resourceLoader, new StandardEnvironment());
    }
    
    public ResourceEditorRegistrar(final ResourceLoader resourceLoader, final PropertyResolver propertyResolver) {
        this.resourceLoader = resourceLoader;
        this.propertyResolver = propertyResolver;
    }
    
    @Override
    public void registerCustomEditors(final PropertyEditorRegistry registry) {
        final ResourceEditor baseEditor = new ResourceEditor(this.resourceLoader, this.propertyResolver);
        this.doRegisterEditor(registry, Resource.class, baseEditor);
        this.doRegisterEditor(registry, ContextResource.class, baseEditor);
        this.doRegisterEditor(registry, InputStream.class, new InputStreamEditor(baseEditor));
        this.doRegisterEditor(registry, InputSource.class, new InputSourceEditor(baseEditor));
        this.doRegisterEditor(registry, File.class, new FileEditor(baseEditor));
        this.doRegisterEditor(registry, URL.class, new URLEditor(baseEditor));
        final ClassLoader classLoader = this.resourceLoader.getClassLoader();
        this.doRegisterEditor(registry, URI.class, new URIEditor(classLoader));
        this.doRegisterEditor(registry, Class.class, new ClassEditor(classLoader));
        this.doRegisterEditor(registry, Class[].class, new ClassArrayEditor(classLoader));
        if (this.resourceLoader instanceof ResourcePatternResolver) {
            this.doRegisterEditor(registry, Resource[].class, new ResourceArrayPropertyEditor((ResourcePatternResolver)this.resourceLoader, this.propertyResolver));
        }
    }
    
    private void doRegisterEditor(final PropertyEditorRegistry registry, final Class<?> requiredType, final PropertyEditor editor) {
        if (registry instanceof PropertyEditorRegistrySupport) {
            ((PropertyEditorRegistrySupport)registry).overrideDefaultEditor(requiredType, editor);
        }
        else {
            registry.registerCustomEditor(requiredType, editor);
        }
    }
}
