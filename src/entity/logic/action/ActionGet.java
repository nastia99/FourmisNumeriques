package entity.logic.action;

import entity.Ant;
import entity.EntityTypes;
import entity.Food;
import entity.Tile;
import openGL.world.World;

public class ActionGet extends Action {

    @Override
    public void execute(Ant a, World world) {
        if (a.isCarryingFood())
            return;
        Tile tile = (Tile) world.getChunkInWorldCoords(a.getPosition().x, a.getPosition().z);
        if (tile != null && tile.contains(EntityTypes.FOOD)) {
            a.setFood((Food) tile.getEntity(EntityTypes.FOOD));
        }
    }

    @Override
    public String toString() {
        return "get";
    }
}
