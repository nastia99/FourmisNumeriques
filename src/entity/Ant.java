package entity;

import configs.Configs;
import entity.logic.Tree;
import openGL.entities.RenderableObject;
import openGL.utils.Maths;
import openGL.world.World;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import openGL.renderEngine.DisplayManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class Ant extends RenderableObject implements Comparable<Ant> {

    private Vector3f targetPosition;
    private float targetRot;

    private Vector3f lastPosition;
    private float lastRot;

    private Tree decisionTree;
    private Food food;
    private Vector2f home;

    private float fitnessScore = 0;

    public Ant(Vector3f position, float rotY, Vector2f home) {
        super(Configs.antTexturedModel, position, 0, rotY, 0, .3f);
        decisionTree = new Tree();
        targetPosition = new Vector3f(position);
        lastPosition = new Vector3f(position);
        lastRot = rotY;
        targetRot = rotY;
        decisionTree = Tree.generateRandomTree(3, 7);
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

    public Vector2f getHome() {
        return home;
    }

    public float getFitnessScore() {
        return fitnessScore;
    }

    public void addFitness(float fit) {
        fitnessScore += fit;
    }

    /**
     * Get the ant as a dom element containing its decision tree, position, fitness score, carried food and home
     * @param document the dom used to generate the element
     * @return an element representing the ant
     */
    public Element getAsElement(Document document) {
        Element antNode = document.createElement("ant");

        Element treeElement = decisionTree.getAsElement(document);
        antNode.appendChild(treeElement);
        antNode.setAttribute("posX", String.valueOf(position.x - 5f));
        antNode.setAttribute("rotY", String.valueOf(rotY));
        antNode.setAttribute("posZ", String.valueOf(position.z - .5f));
        antNode.setAttribute("homeX", String.valueOf(home.x - .5f));
        antNode.setAttribute("homeZ", String.valueOf(home.y - .5f));
        antNode.setAttribute("fitness", String.valueOf(fitnessScore));
        return antNode;
    }

    public static Ant getFromElement(Element elem) {
        if (elem.getTagName().equals("ant")) {
            float posX = Float.parseFloat(elem.getAttribute("posX")) + .5f;
            float posZ = Float.parseFloat(elem.getAttribute("posZ")) + .5f;
            float rotY = Float.parseFloat(elem.getAttribute("rotY"));
            float homeX = Float.parseFloat(elem.getAttribute("homeX")) + .5f;
            float homeZ = Float.parseFloat(elem.getAttribute("homeZ")) + .5f;
            Element treeElem = (Element) elem.getElementsByTagName("tree").item(0);
            Tree tree = Tree.getFromElement(treeElem);
            Ant ant = new Ant(new Vector3f(posX, 0, posZ), rotY, new Vector2f(homeX, homeZ));
            if (tree != null)
                ant.decisionTree = tree;
            return ant;
        }
        return null;
    }

    @Override
    public int compareTo(Ant ant) {
        return Float.compare(fitnessScore, ant.fitnessScore);
    }
}

