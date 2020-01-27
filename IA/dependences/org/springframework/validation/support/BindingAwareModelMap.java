// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.validation.support;

import java.util.LinkedHashMap;
import org.springframework.validation.BindingResult;
import java.util.Iterator;
import java.util.Map;
import org.springframework.ui.ExtendedModelMap;

public class BindingAwareModelMap extends ExtendedModelMap
{
    @Override
    public Object put(final String key, final Object value) {
        this.removeBindingResultIfNecessary(key, value);
        return super.put(key, value);
    }
    
    @Override
    public void putAll(final Map<? extends String, ?> map) {
        for (final Map.Entry<? extends String, ?> entry : map.entrySet()) {
            this.removeBindingResultIfNecessary(entry.getKey(), entry.getValue());
        }
        super.putAll(map);
    }
    
    private void removeBindingResultIfNecessary(final Object key, final Object value) {
        if (key instanceof String) {
            final String attributeName = (String)key;
            if (!attributeName.startsWith(BindingResult.MODEL_KEY_PREFIX)) {
                final String bindingResultKey = BindingResult.MODEL_KEY_PREFIX + attributeName;
                final BindingResult bindingResult = ((LinkedHashMap<K, BindingResult>)this).get(bindingResultKey);
                if (bindingResult != null && bindingResult.getTarget() != value) {
                    this.remove(bindingResultKey);
                }
            }
        }
    }
}
