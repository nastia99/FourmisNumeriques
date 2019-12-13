package openGL.shaders.Uniforms;

import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector4f;

public class UniformVec4 extends Uniform {
	private float currentX;
	private float currentY;
	private float currentZ;
	private float currentW;

	public UniformVec4(String name) {
		super(name);
	}

	public void loadVec4(Vector4f vector) {
		loadVec4(vector.x, vector.y, vector.z, vector.w);
	}

	public void loadVec4(float x, float y, float z, float w) {
		if (x != currentX || y != currentY || z != currentZ || w != currentW) {
			this.currentX = x;
			this.currentY = y;
			this.currentZ = z;
			this.currentW = w;
			GL20.glUniform4f(super.getLocation(), x, y, z, w);
		}
	}

}
