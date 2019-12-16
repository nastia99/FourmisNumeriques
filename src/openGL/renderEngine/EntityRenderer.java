package openGL.renderEngine;

import java.util.List;
import java.util.Map;

import configs.Configs;
import openGL.models.Model;
import openGL.models.TexturedModel;

import openGL.shaders.HitboxShader;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import openGL.shaders.StaticShader;
import openGL.textures.ModelTexture;
import openGL.utils.Maths;
import openGL.entities.RenderableObject;

public class EntityRenderer {

	private StaticShader shader;
	private HitboxShader hitboxShader;

	public EntityRenderer(StaticShader shader, HitboxShader hitboxShader ,Matrix4f projectionMatrix) {
		this.shader = shader;
		this.hitboxShader = hitboxShader;
		shader.start();
		shader.projectionMatrix.loadMatrix(projectionMatrix);
		shader.stop();
		hitboxShader.start();
		hitboxShader.projectionMatrix.loadMatrix(projectionMatrix);
		hitboxShader.stop();
	}

	public void render(Map<TexturedModel, List<RenderableObject>> entities) {
		for (TexturedModel model : entities.keySet()) {
			prepareTexturedModel(model);
			List<RenderableObject> batch = entities.get(model);
			for (RenderableObject renderableObject : batch) {
				prepareInstance(renderableObject);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(),
						GL11.GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}
	}

	public void renderHitbox(Map<TexturedModel, List<RenderableObject>> entities) {
		prepareHitboxModel(Configs.sphereTexturedModel);
		for (TexturedModel model : entities.keySet()) {
			List<RenderableObject> batch = entities.get(model);
			for (RenderableObject renderableObject : batch) {
				prepareInstanceHitbox(renderableObject);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(),
						GL11.GL_UNSIGNED_INT, 0);
			}
		}
		unbindTexturedModel();
	}

	private void prepareTexturedModel(TexturedModel model) {
		Model rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		ModelTexture texture = model.getTexture();
		shader.shineDamper.loadFloat(texture.getShineDamper());
		shader.reflectivity.loadFloat(texture.getReflectivity());
		shader.currentTime.loadInteger((int) (DisplayManager.getCurrentTime() % 333));

		if (model == Configs.antTexturedModel) {
			shader.animated.loadBoolean(true);
			shader.animationDuration.loadInteger(333);
		} else {
			shader.animated.loadBoolean(false);
		}
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
	}

	private void prepareHitboxModel(TexturedModel model) {
		Model rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
	}

	private void unbindTexturedModel() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	private void prepareInstance(RenderableObject renderableObject) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(renderableObject.getPosition(), renderableObject.getRotX(), renderableObject.getRotY(), renderableObject.getRotZ(), renderableObject.getScale());
		shader.transformationMatrix.loadMatrix(transformationMatrix);
	}

	private void prepareInstanceHitbox(RenderableObject renderableObject) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(renderableObject.getPosition(), renderableObject.getRotX(), renderableObject.getRotY(), renderableObject.getRotZ(), 1);
		hitboxShader.transformationMatrix.loadMatrix(transformationMatrix);
		hitboxShader.radius.loadFloat(renderableObject.getBoundingSphereRadius());
		hitboxShader.positionOffset.loadVec3(renderableObject.getBoundingSphereOffset());
	}

}
