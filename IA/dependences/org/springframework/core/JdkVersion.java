// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

public abstract class JdkVersion
{
    public static final int JAVA_13 = 0;
    public static final int JAVA_14 = 1;
    public static final int JAVA_15 = 2;
    public static final int JAVA_16 = 3;
    public static final int JAVA_17 = 4;
    public static final int JAVA_18 = 5;
    public static final int JAVA_19 = 6;
    private static final String javaVersion;
    private static final int majorJavaVersion;
    
    public static String getJavaVersion() {
        return JdkVersion.javaVersion;
    }
    
    public static int getMajorJavaVersion() {
        return JdkVersion.majorJavaVersion;
    }
    
    static {
        javaVersion = System.getProperty("java.version");
        if (JdkVersion.javaVersion.contains("1.9.")) {
            majorJavaVersion = 6;
        }
        else if (JdkVersion.javaVersion.contains("1.8.")) {
            majorJavaVersion = 5;
        }
        else if (JdkVersion.javaVersion.contains("1.7.")) {
            majorJavaVersion = 4;
        }
        else {
            majorJavaVersion = 3;
        }
    }
}
