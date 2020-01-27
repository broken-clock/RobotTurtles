// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.type.classreading;

import java.util.Iterator;
import org.springframework.asm.Type;
import java.util.Map;
import org.springframework.core.annotation.AnnotationAttributes;

abstract class AnnotationReadingVisitorUtils
{
    public static AnnotationAttributes convertClassValues(final ClassLoader classLoader, final AnnotationAttributes original, final boolean classValuesAsString) {
        if (original == null) {
            return null;
        }
        final AnnotationAttributes result = new AnnotationAttributes(original.size());
        for (final Map.Entry<String, Object> entry : original.entrySet()) {
            try {
                Object value = entry.getValue();
                if (value instanceof AnnotationAttributes) {
                    value = convertClassValues(classLoader, (AnnotationAttributes)value, classValuesAsString);
                }
                else if (value instanceof AnnotationAttributes[]) {
                    final AnnotationAttributes[] values = (AnnotationAttributes[])value;
                    for (int i = 0; i < values.length; ++i) {
                        values[i] = convertClassValues(classLoader, values[i], classValuesAsString);
                    }
                }
                else if (value instanceof Type) {
                    value = (classValuesAsString ? ((Type)value).getClassName() : classLoader.loadClass(((Type)value).getClassName()));
                }
                else if (value instanceof Type[]) {
                    final Type[] array = (Type[])value;
                    final Object[] convArray = classValuesAsString ? new String[array.length] : new Class[array.length];
                    for (int j = 0; j < array.length; ++j) {
                        convArray[j] = (classValuesAsString ? array[j].getClassName() : classLoader.loadClass(array[j].getClassName()));
                    }
                    value = convArray;
                }
                else if (classValuesAsString) {
                    if (value instanceof Class) {
                        value = ((Class)value).getName();
                    }
                    else if (value instanceof Class[]) {
                        final Class<?>[] clazzArray = (Class<?>[])value;
                        final String[] newValue = new String[clazzArray.length];
                        for (int j = 0; j < clazzArray.length; ++j) {
                            newValue[j] = clazzArray[j].getName();
                        }
                        value = newValue;
                    }
                }
                result.put(entry.getKey(), value);
            }
            catch (Exception ex) {}
        }
        return result;
    }
}
