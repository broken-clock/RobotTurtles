// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.bind;

import java.util.Iterator;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Array;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.validation.DataBinder;

public class WebDataBinder extends DataBinder
{
    public static final String DEFAULT_FIELD_MARKER_PREFIX = "_";
    public static final String DEFAULT_FIELD_DEFAULT_PREFIX = "!";
    private String fieldMarkerPrefix;
    private String fieldDefaultPrefix;
    private boolean bindEmptyMultipartFiles;
    
    public WebDataBinder(final Object target) {
        super(target);
        this.fieldMarkerPrefix = "_";
        this.fieldDefaultPrefix = "!";
        this.bindEmptyMultipartFiles = true;
    }
    
    public WebDataBinder(final Object target, final String objectName) {
        super(target, objectName);
        this.fieldMarkerPrefix = "_";
        this.fieldDefaultPrefix = "!";
        this.bindEmptyMultipartFiles = true;
    }
    
    public void setFieldMarkerPrefix(final String fieldMarkerPrefix) {
        this.fieldMarkerPrefix = fieldMarkerPrefix;
    }
    
    public String getFieldMarkerPrefix() {
        return this.fieldMarkerPrefix;
    }
    
    public void setFieldDefaultPrefix(final String fieldDefaultPrefix) {
        this.fieldDefaultPrefix = fieldDefaultPrefix;
    }
    
    public String getFieldDefaultPrefix() {
        return this.fieldDefaultPrefix;
    }
    
    public void setBindEmptyMultipartFiles(final boolean bindEmptyMultipartFiles) {
        this.bindEmptyMultipartFiles = bindEmptyMultipartFiles;
    }
    
    public boolean isBindEmptyMultipartFiles() {
        return this.bindEmptyMultipartFiles;
    }
    
    @Override
    protected void doBind(final MutablePropertyValues mpvs) {
        this.checkFieldDefaults(mpvs);
        this.checkFieldMarkers(mpvs);
        super.doBind(mpvs);
    }
    
    protected void checkFieldDefaults(final MutablePropertyValues mpvs) {
        if (this.getFieldDefaultPrefix() != null) {
            final String fieldDefaultPrefix = this.getFieldDefaultPrefix();
            final PropertyValue[] propertyValues;
            final PropertyValue[] pvArray = propertyValues = mpvs.getPropertyValues();
            for (final PropertyValue pv : propertyValues) {
                if (pv.getName().startsWith(fieldDefaultPrefix)) {
                    final String field = pv.getName().substring(fieldDefaultPrefix.length());
                    if (this.getPropertyAccessor().isWritableProperty(field) && !mpvs.contains(field)) {
                        mpvs.add(field, pv.getValue());
                    }
                    mpvs.removePropertyValue(pv);
                }
            }
        }
    }
    
    protected void checkFieldMarkers(final MutablePropertyValues mpvs) {
        if (this.getFieldMarkerPrefix() != null) {
            final String fieldMarkerPrefix = this.getFieldMarkerPrefix();
            final PropertyValue[] propertyValues;
            final PropertyValue[] pvArray = propertyValues = mpvs.getPropertyValues();
            for (final PropertyValue pv : propertyValues) {
                if (pv.getName().startsWith(fieldMarkerPrefix)) {
                    final String field = pv.getName().substring(fieldMarkerPrefix.length());
                    if (this.getPropertyAccessor().isWritableProperty(field) && !mpvs.contains(field)) {
                        final Class<?> fieldType = this.getPropertyAccessor().getPropertyType(field);
                        mpvs.add(field, this.getEmptyValue(field, fieldType));
                    }
                    mpvs.removePropertyValue(pv);
                }
            }
        }
    }
    
    protected Object getEmptyValue(final String field, final Class<?> fieldType) {
        if ((fieldType != null && Boolean.TYPE.equals(fieldType)) || Boolean.class.equals(fieldType)) {
            return Boolean.FALSE;
        }
        if (fieldType != null && fieldType.isArray()) {
            return Array.newInstance(fieldType.getComponentType(), 0);
        }
        return null;
    }
    
    protected void bindMultipart(final Map<String, List<MultipartFile>> multipartFiles, final MutablePropertyValues mpvs) {
        for (final Map.Entry<String, List<MultipartFile>> entry : multipartFiles.entrySet()) {
            final String key = entry.getKey();
            final List<MultipartFile> values = entry.getValue();
            if (values.size() == 1) {
                final MultipartFile value = values.get(0);
                if (!this.isBindEmptyMultipartFiles() && value.isEmpty()) {
                    continue;
                }
                mpvs.add(key, value);
            }
            else {
                mpvs.add(key, values);
            }
        }
    }
}
