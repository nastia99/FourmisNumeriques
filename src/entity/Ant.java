package entity;

import configs.Configs;
import engineTester.MainGameLoop;
import openGL.entities.RenderableObject;
import openGL.models.TexturedModel;
import openGL.renderEngine.OBJLoader;
import openGL.textures.ModelTexture;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;
import openGL.renderEngine.DisplayManager;

public class Ant extends RenderableObject {

    private static final float RUN_SPEED = 10;
    private static final float TURN_SPEED = 160;

    private float currentSpeed;
    private float currentTurnSpeed;

    public Ant(Vector3f position, float rotY) {
        super(Configs.antTexturedModel, position, 0, rotY, 0, .3f);
    }

    public void move() {
        checkInputs();
        super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
        float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
        super.increasePosition((float)Math.cos(Math.toRadians(-getRotY())) * distance, 0, (float)Math.sin(Math.toRadians(-getRotY())) * distance);
    }

    public void checkInputs() {
        if (Keyboard.isKeyDown(Keyboard.KEY_Z)) {
            currentSpeed = RUN_SPEED;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            currentSpeed = -RUN_SPEED;
        } else {
            currentSpeed = 0;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
            currentTurnSpeed = TURN_SPEED;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            currentTurnSpeed = -TURN_SPEED;
        } else {
            currentTurnSpeed = 0;
        }
    }
}
