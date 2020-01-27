// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.objenesis.instantiator;

import java.io.Serializable;

public class SerializationInstantiatorHelper
{
    public static <T> Class<? super T> getNonSerializableSuperClass(final Class<T> type) {
        Class<? super T> result = type;
        while (Serializable.class.isAssignableFrom(result)) {
            result = result.getSuperclass();
            if (result == null) {
                throw new Error("Bad class hierarchy: No non-serializable parents");
            }
        }
        return result;
    }
}
