// 
// Decompiled by Procyon v0.5.36
// 

package com.grooptown.snorkunking.service.engine.tile;

import com.grooptown.snorkunking.service.engine.grid.PanelEnum;
import com.grooptown.snorkunking.service.engine.grid.Panel;

public class WoodBoxTile extends Tile implements Panel
{
    @Override
    public String toAscii() {
        return "WBox";
    }
    
    @Override
    public PanelEnum getPanelName() {
        return PanelEnum.WOODBOX;
    }
}
