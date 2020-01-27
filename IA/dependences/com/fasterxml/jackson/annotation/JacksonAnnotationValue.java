// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.annotation;

import java.lang.annotation.Annotation;

public interface JacksonAnnotationValue<A extends Annotation>
{
    Class<A> valueFor();
}
