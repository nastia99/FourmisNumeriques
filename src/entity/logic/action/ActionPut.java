package entity.logic.action;

import entity.Ant;
import entity.Tile;
import openGL.world.World;

public class ActionPut extends Action {

    @Override
    public void execute(Ant a, World world) {
        if (a.isCarryingFood()) {
            ((Tile) world.getChunkInWorldCoords(a.getPosition().x, a.getPosition().z)).addEntity(a.getFood());
            a.setFood(null);
        }
    }
}
