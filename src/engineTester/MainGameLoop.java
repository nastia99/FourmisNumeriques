package engineTester;

import configs.Configs;

import gui.MainGUI;

import openGL.renderEngine.DisplayManager;
import openGL.renderEngine.Loader;
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

        simulation = new Simulation(gui);
        simulation.run();

        simulation.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
        System.exit(0);
    }

    private static void InitGUI() {
        frame = new JFrame("GUI");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        gui = new MainGUI();
        frame.setSize(1920/2, 800);
        frame.setLocation(0, 0);
        frame.setContentPane(gui.mainPanel);
        frame.setVisible(true);
    }
}
