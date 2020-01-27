// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.annotation;

import java.lang.annotation.Documented;
import org.springframework.context.annotation.Import;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Import({ SchedulingConfiguration.class })
@Documented
public @interface EnableScheduling {
}
