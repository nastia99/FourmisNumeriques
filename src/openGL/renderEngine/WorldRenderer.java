package openGL.renderEngine;

import java.util.List;

import openGL.models.Model;

import openGL.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import openGL.shaders.WorldShader;
import openGL.world.Chunk;
import openGL.textures.ModelTexture;
import openGL.utils.Maths;

public class WorldRenderer {

	private WorldShader shader;

	public WorldRenderer(WorldShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.start();
		shader.projectionMatrix.loadMatrix(projectionMatrix);
		shader.stop();
	}

	public void render(World world) {
		for (Chunk chunk : world.getChunks()) {
			prepareTerrain(chunk);
			loadModelMatrix(chunk);
			GL11.glDrawElements(GL11.GL_TRIANGLES, chunk.getModel().getVertexCount(),
					GL11.GL_UNSIGNED_INT, 0);
			unbindTexturedModel();
		}
	}

	private void prepareTerrain(Chunk chunk) {
		Model rawModel = chunk.getModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		ModelTexture texture = chunk.getTexture();
		shader.shineDamper.loadFloat(texture.getShineDamper());
		shader.reflectivity.loadFloat(texture.getReflectivity());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getID());
	}

	private void unbindTexturedModel() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	private void loadModelMatrix(Chunk chunk) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(
				new Vector3f(chunk.getX(), 0, chunk.getZ()), 0, 0, 0, 1);
		shader.transformationMatrix.loadMatrix(transformationMatrix);
	}

}
