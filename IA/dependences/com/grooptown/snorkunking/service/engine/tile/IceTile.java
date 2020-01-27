// 
// Decompiled by Procyon v0.5.36
// 

package com.grooptown.snorkunking.service.engine.tile;

import com.grooptown.snorkunking.service.engine.grid.PanelEnum;

public class IceTile extends Tile
{
    @Override
    public String toAscii() {
        return "Ice ";
    }
    
    @Override
    public PanelEnum getPanelName() {
        return PanelEnum.ICE;
    }
}
