// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.objenesis.strategy;

import org.springframework.objenesis.instantiator.perc.PercSerializationInstantiator;
import org.springframework.objenesis.instantiator.gcj.GCJSerializationInstantiator;
import org.springframework.objenesis.instantiator.android.AndroidSerializationInstantiator;
import org.springframework.objenesis.instantiator.basic.ObjectStreamClassInstantiator;
import org.springframework.objenesis.ObjenesisException;
import java.io.NotSerializableException;
import java.io.Serializable;
import org.springframework.objenesis.instantiator.ObjectInstantiator;

public class SerializingInstantiatorStrategy extends BaseInstantiatorStrategy
{
    public <T> ObjectInstantiator<T> newInstantiatorOf(final Class<T> type) {
        if (!Serializable.class.isAssignableFrom(type)) {
            throw new ObjenesisException(new NotSerializableException(type + " not serializable"));
        }
        if (PlatformDescription.JVM_NAME.startsWith("Java HotSpot") || PlatformDescription.isThisJVM("OpenJDK")) {
            return new ObjectStreamClassInstantiator<T>(type);
        }
        if (PlatformDescription.JVM_NAME.startsWith("Dalvik")) {
            return new AndroidSerializationInstantiator<T>(type);
        }
        if (PlatformDescription.JVM_NAME.startsWith("GNU libgcj")) {
            return new GCJSerializationInstantiator<T>(type);
        }
        if (PlatformDescription.JVM_NAME.startsWith("PERC")) {
            return new PercSerializationInstantiator<T>(type);
        }
        return new ObjectStreamClassInstantiator<T>(type);
    }
}
