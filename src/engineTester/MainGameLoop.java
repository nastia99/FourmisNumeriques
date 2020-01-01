package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import configs.Configs;
import entity.Ant;

import entity.EntityTypes;
import entity.Food;
import entity.Tile;
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
		World world = generateWorld(50);
		addFood(world, 30);
		Camera camera = new Camera(world);
		MasterRenderer renderer = new MasterRenderer(world);
		List<RenderableObject> ants = new ArrayList<RenderableObject>();
		Light light = new Light(new Vector3f(20000,20000,20000),new Vector3f(1,1,1));
		MousePicker mousePicker = new MousePicker(camera, renderer.getProjectionMatrix(), ants);

		Random rand = new Random();
		for (int i = 0; i < 10; i++) {
			ants.add(new Ant(new Vector3f(rand.nextInt(world.getSizeX()) + 0.5f, 0,rand.nextInt(world.getSizeZ()) + 0.5f), 0));
		}

		while(!Display.isCloseRequested()){
			if (Keyboard.isKeyDown(Keyboard.KEY_W) && renderer.canToogleWireframe())
				renderer.toogleWireframe();
			camera.update();
			checkForFocus(world, camera, mousePicker);
			boolean newAction = DisplayManager.intervalHasPassed(Configs.ACTION_DURATION);
			for (RenderableObject ant : ants) {
				((Ant)ant).update(newAction, world);
			}

			renderer.registerRenderableObjects(extractEntities(world));
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

	private static World generateWorld(int size) {
		HeightsGenerator generator = new HeightsGenerator(100000, size * (Chunk.VERTEX_COUNT - 1), size * (Chunk.VERTEX_COUNT - 1));
		generator.generateHeight(new Vector2f(size/2f, size/2f));
		Tile[][] tiles = new Tile[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				tiles[i][j] = new Tile(i, j, generator);
			}
		}
		return new World(tiles, generator);
	}

	private static List<RenderableObject> extractEntities(World world) {
		List<RenderableObject> list = new ArrayList<RenderableObject>();
		for (int i = 0; i < world.getSizeX(); i++) {
			for (int j = 0; j < world.getSizeZ(); j++) {
				Tile tile = (Tile) world.getChunk(i, j);
				if (tile != null && tile.contains(EntityTypes.FOOD))
					list.addAll(tile.getEntities());
			}
		}
		return list;
	}

	private static void addFood(World world, int nbFood) {
		Random random = new Random();
		for (int i = 0; i < nbFood; i++) {
			int x = random.nextInt(world.getSizeX());
			int y = random.nextInt(world.getSizeZ());
			Tile tile = (Tile) world.getChunk(x, y);
			if (tile != null && !tile.contains(EntityTypes.FOOD))
				tile.addEntity(new Food(new Vector3f(x+.5f, world.getHeight(x+.5f, y+.5f), y+.5f), 0, 0, 0));
		}
	}
}
