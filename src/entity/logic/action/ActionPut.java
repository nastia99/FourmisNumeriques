package entity.logic.action;

import entity.Ant;
import entity.Tile;
import openGL.utils.Maths;
import openGL.world.World;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.util.Random;

public class ActionPut extends Action {

    public static final Random random = new Random();
    @Override
    public void execute(Ant a, World world) {
        if (a.isCarryingFood()) {

            Vector3f normal = world.getNormal(a.getPosition().x, a.getPosition().z);
            float rotX = 1.618f * (float) Math.toDegrees(Maths.signedAngle(new Vector2f(normal.z, normal.y), new Vector2f(0, 1)));
            float rotZ = 1.618f * (float) Math.toDegrees(Maths.signedAngle(new Vector2f(0, 1), new Vector2f(normal.x, normal.y)));

            a.getFood().setPosition(new Vector3f(a.getPosition().x, world.getHeight(a.getPosition().x, a.getPosition().z), a.getPosition().z));
            a.setRotX(rotX);
            a.setRotZ(rotZ);
            a.setRotY(random.nextInt(360));

            ((Tile) world.getChunkInWorldCoords(a.getPosition().x, a.getPosition().z)).addEntity(a.getFood());
            a.setFood(null);
        }
    }
}