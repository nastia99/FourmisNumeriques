package entity.logic.action;

import entity.Ant;
import openGL.world.Chunk;
import openGL.world.World;
import org.lwjgl.util.vector.Vector3f;

import java.util.Random;

public class ActionRandom extends Action {

    private Random random;

    public ActionRandom() {
        random = new Random();
    }

    @Override
    public void execute(Ant a, World world) {
        switch(random.nextInt(3)) {
            case 1: //Left
                a.setTargetRot(a.getTargetRot() + 90);
                break;
            case 2: //Right
                a.setTargetRot(a.getTargetRot() - 90);
                break;
        }

        a.setTargetPosition(new Vector3f((float)(a.getPosition().x + Chunk.SIZE * Math.cos(Math.toRadians(a.getTargetRot()))), 0, (float)(a.getPosition().z - Chunk.SIZE * Math.sin(Math.toRadians(a.getTargetRot())))));
    }
}
