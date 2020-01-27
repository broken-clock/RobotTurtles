// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.env;

class SimpleCommandLineArgsParser
{
    public CommandLineArgs parse(final String... args) {
        final CommandLineArgs commandLineArgs = new CommandLineArgs();
        for (final String arg : args) {
            if (arg.startsWith("--")) {
                final String optionText = arg.substring(2, arg.length());
                String optionValue = null;
                String optionName;
                if (optionText.contains("=")) {
                    optionName = optionText.substring(0, optionText.indexOf("="));
                    optionValue = optionText.substring(optionText.indexOf("=") + 1, optionText.length());
                }
                else {
                    optionName = optionText;
                }
                if (optionName.isEmpty() || (optionValue != null && optionValue.isEmpty())) {
                    throw new IllegalArgumentException("Invalid argument syntax: " + arg);
                }
                commandLineArgs.addOptionArg(optionName, optionValue);
            }
            else {
                commandLineArgs.addNonOptionArg(arg);
            }
        }
        return commandLineArgs;
    }
}
