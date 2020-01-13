package entity.logic.action;

import entity.Ant;
import entity.EntityTypes;
import entity.Tile;
import openGL.world.World;

public class ActionCondAntHil extends Action {

    public ActionCondAntHil() {
        isConditional = true;
    }

    @Override
    public void execute(Ant a, World world) {
        return;
    }

    @Override
    public boolean isConditionSatisfied(Ant a, World world) {
        if (world == null)
            System.out.println(world);
        Tile tile = (Tile) world.getChunkInWorldCoords(a.getPosition().x, a.getPosition().z);
        if (tile == null)
            return false;
        else
            return tile.contains(EntityTypes.ANTHIL);
    }

    @Override
    public String toString() {
        return "anthil";
    }
}
