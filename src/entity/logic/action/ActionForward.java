package entity.logic.action;

import entity.Ant;
import openGL.world.Chunk;
import openGL.world.World;
import org.lwjgl.util.vector.Vector3f;

public class ActionForward extends Action {

    @Override
    public void execute(Ant a, World world) {
        a.setTargetPosition(new Vector3f((float)(a.getPosition().x + Math.cos(Math.toRadians(a.getTargetRot()))), 0, (float)(a.getPosition().z - Math.sin(Math.toRadians(a.getTargetRot())))));
    }
}
