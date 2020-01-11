package entity.logic;

import entity.Ant;
import entity.logic.action.Action;
import entity.logic.action.ActionForward;
import openGL.world.World;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.*;

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

    public void simplify(Map<String, Boolean> encounteredActions) {
        if (action.isConditional()) {
            Boolean satisfied = encounteredActions.get(action.toString());
            if (!left.action.isConditional() && left.action.equals(right.action)) {
                this.action = right.action;
                this.right = null;
                this.left = null;
            } else if (satisfied != null && satisfied) {
                this.action = right.action;
                this.left = right.left;
                this.right = right.right;
                simplify(encounteredActions);
            } else if (satisfied != null) {
                this.action = left.action;
                this.right = left.right;
                this.left = left.left;
                simplify(encounteredActions);
            }
            recursiveSimplifyCall(encounteredActions, right, true);
            recursiveSimplifyCall(encounteredActions, left, false);
        }
    }

    private void recursiveSimplifyCall(Map<String, Boolean> encounteredActions, Node node, boolean satisfied) {
        if (node != null) {
            Map<String, Boolean> subEncounteredActionsRight = new HashMap<>();

            for (Map.Entry<String, Boolean> entry : encounteredActions.entrySet()) {
                subEncounteredActionsRight.put(entry.getKey(), entry.getValue());
            }
            subEncounteredActionsRight.put(action.toString(), satisfied);
            node.simplify(subEncounteredActionsRight);
        }
    }

    public int getLevel() {
        if (!action.isConditional() || left == null || right == null)
            return 1;
        return Math.max(left.getLevel(), right.getLevel()) + 1;
    }

    public void execute(Ant a, World world) {
        if (!action.isConditional())
            action.execute(a, world);
        else if (action.isConditionSatisfied(a, world))
            left.execute(a, world);
        else
            right.execute(a, world);
    }

    public Element getAsElement(Document document, String type, int level) {
        Element element = document.createElement("node");
        element.setAttribute("type", type);
        element.setAttribute("action", action.toString());
        element.setAttribute("level", String.valueOf(level));
        if (right != null) {
            Element rightElem = right.getAsElement(document, "right", level + 1);
            element.appendChild(rightElem);
        }
        if (left != null) {
            Element leftElem = left.getAsElement(document, "left", level + 1);
            element.appendChild(leftElem);
        }
        return element;
    }

    public static Node createFromElement(Element element, Node parent) {
        String action = element.getAttribute("action");
        Node node = new Node(parent, Action.getAction(action));
        NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            org.w3c.dom.Node n = nodes.item(i);
            if (n.getNodeName().equals("node")) {
                Element elem = (Element) n;
                if (elem.getAttribute("type").equals("left"))
                    node.left = createFromElement(elem, node);
                else if (elem.getAttribute("type").equals("right"))
                    node.right = createFromElement(elem, node);
            }
        }
        return node;
    }
}
