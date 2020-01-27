// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

import org.springframework.util.ClassUtils;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.LinkedHashMap;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import java.math.BigInteger;
import java.math.BigDecimal;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CharacterEditor;
import org.springframework.beans.propertyeditors.CharArrayPropertyEditor;
import org.springframework.beans.propertyeditors.ByteArrayPropertyEditor;
import org.springframework.beans.propertyeditors.CustomMapEditor;
import java.util.SortedMap;
import java.util.List;
import java.util.SortedSet;
import java.util.Set;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import java.util.Collection;
import org.springframework.beans.propertyeditors.ZoneIdEditor;
import org.springframework.beans.propertyeditors.UUIDEditor;
import java.util.UUID;
import org.springframework.beans.propertyeditors.URLEditor;
import java.net.URL;
import org.springframework.beans.propertyeditors.URIEditor;
import java.net.URI;
import org.springframework.beans.propertyeditors.TimeZoneEditor;
import java.util.TimeZone;
import org.springframework.core.io.support.ResourceArrayPropertyEditor;
import org.springframework.core.io.Resource;
import org.springframework.beans.propertyeditors.PropertiesEditor;
import java.util.Properties;
import org.springframework.beans.propertyeditors.PatternEditor;
import java.util.regex.Pattern;
import org.springframework.beans.propertyeditors.LocaleEditor;
import java.util.Locale;
import org.springframework.beans.propertyeditors.InputSourceEditor;
import org.xml.sax.InputSource;
import org.springframework.beans.propertyeditors.InputStreamEditor;
import java.io.InputStream;
import org.springframework.beans.propertyeditors.FileEditor;
import java.io.File;
import org.springframework.beans.propertyeditors.CurrencyEditor;
import java.util.Currency;
import org.springframework.beans.propertyeditors.ClassArrayEditor;
import org.springframework.beans.propertyeditors.ClassEditor;
import org.springframework.beans.propertyeditors.CharsetEditor;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.beans.PropertyEditor;
import java.util.Map;
import org.springframework.core.convert.ConversionService;

public class PropertyEditorRegistrySupport implements PropertyEditorRegistry
{
    private static Class<?> zoneIdClass;
    private ConversionService conversionService;
    private boolean defaultEditorsActive;
    private boolean configValueEditorsActive;
    private Map<Class<?>, PropertyEditor> defaultEditors;
    private Map<Class<?>, PropertyEditor> overriddenDefaultEditors;
    private Map<Class<?>, PropertyEditor> customEditors;
    private Map<String, CustomEditorHolder> customEditorsForPath;
    private Map<Class<?>, PropertyEditor> customEditorCache;
    
    public PropertyEditorRegistrySupport() {
        this.defaultEditorsActive = false;
        this.configValueEditorsActive = false;
    }
    
    public void setConversionService(final ConversionService conversionService) {
        this.conversionService = conversionService;
    }
    
    public ConversionService getConversionService() {
        return this.conversionService;
    }
    
    protected void registerDefaultEditors() {
        this.defaultEditorsActive = true;
    }
    
    public void useConfigValueEditors() {
        this.configValueEditorsActive = true;
    }
    
    public void overrideDefaultEditor(final Class<?> requiredType, final PropertyEditor propertyEditor) {
        if (this.overriddenDefaultEditors == null) {
            this.overriddenDefaultEditors = new HashMap<Class<?>, PropertyEditor>();
        }
        this.overriddenDefaultEditors.put(requiredType, propertyEditor);
    }
    
    public PropertyEditor getDefaultEditor(final Class<?> requiredType) {
        if (!this.defaultEditorsActive) {
            return null;
        }
        if (this.overriddenDefaultEditors != null) {
            final PropertyEditor editor = this.overriddenDefaultEditors.get(requiredType);
            if (editor != null) {
                return editor;
            }
        }
        if (this.defaultEditors == null) {
            this.createDefaultEditors();
        }
        return this.defaultEditors.get(requiredType);
    }
    
    private void createDefaultEditors() {
        (this.defaultEditors = new HashMap<Class<?>, PropertyEditor>(64)).put(Charset.class, new CharsetEditor());
        this.defaultEditors.put(Class.class, new ClassEditor());
        this.defaultEditors.put(Class[].class, new ClassArrayEditor());
        this.defaultEditors.put(Currency.class, new CurrencyEditor());
        this.defaultEditors.put(File.class, new FileEditor());
        this.defaultEditors.put(InputStream.class, new InputStreamEditor());
        this.defaultEditors.put(InputSource.class, new InputSourceEditor());
        this.defaultEditors.put(Locale.class, new LocaleEditor());
        this.defaultEditors.put(Pattern.class, new PatternEditor());
        this.defaultEditors.put(Properties.class, new PropertiesEditor());
        this.defaultEditors.put(Resource[].class, new ResourceArrayPropertyEditor());
        this.defaultEditors.put(TimeZone.class, new TimeZoneEditor());
        this.defaultEditors.put(URI.class, new URIEditor());
        this.defaultEditors.put(URL.class, new URLEditor());
        this.defaultEditors.put(UUID.class, new UUIDEditor());
        if (PropertyEditorRegistrySupport.zoneIdClass != null) {
            this.defaultEditors.put(PropertyEditorRegistrySupport.zoneIdClass, new ZoneIdEditor());
        }
        this.defaultEditors.put(Collection.class, new CustomCollectionEditor(Collection.class));
        this.defaultEditors.put(Set.class, new CustomCollectionEditor(Set.class));
        this.defaultEditors.put(SortedSet.class, new CustomCollectionEditor(SortedSet.class));
        this.defaultEditors.put(List.class, new CustomCollectionEditor(List.class));
        this.defaultEditors.put(SortedMap.class, new CustomMapEditor(SortedMap.class));
        this.defaultEditors.put(byte[].class, new ByteArrayPropertyEditor());
        this.defaultEditors.put(char[].class, new CharArrayPropertyEditor());
        this.defaultEditors.put(Character.TYPE, new CharacterEditor(false));
        this.defaultEditors.put(Character.class, new CharacterEditor(true));
        this.defaultEditors.put(Boolean.TYPE, new CustomBooleanEditor(false));
        this.defaultEditors.put(Boolean.class, new CustomBooleanEditor(true));
        this.defaultEditors.put(Byte.TYPE, new CustomNumberEditor(Byte.class, false));
        this.defaultEditors.put(Byte.class, new CustomNumberEditor(Byte.class, true));
        this.defaultEditors.put(Short.TYPE, new CustomNumberEditor(Short.class, false));
        this.defaultEditors.put(Short.class, new CustomNumberEditor(Short.class, true));
        this.defaultEditors.put(Integer.TYPE, new CustomNumberEditor(Integer.class, false));
        this.defaultEditors.put(Integer.class, new CustomNumberEditor(Integer.class, true));
        this.defaultEditors.put(Long.TYPE, new CustomNumberEditor(Long.class, false));
        this.defaultEditors.put(Long.class, new CustomNumberEditor(Long.class, true));
        this.defaultEditors.put(Float.TYPE, new CustomNumberEditor(Float.class, false));
        this.defaultEditors.put(Float.class, new CustomNumberEditor(Float.class, true));
        this.defaultEditors.put(Double.TYPE, new CustomNumberEditor(Double.class, false));
        this.defaultEditors.put(Double.class, new CustomNumberEditor(Double.class, true));
        this.defaultEditors.put(BigDecimal.class, new CustomNumberEditor(BigDecimal.class, true));
        this.defaultEditors.put(BigInteger.class, new CustomNumberEditor(BigInteger.class, true));
        if (this.configValueEditorsActive) {
            final StringArrayPropertyEditor sae = new StringArrayPropertyEditor();
            this.defaultEditors.put(String[].class, sae);
            this.defaultEditors.put(short[].class, sae);
            this.defaultEditors.put(int[].class, sae);
            this.defaultEditors.put(long[].class, sae);
        }
    }
    
    protected void copyDefaultEditorsTo(final PropertyEditorRegistrySupport target) {
        target.defaultEditorsActive = this.defaultEditorsActive;
        target.configValueEditorsActive = this.configValueEditorsActive;
        target.defaultEditors = this.defaultEditors;
        target.overriddenDefaultEditors = this.overriddenDefaultEditors;
    }
    
    @Override
    public void registerCustomEditor(final Class<?> requiredType, final PropertyEditor propertyEditor) {
        this.registerCustomEditor(requiredType, null, propertyEditor);
    }
    
    @Override
    public void registerCustomEditor(final Class<?> requiredType, final String propertyPath, final PropertyEditor propertyEditor) {
        if (requiredType == null && propertyPath == null) {
            throw new IllegalArgumentException("Either requiredType or propertyPath is required");
        }
        if (propertyPath != null) {
            if (this.customEditorsForPath == null) {
                this.customEditorsForPath = new LinkedHashMap<String, CustomEditorHolder>(16);
            }
            this.customEditorsForPath.put(propertyPath, new CustomEditorHolder(propertyEditor, (Class)requiredType));
        }
        else {
            if (this.customEditors == null) {
                this.customEditors = new LinkedHashMap<Class<?>, PropertyEditor>(16);
            }
            this.customEditors.put(requiredType, propertyEditor);
            this.customEditorCache = null;
        }
    }
    
    @Override
    public PropertyEditor findCustomEditor(final Class<?> requiredType, final String propertyPath) {
        Class<?> requiredTypeToUse = requiredType;
        if (propertyPath != null) {
            if (this.customEditorsForPath != null) {
                PropertyEditor editor = this.getCustomEditor(propertyPath, requiredType);
                if (editor == null) {
                    final List<String> strippedPaths = new LinkedList<String>();
                    this.addStrippedPropertyPaths(strippedPaths, "", propertyPath);
                    String strippedPath;
                    for (Iterator<String> it = strippedPaths.iterator(); it.hasNext() && editor == null; editor = this.getCustomEditor(strippedPath, requiredType)) {
                        strippedPath = it.next();
                    }
                }
                if (editor != null) {
                    return editor;
                }
            }
            if (requiredType == null) {
                requiredTypeToUse = this.getPropertyType(propertyPath);
            }
        }
        return this.getCustomEditor(requiredTypeToUse);
    }
    
    public boolean hasCustomEditorForElement(final Class<?> elementType, final String propertyPath) {
        if (propertyPath != null && this.customEditorsForPath != null) {
            for (final Map.Entry<String, CustomEditorHolder> entry : this.customEditorsForPath.entrySet()) {
                if (PropertyAccessorUtils.matchesProperty(entry.getKey(), propertyPath) && entry.getValue().getPropertyEditor(elementType) != null) {
                    return true;
                }
            }
        }
        return elementType != null && this.customEditors != null && this.customEditors.containsKey(elementType);
    }
    
    protected Class<?> getPropertyType(final String propertyPath) {
        return null;
    }
    
    private PropertyEditor getCustomEditor(final String propertyName, final Class<?> requiredType) {
        final CustomEditorHolder holder = this.customEditorsForPath.get(propertyName);
        return (holder != null) ? holder.getPropertyEditor(requiredType) : null;
    }
    
    private PropertyEditor getCustomEditor(final Class<?> requiredType) {
        if (requiredType == null || this.customEditors == null) {
            return null;
        }
        PropertyEditor editor = this.customEditors.get(requiredType);
        if (editor == null) {
            if (this.customEditorCache != null) {
                editor = this.customEditorCache.get(requiredType);
            }
            if (editor == null) {
                final Iterator<Class<?>> it = this.customEditors.keySet().iterator();
                while (it.hasNext() && editor == null) {
                    final Class<?> key = it.next();
                    if (key.isAssignableFrom(requiredType)) {
                        editor = this.customEditors.get(key);
                        if (this.customEditorCache == null) {
                            this.customEditorCache = new HashMap<Class<?>, PropertyEditor>();
                        }
                        this.customEditorCache.put(requiredType, editor);
                    }
                }
            }
        }
        return editor;
    }
    
    protected Class<?> guessPropertyTypeFromEditors(final String propertyName) {
        if (this.customEditorsForPath != null) {
            CustomEditorHolder editorHolder = this.customEditorsForPath.get(propertyName);
            if (editorHolder == null) {
                final List<String> strippedPaths = new LinkedList<String>();
                this.addStrippedPropertyPaths(strippedPaths, "", propertyName);
                String strippedName;
                for (Iterator<String> it = strippedPaths.iterator(); it.hasNext() && editorHolder == null; editorHolder = this.customEditorsForPath.get(strippedName)) {
                    strippedName = it.next();
                }
            }
            if (editorHolder != null) {
                return editorHolder.getRegisteredType();
            }
        }
        return null;
    }
    
    protected void copyCustomEditorsTo(final PropertyEditorRegistry target, final String nestedProperty) {
        final String actualPropertyName = (nestedProperty != null) ? PropertyAccessorUtils.getPropertyName(nestedProperty) : null;
        if (this.customEditors != null) {
            for (final Map.Entry<Class<?>, PropertyEditor> entry : this.customEditors.entrySet()) {
                target.registerCustomEditor(entry.getKey(), entry.getValue());
            }
        }
        if (this.customEditorsForPath != null) {
            for (final Map.Entry<String, CustomEditorHolder> entry2 : this.customEditorsForPath.entrySet()) {
                final String editorPath = entry2.getKey();
                final CustomEditorHolder editorHolder = entry2.getValue();
                if (nestedProperty != null) {
                    final int pos = PropertyAccessorUtils.getFirstNestedPropertySeparatorIndex(editorPath);
                    if (pos == -1) {
                        continue;
                    }
                    final String editorNestedProperty = editorPath.substring(0, pos);
                    final String editorNestedPath = editorPath.substring(pos + 1);
                    if (!editorNestedProperty.equals(nestedProperty) && !editorNestedProperty.equals(actualPropertyName)) {
                        continue;
                    }
                    target.registerCustomEditor(editorHolder.getRegisteredType(), editorNestedPath, editorHolder.getPropertyEditor());
                }
                else {
                    target.registerCustomEditor(editorHolder.getRegisteredType(), editorPath, editorHolder.getPropertyEditor());
                }
            }
        }
    }
    
    private void addStrippedPropertyPaths(final List<String> strippedPaths, final String nestedPath, final String propertyPath) {
        final int startIndex = propertyPath.indexOf(91);
        if (startIndex != -1) {
            final int endIndex = propertyPath.indexOf(93);
            if (endIndex != -1) {
                final String prefix = propertyPath.substring(0, startIndex);
                final String key = propertyPath.substring(startIndex, endIndex + 1);
                final String suffix = propertyPath.substring(endIndex + 1, propertyPath.length());
                strippedPaths.add(nestedPath + prefix + suffix);
                this.addStrippedPropertyPaths(strippedPaths, nestedPath + prefix, suffix);
                this.addStrippedPropertyPaths(strippedPaths, nestedPath + prefix + key, suffix);
            }
        }
    }
    
    static {
        try {
            PropertyEditorRegistrySupport.zoneIdClass = PropertyEditorRegistrySupport.class.getClassLoader().loadClass("java.time.ZoneId");
        }
        catch (ClassNotFoundException ex) {
            PropertyEditorRegistrySupport.zoneIdClass = null;
        }
    }
    
    private static class CustomEditorHolder
    {
        private final PropertyEditor propertyEditor;
        private final Class<?> registeredType;
        
        private CustomEditorHolder(final PropertyEditor propertyEditor, final Class<?> registeredType) {
            this.propertyEditor = propertyEditor;
            this.registeredType = registeredType;
        }
        
        private PropertyEditor getPropertyEditor() {
            return this.propertyEditor;
        }
        
        private Class<?> getRegisteredType() {
            return this.registeredType;
        }
        
        private PropertyEditor getPropertyEditor(final Class<?> requiredType) {
            if (this.registeredType == null || (requiredType != null && (ClassUtils.isAssignable(this.registeredType, requiredType) || ClassUtils.isAssignable(requiredType, this.registeredType))) || (requiredType == null && !Collection.class.isAssignableFrom(this.registeredType) && !this.registeredType.isArray())) {
                return this.propertyEditor;
            }
            return null;
        }
    }
}
