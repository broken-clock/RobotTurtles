// 
// Decompiled by Procyon v0.5.36
// 

package com.grooptown.snorkunking.service.engine.tile;

import java.util.HashMap;
import java.util.Map;

public class TileService
{
    private static final Map<String, Tile> nameToTiles;
    
    public static Tile getTile(final String name) {
        return TileService.nameToTiles.get(name);
    }
    
    static {
        (nameToTiles = new HashMap<String, Tile>()).put("Ice", new IceTile());
        TileService.nameToTiles.put("Wall", new WallTile());
        TileService.nameToTiles.put("Wll", new WallTile());
        TileService.nameToTiles.put("Box", new WoodBoxTile());
    }
}
