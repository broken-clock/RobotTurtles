// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.propertyeditors;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.ClassUtils;
import java.beans.PropertyEditorSupport;

public class ClassArrayEditor extends PropertyEditorSupport
{
    private final ClassLoader classLoader;
    
    public ClassArrayEditor() {
        this(null);
    }
    
    public ClassArrayEditor(final ClassLoader classLoader) {
        this.classLoader = ((classLoader != null) ? classLoader : ClassUtils.getDefaultClassLoader());
    }
    
    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        if (StringUtils.hasText(text)) {
            final String[] classNames = StringUtils.commaDelimitedListToStringArray(text);
            final Class<?>[] classes = (Class<?>[])new Class[classNames.length];
            for (int i = 0; i < classNames.length; ++i) {
                final String className = classNames[i].trim();
                classes[i] = ClassUtils.resolveClassName(className, this.classLoader);
            }
            this.setValue(classes);
        }
        else {
            this.setValue(null);
        }
    }
    
    @Override
    public String getAsText() {
        final Class<?>[] classes = (Class<?>[])this.getValue();
        if (ObjectUtils.isEmpty(classes)) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < classes.length; ++i) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(ClassUtils.getQualifiedName(classes[i]));
        }
        return sb.toString();
    }
}
