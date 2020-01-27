// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import java.util.prefs.BackingStoreException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import java.util.Properties;
import java.util.prefs.Preferences;
import org.springframework.beans.factory.InitializingBean;

public class PreferencesPlaceholderConfigurer extends PropertyPlaceholderConfigurer implements InitializingBean
{
    private String systemTreePath;
    private String userTreePath;
    private Preferences systemPrefs;
    private Preferences userPrefs;
    
    public void setSystemTreePath(final String systemTreePath) {
        this.systemTreePath = systemTreePath;
    }
    
    public void setUserTreePath(final String userTreePath) {
        this.userTreePath = userTreePath;
    }
    
    @Override
    public void afterPropertiesSet() {
        this.systemPrefs = ((this.systemTreePath != null) ? Preferences.systemRoot().node(this.systemTreePath) : Preferences.systemRoot());
        this.userPrefs = ((this.userTreePath != null) ? Preferences.userRoot().node(this.userTreePath) : Preferences.userRoot());
    }
    
    @Override
    protected String resolvePlaceholder(final String placeholder, final Properties props) {
        String path = null;
        String key = placeholder;
        final int endOfPath = placeholder.lastIndexOf(47);
        if (endOfPath != -1) {
            path = placeholder.substring(0, endOfPath);
            key = placeholder.substring(endOfPath + 1);
        }
        String value = this.resolvePlaceholder(path, key, this.userPrefs);
        if (value == null) {
            value = this.resolvePlaceholder(path, key, this.systemPrefs);
            if (value == null) {
                value = props.getProperty(placeholder);
            }
        }
        return value;
    }
    
    protected String resolvePlaceholder(final String path, final String key, final Preferences preferences) {
        if (path != null) {
            try {
                if (preferences.nodeExists(path)) {
                    return preferences.node(path).get(key, null);
                }
                return null;
            }
            catch (BackingStoreException ex) {
                throw new BeanDefinitionStoreException("Cannot access specified node path [" + path + "]", ex);
            }
        }
        return preferences.get(key, null);
    }
}
