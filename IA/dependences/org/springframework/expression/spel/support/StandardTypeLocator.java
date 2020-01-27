// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.support;

import org.springframework.expression.EvaluationException;
import java.util.Iterator;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import java.util.Collections;
import java.util.LinkedList;
import org.springframework.util.ClassUtils;
import java.util.List;
import org.springframework.expression.TypeLocator;

public class StandardTypeLocator implements TypeLocator
{
    private final ClassLoader classLoader;
    private final List<String> knownPackagePrefixes;
    
    public StandardTypeLocator() {
        this(ClassUtils.getDefaultClassLoader());
    }
    
    public StandardTypeLocator(final ClassLoader classLoader) {
        this.knownPackagePrefixes = new LinkedList<String>();
        this.classLoader = classLoader;
        this.registerImport("java.lang");
    }
    
    public void registerImport(final String prefix) {
        this.knownPackagePrefixes.add(prefix);
    }
    
    public void removeImport(final String prefix) {
        this.knownPackagePrefixes.remove(prefix);
    }
    
    public List<String> getImportPrefixes() {
        return Collections.unmodifiableList((List<? extends String>)this.knownPackagePrefixes);
    }
    
    @Override
    public Class<?> findType(final String typeName) throws EvaluationException {
        String nameToLookup = typeName;
        try {
            return this.classLoader.loadClass(nameToLookup);
        }
        catch (ClassNotFoundException ey) {
            for (final String prefix : this.knownPackagePrefixes) {
                try {
                    nameToLookup = prefix + "." + typeName;
                    return this.classLoader.loadClass(nameToLookup);
                }
                catch (ClassNotFoundException ex) {
                    continue;
                }
                break;
            }
            throw new SpelEvaluationException(SpelMessage.TYPE_NOT_FOUND, new Object[] { typeName });
        }
    }
}
