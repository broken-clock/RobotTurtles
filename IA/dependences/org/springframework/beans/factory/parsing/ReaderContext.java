// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.parsing;

import org.springframework.core.io.Resource;

public class ReaderContext
{
    private final Resource resource;
    private final ProblemReporter problemReporter;
    private final ReaderEventListener eventListener;
    private final SourceExtractor sourceExtractor;
    
    public ReaderContext(final Resource resource, final ProblemReporter problemReporter, final ReaderEventListener eventListener, final SourceExtractor sourceExtractor) {
        this.resource = resource;
        this.problemReporter = problemReporter;
        this.eventListener = eventListener;
        this.sourceExtractor = sourceExtractor;
    }
    
    public final Resource getResource() {
        return this.resource;
    }
    
    public void fatal(final String message, final Object source) {
        this.fatal(message, source, null, null);
    }
    
    public void fatal(final String message, final Object source, final Throwable ex) {
        this.fatal(message, source, null, ex);
    }
    
    public void fatal(final String message, final Object source, final ParseState parseState) {
        this.fatal(message, source, parseState, null);
    }
    
    public void fatal(final String message, final Object source, final ParseState parseState, final Throwable cause) {
        final Location location = new Location(this.getResource(), source);
        this.problemReporter.fatal(new Problem(message, location, parseState, cause));
    }
    
    public void error(final String message, final Object source) {
        this.error(message, source, null, null);
    }
    
    public void error(final String message, final Object source, final Throwable ex) {
        this.error(message, source, null, ex);
    }
    
    public void error(final String message, final Object source, final ParseState parseState) {
        this.error(message, source, parseState, null);
    }
    
    public void error(final String message, final Object source, final ParseState parseState, final Throwable cause) {
        final Location location = new Location(this.getResource(), source);
        this.problemReporter.error(new Problem(message, location, parseState, cause));
    }
    
    public void warning(final String message, final Object source) {
        this.warning(message, source, null, null);
    }
    
    public void warning(final String message, final Object source, final Throwable ex) {
        this.warning(message, source, null, ex);
    }
    
    public void warning(final String message, final Object source, final ParseState parseState) {
        this.warning(message, source, parseState, null);
    }
    
    public void warning(final String message, final Object source, final ParseState parseState, final Throwable cause) {
        final Location location = new Location(this.getResource(), source);
        this.problemReporter.warning(new Problem(message, location, parseState, cause));
    }
    
    public void fireDefaultsRegistered(final DefaultsDefinition defaultsDefinition) {
        this.eventListener.defaultsRegistered(defaultsDefinition);
    }
    
    public void fireComponentRegistered(final ComponentDefinition componentDefinition) {
        this.eventListener.componentRegistered(componentDefinition);
    }
    
    public void fireAliasRegistered(final String beanName, final String alias, final Object source) {
        this.eventListener.aliasRegistered(new AliasDefinition(beanName, alias, source));
    }
    
    public void fireImportProcessed(final String importedResource, final Object source) {
        this.eventListener.importProcessed(new ImportDefinition(importedResource, source));
    }
    
    public void fireImportProcessed(final String importedResource, final Resource[] actualResources, final Object source) {
        this.eventListener.importProcessed(new ImportDefinition(importedResource, actualResources, source));
    }
    
    public SourceExtractor getSourceExtractor() {
        return this.sourceExtractor;
    }
    
    public Object extractSource(final Object sourceCandidate) {
        return this.sourceExtractor.extractSource(sourceCandidate, this.resource);
    }
}
