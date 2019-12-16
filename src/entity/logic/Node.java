package entity.logic;

import entity.Ant;
import entity.logic.action.Action;
import openGL.world.World;

public class Node {

    private Node right;
    private Node left;
    private Node parent;
    private Action action;

    public Node(Node parent, Action action) {
        this.parent = parent;
        this.action = action;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Action getAction() {
        return action;
    }

    public void execute(Ant a, World world) {
        if (!action.isConditional())
            action.execute(a, world);
        else if (action.isConditionSatisfied(a, world))
            left.execute(a, world);
        else
            right.execute(a, world);
    }
}
