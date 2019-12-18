package entity.logic;

import entity.Ant;
import entity.logic.action.*;
import openGL.world.World;

public class Tree {

    private Node head;

    public Tree() {
        this.head = new Node(null, new ActionForward());
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

    public static Tree generateRandomTree(int minLevel, int maxLevel) {
        Tree tree = new Tree();
        tree.head = new Node(null, Action.getRandomConditionalAction());
        generateSubTree(tree.head, 1, minLevel, maxLevel);
        return tree;
    }

    private static void generateSubTree(Node current, int currentLevel, int minLevel, int maxLevel) {
        if (current.getAction().isConditional()) {
            if (currentLevel < maxLevel && currentLevel >= minLevel) {
                current.setLeft(new Node(current, Action.getRandomAction()));
                current.setRight(new Node(current, Action.getRandomAction()));
            } else if (currentLevel < minLevel){
                current.setLeft(new Node(current, Action.getRandomConditionalAction()));
                current.setRight(new Node(current, Action.getRandomConditionalAction()));
            } else {
                current.setLeft(new Node(current, Action.getRandomSimpleAction()));
                current.setRight(new Node(current, Action.getRandomSimpleAction()));
            }
        }
        if (current.getAction().isConditional()) {
            generateSubTree(current.getRight(), currentLevel + 1, minLevel, maxLevel);
            generateSubTree(current.getLeft(), currentLevel + 1, minLevel, maxLevel);
        }
    }
}
