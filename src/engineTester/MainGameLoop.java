package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import configs.Configs;
import entity.*;

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

public class MainGameLoop {

    public static Loader loader;

    public static void main(String[] args) {
        DisplayManager.createDisplay();
        loader = new Loader();
        Configs.init();

        World world = generateWorld(50, 200);

        Vector2f rotXZ = Maths.calculateXZRotations(world, 25.5f, 25.5f);
        AntHil home = new AntHil(new Vector3f(25.5f, world.getHeight(25.5f, 25.5f), 25.5f), rotXZ.x, 0, rotXZ.y);
        ((Tile) world.getChunk(25, 25)).addEntity(home);

        List<RenderableObject> ants = generateAnts(200, home, world);

        Camera camera = new Camera(world);
        MasterRenderer renderer = new MasterRenderer(world);
        Light light = new Light(new Vector3f(20000, 20000, 20000), new Vector3f(1, 1, 1));
        MousePicker mousePicker = new MousePicker(camera, renderer.getProjectionMatrix(), ants);

        while (!Display.isCloseRequested()) {
            if (Keyboard.isKeyDown(Keyboard.KEY_W) && renderer.canToogleWireframe())
                renderer.toogleWireframe();
            camera.update();
            checkForFocus(world, camera, mousePicker);
            boolean newAction = DisplayManager.intervalHasPassed(Configs.ACTION_DURATION);
            for (RenderableObject ant : ants) {
                ((Ant) ant).update(newAction, world);
            }

            renderer.registerRenderableObjects(world.extractEntities());
            renderer.registerRenderableObjects(ants);
            renderer.render(light, camera, Keyboard.isKeyDown(Keyboard.KEY_H));

            DisplayManager.updateDisplay();
        }

        renderer.clear();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }

    private static void checkForFocus(World world, Camera camera, MousePicker mousePicker) {
        mousePicker.update();
        if (Mouse.isButtonDown(0) && mousePicker.canSelectEntity()) {
            RenderableObject e = mousePicker.getClickedEntity(camera.getFocus());
            if (e != null)
                camera.setFocus(e);
            else
                camera.setFocus(world);
        }
    }

    private static World generateWorld(int size, int nbFood) {
        HeightsGenerator generator = new HeightsGenerator(100000, size * (Chunk.VERTEX_COUNT - 1), size * (Chunk.VERTEX_COUNT - 1));
        generator.generateHeight(new Vector2f(size / 2f, size / 2f));
        Tile[][] tiles = new Tile[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                tiles[i][j] = new Tile(i, j, generator);
            }
        }
        World world = new World(tiles, generator);
        addFood(world, 200);
        return world;
    }

    private static void addFood(World world, int nbFood) {
        Random random = new Random();
        for (int i = 0; i < nbFood; i++) {
            int x = random.nextInt(world.getSizeX());
            int y = random.nextInt(world.getSizeZ());
            Tile tile = (Tile) world.getChunk(x, y);
            if (tile != null && !tile.contains(EntityTypes.FOOD)) {
                Vector2f rotXZ = Maths.calculateXZRotations(world, x + .5f, y + .5f);
                tile.addEntity(new Food(new Vector3f(x + .5f, world.getHeight(x + .5f, y + .5f), y + .5f), rotXZ.x, random.nextFloat() * 360, rotXZ.y));
            }
        }
    }

    private static List<RenderableObject> generateAnts(int nbAnts, AntHil home, World world) {
        List<RenderableObject> ants = new ArrayList<RenderableObject>();
        Random rand = new Random();
        for (int i = 0; i < 200; i++) {
            ants.add(new Ant(new Vector3f(rand.nextInt(world.getSizeX()) + 0.5f, 0, rand.nextInt(world.getSizeZ()) + 0.5f), 0, home));
        }
        return ants;
    }
}
