// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.converter.xml;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;

@Deprecated
public class XmlAwareFormHttpMessageConverter extends FormHttpMessageConverter
{
    public XmlAwareFormHttpMessageConverter() {
        this.addPartConverter(new SourceHttpMessageConverter<Object>());
    }
}
