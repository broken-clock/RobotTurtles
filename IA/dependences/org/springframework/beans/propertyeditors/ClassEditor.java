// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.propertyeditors;

import org.springframework.util.StringUtils;
import org.springframework.util.ClassUtils;
import java.beans.PropertyEditorSupport;

public class ClassEditor extends PropertyEditorSupport
{
    private final ClassLoader classLoader;
    
    public ClassEditor() {
        this(null);
    }
    
    public ClassEditor(final ClassLoader classLoader) {
        this.classLoader = ((classLoader != null) ? classLoader : ClassUtils.getDefaultClassLoader());
    }
    
    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        if (StringUtils.hasText(text)) {
            this.setValue(ClassUtils.resolveClassName(text.trim(), this.classLoader));
        }
        else {
            this.setValue(null);
        }
    }
    
    @Override
    public String getAsText() {
        final Class<?> clazz = (Class<?>)this.getValue();
        if (clazz != null) {
            return ClassUtils.getQualifiedName(clazz);
        }
        return "";
    }
}
