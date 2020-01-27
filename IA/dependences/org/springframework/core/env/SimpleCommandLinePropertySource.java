// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.env;

import java.util.List;

public class SimpleCommandLinePropertySource extends CommandLinePropertySource<CommandLineArgs>
{
    public SimpleCommandLinePropertySource(final String... args) {
        super(new SimpleCommandLineArgsParser().parse(args));
    }
    
    public SimpleCommandLinePropertySource(final String name, final String[] args) {
        super(name, new SimpleCommandLineArgsParser().parse(args));
    }
    
    @Override
    public String[] getPropertyNames() {
        return ((CommandLineArgs)this.source).getOptionNames().toArray(new String[((CommandLineArgs)this.source).getOptionNames().size()]);
    }
    
    @Override
    protected boolean containsOption(final String name) {
        return ((CommandLineArgs)this.source).containsOption(name);
    }
    
    @Override
    protected List<String> getOptionValues(final String name) {
        return ((CommandLineArgs)this.source).getOptionValues(name);
    }
    
    @Override
    protected List<String> getNonOptionArgs() {
        return ((CommandLineArgs)this.source).getNonOptionArgs();
    }
}
