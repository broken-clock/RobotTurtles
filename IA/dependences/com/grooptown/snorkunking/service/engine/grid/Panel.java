// 
// Decompiled by Procyon v0.5.36
// 

package com.grooptown.snorkunking.service.engine.grid;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface Panel
{
    String toAscii();
    
    PanelEnum getPanelName();
}
