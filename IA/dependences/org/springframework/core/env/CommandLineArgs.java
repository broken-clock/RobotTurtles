// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.env;

import java.util.Collections;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CommandLineArgs
{
    private final Map<String, List<String>> optionArgs;
    private final List<String> nonOptionArgs;
    
    CommandLineArgs() {
        this.optionArgs = new HashMap<String, List<String>>();
        this.nonOptionArgs = new ArrayList<String>();
    }
    
    public void addOptionArg(final String optionName, final String optionValue) {
        if (!this.optionArgs.containsKey(optionName)) {
            this.optionArgs.put(optionName, new ArrayList<String>());
        }
        if (optionValue != null) {
            this.optionArgs.get(optionName).add(optionValue);
        }
    }
    
    public Set<String> getOptionNames() {
        return Collections.unmodifiableSet((Set<? extends String>)this.optionArgs.keySet());
    }
    
    public boolean containsOption(final String optionName) {
        return this.optionArgs.containsKey(optionName);
    }
    
    public List<String> getOptionValues(final String optionName) {
        return this.optionArgs.get(optionName);
    }
    
    public void addNonOptionArg(final String value) {
        this.nonOptionArgs.add(value);
    }
    
    public List<String> getNonOptionArgs() {
        return Collections.unmodifiableList((List<? extends String>)this.nonOptionArgs);
    }
}
