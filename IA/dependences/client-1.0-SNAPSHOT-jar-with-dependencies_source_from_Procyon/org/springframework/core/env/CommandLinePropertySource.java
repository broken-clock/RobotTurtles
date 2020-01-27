// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.env;

import java.util.List;
import java.util.Collection;
import org.springframework.util.StringUtils;

public abstract class CommandLinePropertySource<T> extends EnumerablePropertySource<T>
{
    public static final String COMMAND_LINE_PROPERTY_SOURCE_NAME = "commandLineArgs";
    public static final String DEFAULT_NON_OPTION_ARGS_PROPERTY_NAME = "nonOptionArgs";
    private String nonOptionArgsPropertyName;
    
    public CommandLinePropertySource(final T source) {
        super("commandLineArgs", source);
        this.nonOptionArgsPropertyName = "nonOptionArgs";
    }
    
    public CommandLinePropertySource(final String name, final T source) {
        super(name, source);
        this.nonOptionArgsPropertyName = "nonOptionArgs";
    }
    
    public void setNonOptionArgsPropertyName(final String nonOptionArgsPropertyName) {
        this.nonOptionArgsPropertyName = nonOptionArgsPropertyName;
    }
    
    @Override
    public final boolean containsProperty(final String name) {
        if (this.nonOptionArgsPropertyName.equals(name)) {
            return !this.getNonOptionArgs().isEmpty();
        }
        return this.containsOption(name);
    }
    
    @Override
    public final String getProperty(final String name) {
        if (this.nonOptionArgsPropertyName.equals(name)) {
            final Collection<String> nonOptionArguments = this.getNonOptionArgs();
            if (nonOptionArguments.isEmpty()) {
                return null;
            }
            return StringUtils.collectionToCommaDelimitedString(nonOptionArguments);
        }
        else {
            final Collection<String> optionValues = this.getOptionValues(name);
            if (optionValues == null) {
                return null;
            }
            return StringUtils.collectionToCommaDelimitedString(optionValues);
        }
    }
    
    protected abstract boolean containsOption(final String p0);
    
    protected abstract List<String> getOptionValues(final String p0);
    
    protected abstract List<String> getNonOptionArgs();
}
