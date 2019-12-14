package engineTester;

import java.util.ArrayList;
import java.util.List;

import configs.Configs;
import entity.Ant;

import openGL.world.World;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
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

		World world = new World(20, 20);
		Camera camera = new Camera(world);
		MasterRenderer renderer = new MasterRenderer(world);

		List<RenderableObject> renderableObjects = new ArrayList<RenderableObject>();
		Light light = new Light(new Vector3f(20000,20000,2000),new Vector3f(1,1,1));

		MousePicker mousePicker = new MousePicker(camera, renderer.getProjectionMatrix(), renderableObjects);

		for(int i=0;i<20;i+=2){
			for(int j=0;j<20;j+=2){
				renderableObjects.add(new Ant(new Vector3f(i+.5f, 0, j+.5f), 0));
			}
		}

		RenderableObject selected;

		while(!Display.isCloseRequested()){
			if (Keyboard.isKeyDown(Keyboard.KEY_W) && renderer.canToogleWireframe())
				renderer.toogleWireframe();
			camera.update();
			mousePicker.update();
			if (Mouse.isButtonDown(0) && mousePicker.canSelectEntity()) {
				RenderableObject e = mousePicker.getClickedEntity(camera.getFocus());
				if (e != null)
					camera.setFocus(e);
				else
					camera.setFocus(world);
			}

			selected = camera.getFocus();
			if (selected instanceof Ant)
				((Ant)selected).move();

			renderer.registerRenderableObjects(renderableObjects);

			renderer.render(light, camera, Keyboard.isKeyDown(Keyboard.KEY_H));

			DisplayManager.updateDisplay();
		}

		renderer.clear();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}

}
