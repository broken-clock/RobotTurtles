// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.Serializable;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;

public abstract class FilteredBeanPropertyWriter
{
    public static BeanPropertyWriter constructViewBased(final BeanPropertyWriter base, final Class<?>[] viewsToIncludeIn) {
        if (viewsToIncludeIn.length == 1) {
            return new SingleView(base, viewsToIncludeIn[0]);
        }
        return new MultiView(base, viewsToIncludeIn);
    }
    
    private static final class SingleView extends BeanPropertyWriter implements Serializable
    {
        private static final long serialVersionUID = 1L;
        protected final BeanPropertyWriter _delegate;
        protected final Class<?> _view;
        
        protected SingleView(final BeanPropertyWriter delegate, final Class<?> view) {
            super(delegate);
            this._delegate = delegate;
            this._view = view;
        }
        
        @Override
        public SingleView rename(final NameTransformer transformer) {
            return new SingleView(this._delegate.rename(transformer), this._view);
        }
        
        @Override
        public void assignSerializer(final JsonSerializer<Object> ser) {
            this._delegate.assignSerializer(ser);
        }
        
        @Override
        public void assignNullSerializer(final JsonSerializer<Object> nullSer) {
            this._delegate.assignNullSerializer(nullSer);
        }
        
        @Override
        public void serializeAsField(final Object bean, final JsonGenerator gen, final SerializerProvider prov) throws Exception {
            final Class<?> activeView = prov.getActiveView();
            if (activeView == null || this._view.isAssignableFrom(activeView)) {
                this._delegate.serializeAsField(bean, gen, prov);
            }
            else {
                this._delegate.serializeAsOmittedField(bean, gen, prov);
            }
        }
        
        @Override
        public void serializeAsElement(final Object bean, final JsonGenerator gen, final SerializerProvider prov) throws Exception {
            final Class<?> activeView = prov.getActiveView();
            if (activeView == null || this._view.isAssignableFrom(activeView)) {
                this._delegate.serializeAsElement(bean, gen, prov);
            }
            else {
                this._delegate.serializeAsPlaceholder(bean, gen, prov);
            }
        }
        
        @Override
        public void depositSchemaProperty(final JsonObjectFormatVisitor v, final SerializerProvider provider) throws JsonMappingException {
            final Class<?> activeView = provider.getActiveView();
            if (activeView == null || this._view.isAssignableFrom(activeView)) {
                super.depositSchemaProperty(v, provider);
            }
        }
    }
    
    private static final class MultiView extends BeanPropertyWriter implements Serializable
    {
        private static final long serialVersionUID = 1L;
        protected final BeanPropertyWriter _delegate;
        protected final Class<?>[] _views;
        
        protected MultiView(final BeanPropertyWriter delegate, final Class<?>[] views) {
            super(delegate);
            this._delegate = delegate;
            this._views = views;
        }
        
        @Override
        public MultiView rename(final NameTransformer transformer) {
            return new MultiView(this._delegate.rename(transformer), this._views);
        }
        
        @Override
        public void assignSerializer(final JsonSerializer<Object> ser) {
            this._delegate.assignSerializer(ser);
        }
        
        @Override
        public void assignNullSerializer(final JsonSerializer<Object> nullSer) {
            this._delegate.assignNullSerializer(nullSer);
        }
        
        @Override
        public void serializeAsField(final Object bean, final JsonGenerator gen, final SerializerProvider prov) throws Exception {
            if (this._inView(prov.getActiveView())) {
                this._delegate.serializeAsField(bean, gen, prov);
                return;
            }
            this._delegate.serializeAsOmittedField(bean, gen, prov);
        }
        
        @Override
        public void serializeAsElement(final Object bean, final JsonGenerator gen, final SerializerProvider prov) throws Exception {
            if (this._inView(prov.getActiveView())) {
                this._delegate.serializeAsElement(bean, gen, prov);
                return;
            }
            this._delegate.serializeAsPlaceholder(bean, gen, prov);
        }
        
        @Override
        public void depositSchemaProperty(final JsonObjectFormatVisitor v, final SerializerProvider provider) throws JsonMappingException {
            if (this._inView(provider.getActiveView())) {
                super.depositSchemaProperty(v, provider);
            }
        }
        
        private final boolean _inView(final Class<?> activeView) {
            if (activeView == null) {
                return true;
            }
            for (int len = this._views.length, i = 0; i < len; ++i) {
                if (this._views[i].isAssignableFrom(activeView)) {
                    return true;
                }
            }
            return false;
        }
    }
}
