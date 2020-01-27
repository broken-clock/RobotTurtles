// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.objenesis.strategy;

import org.springframework.objenesis.instantiator.sun.UnsafeFactoryInstantiator;
import org.springframework.objenesis.instantiator.perc.PercInstantiator;
import org.springframework.objenesis.instantiator.gcj.GCJInstantiator;
import org.springframework.objenesis.instantiator.android.Android18Instantiator;
import org.springframework.objenesis.instantiator.android.Android17Instantiator;
import org.springframework.objenesis.instantiator.android.Android10Instantiator;
import org.springframework.objenesis.instantiator.jrockit.JRockitLegacyInstantiator;
import org.springframework.objenesis.instantiator.sun.SunReflectionFactoryInstantiator;
import org.springframework.objenesis.instantiator.ObjectInstantiator;

public class StdInstantiatorStrategy extends BaseInstantiatorStrategy
{
    public <T> ObjectInstantiator<T> newInstantiatorOf(final Class<T> type) {
        if (PlatformDescription.isThisJVM("Java HotSpot") || PlatformDescription.isThisJVM("OpenJDK")) {
            return new SunReflectionFactoryInstantiator<T>(type);
        }
        if (PlatformDescription.isThisJVM("BEA")) {
            if (PlatformDescription.VM_VERSION.startsWith("1.4") && !PlatformDescription.VENDOR_VERSION.startsWith("R") && (PlatformDescription.VM_INFO == null || !PlatformDescription.VM_INFO.startsWith("R25.1") || !PlatformDescription.VM_INFO.startsWith("R25.2"))) {
                return new JRockitLegacyInstantiator<T>(type);
            }
            return new SunReflectionFactoryInstantiator<T>(type);
        }
        else if (PlatformDescription.isThisJVM("Dalvik")) {
            if (PlatformDescription.ANDROID_VERSION <= 10) {
                return new Android10Instantiator<T>(type);
            }
            if (PlatformDescription.ANDROID_VERSION <= 17) {
                return new Android17Instantiator<T>(type);
            }
            return new Android18Instantiator<T>(type);
        }
        else {
            if (PlatformDescription.isThisJVM("GNU libgcj")) {
                return new GCJInstantiator<T>(type);
            }
            if (PlatformDescription.isThisJVM("PERC")) {
                return new PercInstantiator<T>(type);
            }
            return new UnsafeFactoryInstantiator<T>(type);
        }
    }
}
