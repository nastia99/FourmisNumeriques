package entity.logic.action;

import entity.Ant;
import entity.EntityTypes;
import entity.Tile;
import openGL.world.World;

public class ActionCondFood extends Action {

    public ActionCondFood() {
        isConditional = true;
    }

    @Override
    public void execute(Ant a, World world) {
        //Do nothing
        return;
    }

    @Override
    public boolean isConditionSatisfied(Ant a, World world) {
        Tile tile = (Tile) world.getChunkInWorldCoords(a.getPosition().x, a.getPosition().z);
        if (tile == null)
            return false;
        else
            return tile.contains(EntityTypes.FOOD);
    }
}
