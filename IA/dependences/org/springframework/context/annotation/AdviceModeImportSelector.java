// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.Assert;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.type.AnnotationMetadata;
import java.lang.annotation.Annotation;

public abstract class AdviceModeImportSelector<A extends Annotation> implements ImportSelector
{
    public static final String DEFAULT_ADVICE_MODE_ATTRIBUTE_NAME = "mode";
    
    protected String getAdviceModeAttributeName() {
        return "mode";
    }
    
    @Override
    public final String[] selectImports(final AnnotationMetadata importingClassMetadata) {
        final Class<?> annoType = GenericTypeResolver.resolveTypeArgument(this.getClass(), AdviceModeImportSelector.class);
        final AnnotationAttributes attributes = AnnotationConfigUtils.attributesFor(importingClassMetadata, annoType);
        Assert.notNull(attributes, String.format("@%s is not present on importing class '%s' as expected", annoType.getSimpleName(), importingClassMetadata.getClassName()));
        final AdviceMode adviceMode = attributes.getEnum(this.getAdviceModeAttributeName());
        final String[] imports = this.selectImports(adviceMode);
        Assert.notNull(imports, String.format("Unknown AdviceMode: '%s'", adviceMode));
        return imports;
    }
    
    protected abstract String[] selectImports(final AdviceMode p0);
}
