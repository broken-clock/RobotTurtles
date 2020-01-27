// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import org.springframework.core.annotation.AnnotationUtils;
import java.lang.reflect.Method;

class BeanAnnotationHelper
{
    public static boolean isBeanAnnotated(final Method method) {
        return AnnotationUtils.findAnnotation(method, Bean.class) != null;
    }
    
    public static String determineBeanNameFor(final Method beanMethod) {
        String beanName = beanMethod.getName();
        final Bean bean = AnnotationUtils.findAnnotation(beanMethod, Bean.class);
        if (bean != null && bean.name().length > 0) {
            beanName = bean.name()[0];
        }
        return beanName;
    }
}
