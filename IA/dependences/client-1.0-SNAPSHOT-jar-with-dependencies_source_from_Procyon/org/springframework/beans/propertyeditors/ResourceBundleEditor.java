// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.propertyeditors;

import java.util.Locale;
import org.springframework.util.StringUtils;
import java.util.ResourceBundle;
import org.springframework.util.Assert;
import java.beans.PropertyEditorSupport;

public class ResourceBundleEditor extends PropertyEditorSupport
{
    public static final String BASE_NAME_SEPARATOR = "_";
    
    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        Assert.hasText(text, "'text' must not be empty");
        final String rawBaseName = text.trim();
        final int indexOfBaseNameSeparator = rawBaseName.indexOf("_");
        ResourceBundle bundle;
        if (indexOfBaseNameSeparator == -1) {
            bundle = ResourceBundle.getBundle(rawBaseName);
        }
        else {
            final String baseName = rawBaseName.substring(0, indexOfBaseNameSeparator);
            if (!StringUtils.hasText(baseName)) {
                throw new IllegalArgumentException("Bad ResourceBundle name : received '" + text + "' as argument to 'setAsText(String value)'.");
            }
            final String localeString = rawBaseName.substring(indexOfBaseNameSeparator + 1);
            final Locale locale = StringUtils.parseLocaleString(localeString);
            bundle = (StringUtils.hasText(localeString) ? ResourceBundle.getBundle(baseName, locale) : ResourceBundle.getBundle(baseName));
        }
        this.setValue(bundle);
    }
}
