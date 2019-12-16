package entity;

import configs.Configs;
import engineTester.MainGameLoop;
import openGL.entities.RenderableObject;
import openGL.models.TexturedModel;
import openGL.renderEngine.OBJLoader;
import openGL.textures.ModelTexture;
import openGL.utils.Maths;
import openGL.world.Chunk;
import openGL.world.World;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;
import openGL.renderEngine.DisplayManager;

import java.util.Random;

public class Ant extends RenderableObject {

    private Vector3f targetPosition;
    private float targetRot;

    private Vector3f lastPosition;
    private float lastRot;

    private Action currentAction;

    public Ant(Vector3f position, float rotY) {
        super(Configs.antTexturedModel, position, 0, rotY, 0, .3f);
        currentAction = Action.FORWARD;
        targetPosition = new Vector3f(position);
        lastPosition = new Vector3f(position);
        lastRot = rotY;
        targetRot = rotY;
    }

    public void update(boolean newAction, World world) {
        if (newAction) {
            position.x = targetPosition.x;
            position.z = targetPosition.z;
            lastPosition.x = position.x;
            lastPosition.z = position.z;
            Maths.loopVector(position, new Vector3f(world.getSizeX(), 0, world.getSizeZ()));
            Maths.loopVector(targetPosition, new Vector3f(world.getSizeX(), 0, world.getSizeZ()));
            Maths.loopVector(lastPosition, new Vector3f(world.getSizeX(), 0, world.getSizeZ()));

            rotY = targetRot;
            lastRot = rotY;
            Random rand = new Random();

            switch(rand.nextInt(3)) {
                case 0:
                    currentAction = Action.FORWARD;
                    targetPosition.x = (float) (position.x + Chunk.SIZE * Math.cos(Math.toRadians(rotY)));
                    targetPosition.z = (float) (position.z - Chunk.SIZE * Math.sin(Math.toRadians(rotY)));
                    break;
                case 1:
                    currentAction = Action.ROTATE_LEFT;
                    targetRot -= 90;
                    break;
                case 2:
                    currentAction = Action.ROTATE_RIGHT;
                    targetRot += 90;
                    break;
            }
        } else {
            float percent = (float)DisplayManager.timeSinceLastInterval() / Configs.ACTION_DURATION;
            position.x = (targetPosition.x - lastPosition.x) * percent + lastPosition.x;
            position.z = (targetPosition.z - lastPosition.z) * percent + lastPosition.z;
            rotY = (targetRot - lastRot) * percent + lastRot;
        }
    }
}

enum Action {
    FORWARD,
    ROTATE_LEFT,
    ROTATE_RIGHT,
    PICK_UP,
    GET_HOME,
    PUT
}
