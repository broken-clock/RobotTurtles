// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonParser;

public class StackTraceElementDeserializer extends StdScalarDeserializer<StackTraceElement>
{
    private static final long serialVersionUID = 1L;
    
    public StackTraceElementDeserializer() {
        super(StackTraceElement.class);
    }
    
    @Override
    public StackTraceElement deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        JsonToken t = p.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            String className = "";
            String methodName = "";
            String fileName = "";
            String moduleName = null;
            String moduleVersion = null;
            String classLoaderName = null;
            int lineNumber = -1;
            while ((t = p.nextValue()) != JsonToken.END_OBJECT) {
                final String propName = p.getCurrentName();
                if ("className".equals(propName)) {
                    className = p.getText();
                }
                else if ("classLoaderName".equals(propName)) {
                    classLoaderName = p.getText();
                }
                else if ("fileName".equals(propName)) {
                    fileName = p.getText();
                }
                else if ("lineNumber".equals(propName)) {
                    if (t.isNumeric()) {
                        lineNumber = p.getIntValue();
                    }
                    else {
                        lineNumber = this._parseIntPrimitive(p, ctxt);
                    }
                }
                else if ("methodName".equals(propName)) {
                    methodName = p.getText();
                }
                else if (!"nativeMethod".equals(propName)) {
                    if ("moduleName".equals(propName)) {
                        moduleName = p.getText();
                    }
                    else if ("moduleVersion".equals(propName)) {
                        moduleVersion = p.getText();
                    }
                    else if (!"declaringClass".equals(propName)) {
                        if (!"format".equals(propName)) {
                            this.handleUnknownProperty(p, ctxt, this._valueClass, propName);
                        }
                    }
                }
                p.skipChildren();
            }
            return this.constructValue(ctxt, className, methodName, fileName, lineNumber, moduleName, moduleVersion, classLoaderName);
        }
        if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
            p.nextToken();
            final StackTraceElement value = this.deserialize(p, ctxt);
            if (p.nextToken() != JsonToken.END_ARRAY) {
                this.handleMissingEndArrayForSingle(p, ctxt);
            }
            return value;
        }
        return (StackTraceElement)ctxt.handleUnexpectedToken(this._valueClass, p);
    }
    
    @Deprecated
    protected StackTraceElement constructValue(final DeserializationContext ctxt, final String className, final String methodName, final String fileName, final int lineNumber, final String moduleName, final String moduleVersion) {
        return this.constructValue(ctxt, className, methodName, fileName, lineNumber, moduleName, moduleVersion, null);
    }
    
    protected StackTraceElement constructValue(final DeserializationContext ctxt, final String className, final String methodName, final String fileName, final int lineNumber, final String moduleName, final String moduleVersion, final String classLoaderName) {
        return new StackTraceElement(className, methodName, fileName, lineNumber);
    }
}
