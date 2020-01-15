package entity.logic;

import entity.Ant;
import entity.logic.action.*;
import openGL.world.World;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Tree {

    private Node head;

    public Tree() {
        this.head = new Node(null, new ActionForward());
    }

    public void makeDecision(Ant a, World world) {
        head.execute(a, world);
    }

    public Element getAsElement(Document document) {
        Element treeNode = document.createElement("tree");

        Element headElement = head.getAsElement(document, "head", 0);
        treeNode.appendChild(headElement);
        return treeNode;
    }

    public void simplify() {
        Map<String, Boolean> encounteredActions = new HashMap<>();
        head.simplify(encounteredActions);
    }

    public int getLevel() {
        return head.getLevel();
    }

    public Node getHead() {
        return head;
    }

    public static Tree crossBread(Tree t1, Tree t2, boolean mutation) {
        return null;
    }

    public static Tree getFromElement(Element elem) {
        if (elem.getTagName().equals("tree")) {
            NodeList nodes = elem.getChildNodes();
            Tree tree = new Tree();
            for (int j = 0; j < nodes.getLength(); j++) {
                org.w3c.dom.Node node = nodes.item(j);
                if (node.getNodeName().equals("node")) {
                    tree.head = Node.createFromElement((Element) node, null);
                }
            }
            return tree;
        }
        return null;
    }

    public void saveToXML(String file) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element antNode = getAsElement(document);
            document.appendChild(antNode);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(file);

            transformer.transform(domSource, streamResult);

        } catch (ParserConfigurationException | TransformerException pce) {
            pce.printStackTrace();
        }
    }

    public static void saveListtoXML(String path, List<Tree> trees) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            for (Tree tree : trees) {
                Element treeNode = tree.getAsElement(document);
                document.appendChild(treeNode);
            }
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(path));

            transformer.transform(domSource, streamResult);

        } catch (ParserConfigurationException | TransformerException pce) {
            pce.printStackTrace();
        }
    }

    public static Tree loadFromXML(String file) {
        Tree tree = new Tree();
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document xml;

            xml = builder.parse(file);
            Element treeNode = (Element) xml.getElementsByTagName("tree").item(0);
            if (treeNode == null)
                return null;
            tree = Tree.getFromElement(treeNode);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return tree;
    }

    public static List<Tree> loadListFromXML(String path) {
        List<Tree> trees = new ArrayList<Tree>();

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            File fileXML = new File(path);
            Document xml;

            xml = builder.parse(fileXML);
            NodeList treeNodes = xml.getElementsByTagName("tree");
            for (int i = 0; i < treeNodes.getLength(); i++) {
                Element element = (Element)treeNodes.item(i);
                NodeList nodes = element.getChildNodes();
                Tree tree = new Tree();
                for (int j = 0; j < nodes.getLength(); j++) {
                    org.w3c.dom.Node node = nodes.item(j);
                    if (node.getNodeName().equals("node")) {
                        tree.head = Node.createFromElement((Element) node, null);
                    }
                }
                trees.add(tree);
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return trees;
    }

    /**
     * Return a random tree that has been simplified
     * @param minLevel minimum level of the unsimplified tree
     * @param maxLevel maximum level of the unsimplified tree
     * @return a simplified random tree
     */
    public static Tree generateRandomTree(int minLevel, int maxLevel) {
        Tree tree = new Tree();
        tree.head = new Node(null, Action.getRandomConditionalAction());
        generateSubTree(tree.head, 1, minLevel, maxLevel);
        tree.simplify();
        return tree;
    }

    private static void generateSubTree(Node current, int currentLevel, int minLevel, int maxLevel) {
        if (current.getAction().isConditional()) {
            if (currentLevel <= maxLevel && currentLevel >= minLevel) {
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
