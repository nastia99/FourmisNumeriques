package entity.logic;

import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

public class TreeCanvas extends Canvas {

    private Tree logicTree;

    public Tree getLogicTree() {
        return logicTree;
    }

    public void setLogicTree(Tree logicTree) {
        this.logicTree = logicTree;
    }

    private void drawTree(Graphics g) {
        int level = logicTree.getLevel();
        int levelHeight = getHeight() / level;
        drawNode(logicTree.getHead(), 0, 0, levelHeight, g);
    }

    private void drawNode(Node node, int x, int level, int levelHeight, Graphics g) {
        int levelSection = level == 0 ? 1 : (int) Math.pow(2, level);
        int sectionWidth = getWidth() / levelSection;

        Font font = new Font("Serif", Font.PLAIN, 12);
        Rectangle section = new Rectangle(x * getWidth() / levelSection, levelHeight * level, sectionWidth, levelHeight);
        centerString(g, section, node.getAction().toString(), font);

        Vector2f middle = new Vector2f(section.width / 2 + section.x, section.height / 2 + section.y);
        if (node.getLeft() != null) {
            g.drawLine((int) middle.x - 10, (int) middle.y + 10, (int) middle.x - section.width / 4, (int) middle.y - 10 + levelHeight);
            drawNode(node.getLeft(), 2 * x, level + 1, levelHeight, g);
        }
        if (node.getRight() != null) {
            g.drawLine((int) middle.x + 10, (int) middle.y + 10, (int) middle.x + section.width / 4, (int) middle.y - 10 + levelHeight);
            drawNode(node.getRight(), 2 * x + 1, level + 1, levelHeight, g);
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
    public void paint(Graphics g) {
        drawTree(g);
    }
}
