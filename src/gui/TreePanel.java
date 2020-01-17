package gui;

import entity.Ant;
import entity.logic.Node;
import entity.logic.action.ActionRight;
import openGL.world.World;
import org.lwjgl.util.vector.Vector2f;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

public class TreePanel extends JPanel {

    private Ant ant;
    private World world;

    public void setWorld(World world) {
        this.world = world;
    }

    public void setAnt(Ant ant) {
        this.ant = ant;
    }

    private void drawTree(Graphics g) {
        if (ant != null) {
            int level = ant.getDecisionTree().getLevel();
            int levelHeight = getHeight() / level;
            Node node = ant.getDecisionTree().getHead();
            drawNode(node, 0, 0, levelHeight, g, true);
        }
    }

    private void drawNode(Node node, int x, int level, int levelHeight, Graphics g, boolean activeBranch) {
        int levelSection = level == 0 ? 1 : (int) Math.pow(2, level);
        int sectionWidth = getWidth() / levelSection;
        boolean conditionSatisfied = node.getAction().isConditionSatisfied(ant, world);

        Font font = new Font("Serif", Font.PLAIN, 20);
        Rectangle section = new Rectangle(x * getWidth() / levelSection, levelHeight * level, sectionWidth, levelHeight);
        if (conditionSatisfied && activeBranch)
            g.setColor(Color.GREEN);
        else if (activeBranch)
            g.setColor(Color.RED);
        else
            g.setColor(Color.BLACK);
        centerString(g, section, node.getAction().toString(), font);
        Vector2f middle = new Vector2f(section.width / 2 + section.x, section.height / 2 + section.y);
        g.setColor(Color.BLACK);
        if (node.getLeft() != null) {
            if (!conditionSatisfied && activeBranch)
                g.setColor(Color.GREEN);
            else
                g.setColor(Color.BLACK);
            if (node.getAction().isConditional()) {
                g.drawLine((int) middle.x - 10, (int) middle.y + 15, (int) middle.x - section.width / 4, (int) middle.y - 15 + levelHeight);
                drawNode(node.getLeft(), 2 * x, level + 1, levelHeight, g, activeBranch && !conditionSatisfied);
            }
        }
        if (node.getRight() != null) {
            if (conditionSatisfied && activeBranch)
                g.setColor(Color.GREEN);
            else
                g.setColor(Color.BLACK);
            if (node.getAction().isConditional()) {
                g.drawLine((int) middle.x + 10, (int) middle.y + 15, (int) middle.x + section.width / 4, (int) middle.y - 15 + levelHeight);
                drawNode(node.getRight(), 2 * x + 1, level + 1, levelHeight, g, activeBranch && conditionSatisfied);
            }
        }
    }

    public void centerString(Graphics g, Rectangle r, String s, Font font) {
        FontRenderContext frc = new FontRenderContext(null, true, true);

        Rectangle2D r2D = font.getStringBounds(s, frc);
        int rWidth = (int) Math.round(r2D.getWidth());
        int rHeight = (int) Math.round(r2D.getHeight());
        int rX = (int) Math.round(r2D.getX());
        int rY = (int) Math.round(r2D.getY());

        int a = (r.width / 2) - (rWidth / 2) - rX;
        int b = (r.height / 2) - (rHeight / 2) - rY;

        g.setFont(font);
        g.drawString(s, r.x + a, r.y + b);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        if (ant != null && world != null)
            drawTree(g);
    }
}
