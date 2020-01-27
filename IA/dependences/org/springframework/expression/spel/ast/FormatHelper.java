// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.core.convert.TypeDescriptor;
import java.util.List;

public class FormatHelper
{
    public static String formatMethodForMessage(final String name, final List<TypeDescriptor> argumentTypes) {
        final StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append("(");
        for (int i = 0; i < argumentTypes.size(); ++i) {
            if (i > 0) {
                sb.append(",");
            }
            final TypeDescriptor typeDescriptor = argumentTypes.get(i);
            if (typeDescriptor != null) {
                sb.append(formatClassNameForMessage(typeDescriptor.getType()));
            }
            else {
                sb.append(formatClassNameForMessage(null));
            }
        }
        sb.append(")");
        return sb.toString();
    }
    
    public static String formatClassNameForMessage(final Class<?> clazz) {
        if (clazz == null) {
            return "null";
        }
        final StringBuilder fmtd = new StringBuilder();
        if (clazz.isArray()) {
            int dims;
            Class<?> baseClass;
            for (dims = 1, baseClass = clazz.getComponentType(); baseClass.isArray(); baseClass = baseClass.getComponentType(), ++dims) {}
            fmtd.append(baseClass.getName());
            for (int i = 0; i < dims; ++i) {
                fmtd.append("[]");
            }
        }
        else {
            fmtd.append(clazz.getName());
        }
        return fmtd.toString();
    }
}
