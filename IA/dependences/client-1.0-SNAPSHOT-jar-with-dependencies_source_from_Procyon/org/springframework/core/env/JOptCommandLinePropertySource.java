// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.env;

import java.util.Collections;
import org.springframework.util.Assert;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import joptsimple.OptionSpec;
import java.util.ArrayList;
import joptsimple.OptionSet;

public class JOptCommandLinePropertySource extends CommandLinePropertySource<OptionSet>
{
    public JOptCommandLinePropertySource(final OptionSet options) {
        super(options);
    }
    
    public JOptCommandLinePropertySource(final String name, final OptionSet options) {
        super(name, options);
    }
    
    @Override
    protected boolean containsOption(final String name) {
        return ((OptionSet)this.source).has(name);
    }
    
    @Override
    public String[] getPropertyNames() {
        final List<String> names = new ArrayList<String>();
        for (final OptionSpec<?> spec : ((OptionSet)this.source).specs()) {
            final List<String> aliases = new ArrayList<String>(spec.options());
            if (!aliases.isEmpty()) {
                names.add(aliases.get(aliases.size() - 1));
            }
        }
        return names.toArray(new String[names.size()]);
    }
    
    public List<String> getOptionValues(final String name) {
        final List<?> argValues = (List<?>)((OptionSet)this.source).valuesOf(name);
        final List<String> stringArgValues = new ArrayList<String>();
        for (final Object argValue : argValues) {
            Assert.isInstanceOf(String.class, argValue, "Argument values must be of type String");
            stringArgValues.add((String)argValue);
        }
        if (stringArgValues.isEmpty()) {
            return ((OptionSet)this.source).has(name) ? Collections.emptyList() : null;
        }
        return Collections.unmodifiableList((List<? extends String>)stringArgValues);
    }
    
    @Override
    protected List<String> getNonOptionArgs() {
        final List<?> argValues = (List<?>)((OptionSet)this.source).nonOptionArguments();
        final List<String> stringArgValues = new ArrayList<String>();
        for (final Object argValue : argValues) {
            Assert.isInstanceOf(String.class, argValue, "Argument values must be of type String");
            stringArgValues.add((String)argValue);
        }
        return stringArgValues.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList((List<? extends String>)stringArgValues);
    }
}
