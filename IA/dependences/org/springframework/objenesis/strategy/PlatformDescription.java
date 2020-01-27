// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.objenesis.strategy;

import java.lang.reflect.Field;
import org.springframework.objenesis.ObjenesisException;

public final class PlatformDescription
{
    public static final String JROCKIT = "BEA";
    public static final String GNU = "GNU libgcj";
    public static final String SUN = "Java HotSpot";
    public static final String OPENJDK = "OpenJDK";
    public static final String PERC = "PERC";
    public static final String DALVIK = "Dalvik";
    public static final String SPECIFICATION_VERSION;
    public static final String VM_VERSION;
    public static final String VM_INFO;
    public static final String VENDOR_VERSION;
    public static final String VENDOR;
    public static final String JVM_NAME;
    public static final int ANDROID_VERSION;
    
    public static boolean isThisJVM(final String name) {
        return PlatformDescription.JVM_NAME.startsWith(name);
    }
    
    private static int getAndroidVersion() {
        if (!isThisJVM("Dalvik")) {
            return 0;
        }
        return getAndroidVersion0();
    }
    
    private static int getAndroidVersion0() {
        Class<?> clazz;
        try {
            clazz = Class.forName("android.os.Build$VERSION");
        }
        catch (ClassNotFoundException e) {
            throw new ObjenesisException(e);
        }
        Field field;
        try {
            field = clazz.getField("SDK_INT");
        }
        catch (NoSuchFieldException e3) {
            return getOldAndroidVersion(clazz);
        }
        int version;
        try {
            version = (int)field.get(null);
        }
        catch (IllegalAccessException e2) {
            throw new RuntimeException(e2);
        }
        return version;
    }
    
    private static int getOldAndroidVersion(final Class<?> versionClass) {
        Field field;
        try {
            field = versionClass.getField("SDK");
        }
        catch (NoSuchFieldException e) {
            throw new ObjenesisException(e);
        }
        String version;
        try {
            version = (String)field.get(null);
        }
        catch (IllegalAccessException e2) {
            throw new RuntimeException(e2);
        }
        return Integer.parseInt(version);
    }
    
    private PlatformDescription() {
    }
    
    static {
        SPECIFICATION_VERSION = System.getProperty("java.specification.version");
        VM_VERSION = System.getProperty("java.runtime.version");
        VM_INFO = System.getProperty("java.vm.info");
        VENDOR_VERSION = System.getProperty("java.vm.version");
        VENDOR = System.getProperty("java.vm.vendor");
        JVM_NAME = System.getProperty("java.vm.name");
        ANDROID_VERSION = getAndroidVersion();
    }
}
