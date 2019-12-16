package entity.logic;

import entity.Ant;
import entity.logic.action.Action;
import entity.logic.action.ActionForward;
import openGL.world.World;

public class Tree {

    private Node head;

    public Tree() {
        this.head = new Node(null, new ActionForward());
        //Todo Generate random tree
    }

    public void makeDecision(Ant a, World world) {
        head.execute(a, world);
    }

    public void simplify() {

    }

    public static Tree crossBread(Tree t1, Tree t2, boolean mutation) {
        return null;
    }

    public static Tree loadFromString(String text) {
        return null;
    }
}
