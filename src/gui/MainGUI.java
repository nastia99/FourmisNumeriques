package gui;

import engineTester.MainGameLoop;
import entity.Ant;
import entity.AntHil;
import entity.Food;
import entity.Population;
import entity.logic.Tree;
import openGL.entities.RenderableObject;
import openGL.utils.Maths;
import openGL.world.World;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class MainGUI {

    private JTabbedPane tabContainer;
    public JPanel mainPanel;
    private JPanel selected;
    private JPanel best;
    private JPanel option;
    private JButton saveSelected;
    private JButton loadSelected;
    private JButton isolateSelected;
    private JButton editSelected;
    private JPanel selectedControlPanel;
    private JPanel selectedTreeSubPanel;
    private JButton saveBest;
    private JButton loadBest;
    private JButton isolateBest;
    private JButton editBest;
    private JPanel bestControlPanel;
    private JPanel bestTreeSubPanel;
    private TreePanel selectedTreePanel;
    private TreePanel bestTreePanel;
    private Ant selectedAnt;
    private Ant bestAnt;

    public MainGUI() {
        selectedTreePanel = new TreePanel();
        selectedTreePanel.setMinimumSize(new Dimension(1000, 600));
        selectedTreePanel.setBorder(BorderFactory.createRaisedBevelBorder());
        selectedTreeSubPanel.add(selectedTreePanel);

        bestTreePanel = new TreePanel();
        bestTreePanel.setMinimumSize(new Dimension(1000, 600));
        bestTreePanel.setBorder(BorderFactory.createRaisedBevelBorder());
        bestTreeSubPanel.add(bestTreePanel);

        saveSelected.addActionListener(actionEvent -> {
            MainGameLoop.simulation.setRunning(false);
            if (selectedAnt != null) saveTreeToFile(selectedAnt.getDecisionTree());
            MainGameLoop.simulation.setRunning(true);
        });
        loadSelected.addActionListener(actionEvent -> {
            MainGameLoop.simulation.setRunning(false);
            if (selectedAnt != null) {
                Tree tree = loadTreeFromFile();
                if (tree != null) selectedAnt.setDecisionTree(tree);
            }
            MainGameLoop.simulation.setRunning(true);
        });
        isolateSelected.addActionListener(actionEvent -> {
            if (selectedAnt != null) {
                boolean state = MainGameLoop.simulation.isRenderSelectedOnly();
                MainGameLoop.simulation.setRenderSelectedOnly(!state);
                if (!state) isolateSelected.setText("Show All");
                else isolateSelected.setText("Isolate");
            }
        });

        saveBest.addActionListener(actionEvent -> {
            MainGameLoop.simulation.setRunning(false);
            if (bestAnt != null) saveTreeToFile(bestAnt.getDecisionTree());
            MainGameLoop.simulation.setRunning(true);
        });
        loadBest.addActionListener(actionEvent -> {
            MainGameLoop.simulation.setRunning(false);
            if (bestAnt != null) {
                Tree tree = loadTreeFromFile();
                if (tree != null) bestAnt.setDecisionTree(tree);
            }
            MainGameLoop.simulation.setRunning(true);
        });
        isolateBest.addActionListener(actionEvent -> {
            if (bestAnt != null) {
                boolean state = MainGameLoop.simulation.isRenderBestOnly();
                MainGameLoop.simulation.setRenderBestOnly(!state);
                if (!state) isolateBest.setText("Show All");
                else isolateBest.setText("Isolate");
            }
        });
    }


    public void setSelectedAnt(Ant ant) {
        selectedAnt = ant;
        selectedTreePanel.setAnt(ant);
        selectedTreePanel.updateUI();
    }

    public void setBestAnt(Ant ant) {
        bestAnt = ant;
        bestTreePanel.setAnt(ant);
        bestTreePanel.updateUI();
    }

    public void setWorld(World world) {
        selectedTreePanel.setWorld(world);
        bestTreePanel.setWorld(world);
    }

    public void updateCanvas() {
        selectedTreePanel.updateUI();
    }

    public void saveTreeToFile(Tree tree) {
        JFileChooser fc = new JFileChooser();
        File file = null;
        int returnVal = fc.showOpenDialog(this.mainPanel);

        if (returnVal == JFileChooser.APPROVE_OPTION)
            file = fc.getSelectedFile();
        else
            return;
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element antNode = tree.getAsElement(document);
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

    public Tree loadTreeFromFile() {
        JFileChooser fc = new JFileChooser();
        File file = null;
        int returnVal = fc.showOpenDialog(this.mainPanel);

        if (returnVal == JFileChooser.APPROVE_OPTION)
            file = fc.getSelectedFile();
        else
            return null;
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
}
