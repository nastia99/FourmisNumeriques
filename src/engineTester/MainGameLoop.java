package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import configs.Configs;
import entity.*;

import gui.MainGUI;
import openGL.utils.Maths;
import openGL.world.Chunk;
import openGL.world.HeightsGenerator;
import openGL.world.World;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import openGL.renderEngine.DisplayManager;
import openGL.renderEngine.Loader;
import openGL.renderEngine.MasterRenderer;
import openGL.entities.Camera;
import openGL.entities.RenderableObject;
import openGL.entities.Light;
import openGL.utils.MousePicker;
import simulation.Simulation;

import javax.swing.*;

public class MainGameLoop {

    public static Loader loader;
    public static JFrame frame;
    public static MainGUI gui;
    public static Simulation simulation;

    public static void main(String[] args) {
        DisplayManager.createDisplay();
        loader = new Loader();
        Configs.init("res/properties.properties");
        Configs.initModels();
        InitGUI();

        simulation = new Simulation("testWorld.xml", gui);
        simulation.run();

        simulation.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
        System.exit(0);
    }

    private static void InitGUI() {
        frame = new JFrame("GUI");
        gui = new MainGUI();
        frame.setContentPane(gui.mainPanel);
        frame.pack();
        frame.setVisible(true);
    }
}
