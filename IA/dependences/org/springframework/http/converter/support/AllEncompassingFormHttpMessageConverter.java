// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.converter.support;

import org.springframework.util.ClassUtils;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;

public class AllEncompassingFormHttpMessageConverter extends FormHttpMessageConverter
{
    private static final boolean jaxb2Present;
    private static final boolean jackson2Present;
    private static final boolean jacksonPresent;
    
    public AllEncompassingFormHttpMessageConverter() {
        this.addPartConverter(new SourceHttpMessageConverter<Object>());
        if (AllEncompassingFormHttpMessageConverter.jaxb2Present) {
            this.addPartConverter(new Jaxb2RootElementHttpMessageConverter());
        }
        if (AllEncompassingFormHttpMessageConverter.jackson2Present) {
            this.addPartConverter(new MappingJackson2HttpMessageConverter());
        }
        else if (AllEncompassingFormHttpMessageConverter.jacksonPresent) {
            this.addPartConverter(new MappingJacksonHttpMessageConverter());
        }
    }
    
    static {
        jaxb2Present = ClassUtils.isPresent("javax.xml.bind.Binder", AllEncompassingFormHttpMessageConverter.class.getClassLoader());
        jackson2Present = (ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", AllEncompassingFormHttpMessageConverter.class.getClassLoader()) && ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", AllEncompassingFormHttpMessageConverter.class.getClassLoader()));
        jacksonPresent = (ClassUtils.isPresent("org.codehaus.jackson.map.ObjectMapper", AllEncompassingFormHttpMessageConverter.class.getClassLoader()) && ClassUtils.isPresent("org.codehaus.jackson.JsonGenerator", AllEncompassingFormHttpMessageConverter.class.getClassLoader()));
    }
}
