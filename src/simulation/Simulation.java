package simulation;

import configs.Configs;
import entity.Ant;
import entity.AntHil;
import entity.Population;
import entity.Tile;
import gui.MainGUI;
import openGL.entities.Camera;
import openGL.entities.Light;
import openGL.entities.RenderableObject;
import openGL.renderEngine.DisplayManager;
import openGL.renderEngine.MasterRenderer;
import openGL.utils.Maths;
import openGL.utils.MousePicker;
import openGL.world.World;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Simulation {

    private MasterRenderer renderer;

    private World world;

    private Camera camera;
    private Light light;
    private MousePicker mousePicker;
    private MainGUI gui;

    private boolean renderSelectedOnly = false;
    private boolean renderBestOnly = false;
    private boolean running = true;

    private Ant selectedAnt = null;
    private Ant bestAnt = null;

    public Simulation(String file, MainGUI gui) {
        world = World.loadFromXML(file);
        if (world == null) {
            world = new World();
            List<AntHil> homes = new ArrayList<>();
            Vector2f rotXZ = Maths.calculateXZRotations(world, 25.5f, 25.5f);
            AntHil home = new AntHil(new Vector3f(25.5f, world.getHeight(25.5f, 25.5f), 25.5f), rotXZ.x, 0, rotXZ.y);
            homes.add(home);
            ((Tile) world.getChunk(25, 25)).addEntity(home);
            Population population = new Population();
            population.populate(Configs.nbAnts, world, homes);
            world.setPopulation(population);
        }
        camera = new Camera(world);
        renderer = new MasterRenderer(world);
        light = new Light(new Vector3f(20000, 20000, 20000), new Vector3f(1, 1, 1));
        mousePicker = new MousePicker(camera, renderer.getProjectionMatrix(), world.getPopulation().getAnts());
        this.gui = gui;
        gui.setWorld(world);
    }

    public void run() {
        while (!Display.isCloseRequested()) {
            if (Keyboard.isKeyDown(Keyboard.KEY_W) && renderer.canToogleWireframe())
                renderer.toogleWireframe();
            camera.update();
            checkForFocus();
            if (running) {
                boolean newAction = DisplayManager.intervalHasPassed(Configs.ACTION_DURATION);
                if (renderSelectedOnly) {
                    selectedAnt.update(newAction, world);
                    renderer.registerAnt(selectedAnt);
                } else if (renderBestOnly) {
                    bestAnt.update(newAction, world);
                    renderer.registerAnt(bestAnt);
                } else {
                    bestAnt = world.getPopulation().getAnts().get(0);
                    for (Ant ant : world.getPopulation().getAnts()) {
                        ant.update(newAction, world);
                        if (ant.getFitnessScore() > bestAnt.getFitnessScore()) ;
                        bestAnt = ant;
                    }
                    renderer.registerAnts(world.getPopulation().getAnts());
                }
                renderer.registerRenderableObjects(world.extractEntities());
                renderer.render(light, camera, Keyboard.isKeyDown(Keyboard.KEY_H));

                gui.setBestAnt(bestAnt);
                gui.updateCanvas();
            }

            DisplayManager.updateDisplay();
        }
    }

    private void checkForFocus() {
        mousePicker.update();
        if (Mouse.isButtonDown(0) && mousePicker.canSelectEntity()) {
            RenderableObject e = mousePicker.getClickedEntity(camera.getFocus());
            if (e != null) {
                camera.setFocus(e);
                if (e instanceof Ant) {
                    gui.setSelectedAnt((Ant) e);
                    selectedAnt = (Ant)e;
                } else {
                    gui.setSelectedAnt(null);
                    selectedAnt = null;
                }
            } else {
                camera.setFocus(world);
                gui.setSelectedAnt(null);
                selectedAnt = null;
            }
        }
    }

    public void cleanUp() {
        renderer.clear();
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isRenderSelectedOnly() {
        return renderSelectedOnly;
    }

    public void setRenderSelectedOnly(boolean renderSelectedOnly) {
        if (renderSelectedOnly && selectedAnt == null) return;
        this.renderSelectedOnly = renderSelectedOnly;
        if (renderSelectedOnly)
            renderBestOnly = false;
    }

    public boolean isRenderBestOnly() {
        return renderBestOnly;
    }

    public void setRenderBestOnly(boolean renderBestOnly) {
        if (renderBestOnly && bestAnt == null) return;
        this.renderBestOnly = renderBestOnly;
        if (renderBestOnly)
            renderSelectedOnly = false;
    }
}
