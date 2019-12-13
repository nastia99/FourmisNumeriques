package openGL.renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import openGL.models.TexturedModel;

import openGL.shaders.HitboxShader;
import openGL.utils.Maths;
import openGL.world.World;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import openGL.shaders.StaticShader;
import openGL.shaders.WorldShader;
import openGL.entities.Camera;
import openGL.entities.RenderableObject;
import openGL.entities.Light;

public class MasterRenderer {
	
	private static final float FOV = 70;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 60;

	private boolean isWireframe = false;
	private boolean canToogleWireframe = true;

	private Matrix4f projectionMatrix;
	
	private StaticShader entityShader = new StaticShader();
	private HitboxShader hitboxShader = new HitboxShader();
	private EntityRenderer entityRenderer;
	
	private WorldRenderer worldRenderer;
	private WorldShader worldShader = new WorldShader();
	
	
	private Map<TexturedModel,List<RenderableObject>> entities = new HashMap<TexturedModel,List<RenderableObject>>();
	private World world;
	
	public MasterRenderer(World world){
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		createProjectionMatrix();
		entityRenderer = new EntityRenderer(entityShader, hitboxShader, projectionMatrix);
		worldRenderer = new WorldRenderer(worldShader, projectionMatrix);
		this.world = world;
	}
	
	public void render(Light sun,Camera camera){
		prepare();
		if (isWireframe) {
			hitboxShader.start();
			hitboxShader.viewMatrix.loadMatrix(Maths.createViewMatrix(camera));
			entityRenderer.renderHitbox(entities);
			hitboxShader.stop();
		} else {
			entityShader.start();
			entityShader.lightColour.loadVec3(sun.getColour());
			entityShader.lightPosition.loadVec3(sun.getPosition());
			entityShader.viewMatrix.loadMatrix(Maths.createViewMatrix(camera));
			entityRenderer.render(entities);
			entityShader.stop();
		}
		worldShader.start();
		worldShader.lightColour.loadVec3(sun.getColour());
		worldShader.lightPosition.loadVec3(sun.getPosition());
		worldShader.viewMatrix.loadMatrix(Maths.createViewMatrix(camera));
		worldRenderer.render(world);
		worldShader.stop();
		entities.clear();
	}
	
	public void registerRenderableObjects(List<RenderableObject> renderableObjects){
		for (RenderableObject renderableObject : renderableObjects) {
			if (renderableObject.canBeRendered()) {
				TexturedModel entityModel = renderableObject.getModel();
				List<RenderableObject> batch = entities.get(entityModel);
				if (batch != null) {
					batch.add(renderableObject);
				} else {
					List<RenderableObject> newBatch = new ArrayList<RenderableObject>();
					newBatch.add(renderableObject);
					entities.put(entityModel, newBatch);
				}
			}
		}
	}
	
	public void clear(){
		entityShader.clear();
		worldShader.clear();
	}
	
	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(0.49f, 89f, 0.98f, 1);
		if (!Keyboard.isKeyDown(Keyboard.KEY_W))
			canToogleWireframe = true;
	}
	
	private void createProjectionMatrix() {
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public void toogleWireframe() {
		canToogleWireframe = false;
		isWireframe = !isWireframe;
		if (isWireframe) {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		} else {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		}
	}

	public boolean canToogleWireframe() {
		return canToogleWireframe;
	}
}
