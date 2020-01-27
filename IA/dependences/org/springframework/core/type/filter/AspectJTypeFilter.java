// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.type.filter;

import java.io.IOException;
import org.aspectj.weaver.ResolvedType;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.SimpleScope;
import org.aspectj.weaver.patterns.FormalBinding;
import org.aspectj.weaver.patterns.PatternParser;
import org.aspectj.weaver.ICrossReferenceHandler;
import org.aspectj.weaver.bcel.BcelWorld;
import org.aspectj.bridge.IMessageHandler;
import org.aspectj.weaver.patterns.TypePattern;
import org.aspectj.weaver.World;

public class AspectJTypeFilter implements TypeFilter
{
    private final World world;
    private final TypePattern typePattern;
    
    public AspectJTypeFilter(final String typePatternExpression, final ClassLoader classLoader) {
        (this.world = (World)new BcelWorld(classLoader, IMessageHandler.THROW, (ICrossReferenceHandler)null)).setBehaveInJava5Way(true);
        final PatternParser patternParser = new PatternParser(typePatternExpression);
        final TypePattern typePattern = patternParser.parseTypePattern();
        typePattern.resolve(this.world);
        final IScope scope = (IScope)new SimpleScope(this.world, new FormalBinding[0]);
        this.typePattern = typePattern.resolveBindings(scope, Bindings.NONE, false, false);
    }
    
    @Override
    public boolean match(final MetadataReader metadataReader, final MetadataReaderFactory metadataReaderFactory) throws IOException {
        final String className = metadataReader.getClassMetadata().getClassName();
        final ResolvedType resolvedType = this.world.resolve(className);
        return this.typePattern.matchesStatically(resolvedType);
    }
}
