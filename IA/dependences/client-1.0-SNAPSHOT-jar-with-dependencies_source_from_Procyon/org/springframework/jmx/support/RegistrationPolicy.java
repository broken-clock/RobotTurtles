// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jmx.support;

public enum RegistrationPolicy
{
    FAIL_ON_EXISTING, 
    IGNORE_EXISTING, 
    REPLACE_EXISTING;
    
    static RegistrationPolicy valueOf(final int registrationBehavior) {
        switch (registrationBehavior) {
            case 1: {
                return RegistrationPolicy.IGNORE_EXISTING;
            }
            case 2: {
                return RegistrationPolicy.REPLACE_EXISTING;
            }
            case 0: {
                return RegistrationPolicy.FAIL_ON_EXISTING;
            }
            default: {
                throw new IllegalArgumentException("Unknown MBean registration behavior: " + registrationBehavior);
            }
        }
    }
}
