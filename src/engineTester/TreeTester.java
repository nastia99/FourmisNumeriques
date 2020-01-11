package engineTester;

import configs.Configs;
import entity.logic.Tree;
import entity.logic.TreeCanvas;

import javax.swing.*;
import java.io.IOException;

public class TreeTester {

    public static void main(String[] args) throws IOException {
        Configs.init("res/properties.properties");
        Tree tree = Tree.loadFromXML("test.xml").get(0);
        tree.simplify();
        JFrame frame = new JFrame("My Drawing");
        TreeCanvas canvas = new TreeCanvas();
        canvas.setLogicTree(tree);
        canvas.setSize(1000, 600);
        frame.add(canvas);
        frame.pack();
        frame.setVisible(true);
    }
}
