// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.validation;

import org.springframework.util.Assert;
import java.util.Map;

public abstract class BindingResultUtils
{
    public static BindingResult getBindingResult(final Map<?, ?> model, final String name) {
        Assert.notNull(model, "Model map must not be null");
        Assert.notNull(name, "Name must not be null");
        final Object attr = model.get(BindingResult.MODEL_KEY_PREFIX + name);
        if (attr != null && !(attr instanceof BindingResult)) {
            throw new IllegalStateException("BindingResult attribute is not of type BindingResult: " + attr);
        }
        return (BindingResult)attr;
    }
    
    public static BindingResult getRequiredBindingResult(final Map<?, ?> model, final String name) {
        final BindingResult bindingResult = getBindingResult(model, name);
        if (bindingResult == null) {
            throw new IllegalStateException("No BindingResult attribute found for name '" + name + "'- have you exposed the correct model?");
        }
        return bindingResult;
    }
}
