// 
// Decompiled by Procyon v0.5.36
// 

package com.grooptown.snorkunking.service.engine.grid;

public class EmptyPanel implements Panel
{
    @Override
    public String toAscii() {
        return "    ";
    }
    
    @Override
    public PanelEnum getPanelName() {
        return PanelEnum.EMPTY;
    }
}
