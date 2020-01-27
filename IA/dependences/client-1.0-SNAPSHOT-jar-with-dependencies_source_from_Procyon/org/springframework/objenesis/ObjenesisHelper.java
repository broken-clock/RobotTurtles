// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.objenesis;

import org.springframework.objenesis.instantiator.ObjectInstantiator;
import java.io.Serializable;

public final class ObjenesisHelper
{
    private static final Objenesis OBJENESIS_STD;
    private static final Objenesis OBJENESIS_SERIALIZER;
    
    private ObjenesisHelper() {
    }
    
    public static <T> T newInstance(final Class<T> clazz) {
        return ObjenesisHelper.OBJENESIS_STD.newInstance(clazz);
    }
    
    public static <T extends Serializable> T newSerializableInstance(final Class<T> clazz) {
        return ObjenesisHelper.OBJENESIS_SERIALIZER.newInstance(clazz);
    }
    
    public static <T> ObjectInstantiator<T> getInstantiatorOf(final Class<T> clazz) {
        return ObjenesisHelper.OBJENESIS_STD.getInstantiatorOf(clazz);
    }
    
    public static <T extends Serializable> ObjectInstantiator<T> getSerializableObjectInstantiatorOf(final Class<T> clazz) {
        return ObjenesisHelper.OBJENESIS_SERIALIZER.getInstantiatorOf(clazz);
    }
    
    static {
        OBJENESIS_STD = new ObjenesisStd();
        OBJENESIS_SERIALIZER = new ObjenesisSerializer();
    }
}
