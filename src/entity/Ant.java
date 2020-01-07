package entity;

import configs.Configs;
import entity.logic.Tree;
import openGL.entities.RenderableObject;
import openGL.utils.Maths;
import openGL.world.World;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import openGL.renderEngine.DisplayManager;


public class Ant extends RenderableObject {

    private Vector3f targetPosition;
    private float targetRot;

    private Vector3f lastPosition;
    private float lastRot;

    private Tree decisionTree;
    private Food food;
    private AntHil home;

    public Ant(Vector3f position, float rotY, AntHil home) {
        super(Configs.antTexturedModel, position, 0, rotY, 0, .3f);
        decisionTree = new Tree();
        targetPosition = new Vector3f(position);
        lastPosition = new Vector3f(position);
        lastRot = rotY;
        targetRot = rotY;
        decisionTree = Tree.generateRandomTree(2, 4);
        this.home = home;
    }

    public void update(boolean newAction, World world) {
        calculateRenderingData(world);

        if (food != null) {
            food.getPosition().x = position.x;
            food.getPosition().z = position.z;
        }
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
            decisionTree.makeDecision(this, world);
        }
    }

    private void calculateRenderingData(World world) {
        position.y = world.getHeight(position.x, position.z);

        Vector2f rotXZ = Maths.calculateXZRotations(world, position.x, position.z);
        rotX = rotXZ.x;
        rotZ = rotXZ.y;

        float percent = (float) DisplayManager.timeSinceLastInterval() / Configs.ACTION_DURATION;
        position.x = (targetPosition.x - lastPosition.x) * percent + lastPosition.x;
        position.z = (targetPosition.z - lastPosition.z) * percent + lastPosition.z;
        rotY = (targetRot - lastRot) * Maths.clamp((float) (2.618033 * percent), 0, 1) + lastRot;
    }

    public void setTargetPosition(Vector3f targetPosition) {
        this.targetPosition = targetPosition;
    }

    public void setTargetRot(float targetRot) {
        this.targetRot = targetRot;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public boolean isCarryingFood() {
        return food != null;
    }

    public Vector3f getTargetPosition() {
        return targetPosition;
    }

    public float getTargetRot() {
        return targetRot;
    }

    public AntHil getHome() {
        return home;
    }
}

