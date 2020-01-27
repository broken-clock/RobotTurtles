// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.std;

import java.io.IOException;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;

public class ThrowableDeserializer extends BeanDeserializer
{
    private static final long serialVersionUID = 1L;
    protected static final String PROP_NAME_MESSAGE = "message";
    
    public ThrowableDeserializer(final BeanDeserializer baseDeserializer) {
        super(baseDeserializer);
        this._vanillaProcessing = false;
    }
    
    protected ThrowableDeserializer(final BeanDeserializer src, final NameTransformer unwrapper) {
        super(src, unwrapper);
    }
    
    @Override
    public JsonDeserializer<Object> unwrappingDeserializer(final NameTransformer unwrapper) {
        if (this.getClass() != ThrowableDeserializer.class) {
            return this;
        }
        return new ThrowableDeserializer(this, unwrapper);
    }
    
    @Override
    public Object deserializeFromObject(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (this._propertyBasedCreator != null) {
            return this._deserializeUsingPropertyBased(p, ctxt);
        }
        if (this._delegateDeserializer != null) {
            return this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
        }
        if (this._beanType.isAbstract()) {
            return ctxt.handleMissingInstantiator(this.handledType(), this.getValueInstantiator(), p, "abstract type (need to add/enable type information?)", new Object[0]);
        }
        final boolean hasStringCreator = this._valueInstantiator.canCreateFromString();
        final boolean hasDefaultCtor = this._valueInstantiator.canCreateUsingDefault();
        if (!hasStringCreator && !hasDefaultCtor) {
            return ctxt.handleMissingInstantiator(this.handledType(), this.getValueInstantiator(), p, "Throwable needs a default contructor, a single-String-arg constructor; or explicit @JsonCreator", new Object[0]);
        }
        Object throwable = null;
        Object[] pending = null;
        int pendingIx = 0;
        while (p.getCurrentToken() != JsonToken.END_OBJECT) {
            final String propName = p.getCurrentName();
            SettableBeanProperty prop = this._beanProperties.find(propName);
            p.nextToken();
            if (prop != null) {
                if (throwable != null) {
                    prop.deserializeAndSet(p, ctxt, throwable);
                }
                else {
                    if (pending == null) {
                        final int len = this._beanProperties.size();
                        pending = new Object[len + len];
                    }
                    pending[pendingIx++] = prop;
                    pending[pendingIx++] = prop.deserialize(p, ctxt);
                }
            }
            else {
                final boolean isMessage = "message".equals(propName);
                if (isMessage && hasStringCreator) {
                    throwable = this._valueInstantiator.createFromString(ctxt, p.getValueAsString());
                    if (pending != null) {
                        for (int i = 0, len2 = pendingIx; i < len2; i += 2) {
                            prop = (SettableBeanProperty)pending[i];
                            prop.set(throwable, pending[i + 1]);
                        }
                        pending = null;
                    }
                }
                else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
                    p.skipChildren();
                }
                else if (this._anySetter != null) {
                    this._anySetter.deserializeAndSet(p, ctxt, throwable, propName);
                }
                else {
                    this.handleUnknownProperty(p, ctxt, throwable, propName);
                }
            }
            p.nextToken();
        }
        if (throwable == null) {
            if (hasStringCreator) {
                throwable = this._valueInstantiator.createFromString(ctxt, null);
            }
            else {
                throwable = this._valueInstantiator.createUsingDefault(ctxt);
            }
            if (pending != null) {
                for (int j = 0, len3 = pendingIx; j < len3; j += 2) {
                    final SettableBeanProperty prop2 = (SettableBeanProperty)pending[j];
                    prop2.set(throwable, pending[j + 1]);
                }
            }
        }
        return throwable;
    }
}
