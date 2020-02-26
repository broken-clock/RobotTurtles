package srcIA.dependences.com.grooptown.snorkunking.service.engine.tile;

import srcIA.dependences.com.grooptown.snorkunking.service.engine.grid.Panel;
import srcIA.dependences.com.grooptown.snorkunking.service.engine.grid.PanelEnum;

/**
 * Created by thibautdebroca on 02/11/2019.
 */
public class WallTile extends Tile implements Panel {
    @Override
    public String toAscii() {
        return "Wall";
    }
    @Override
    public PanelEnum getPanelName() {
        return PanelEnum.WALL;
    }
}
