package openGL.shaders;

import openGL.shaders.Uniforms.UniformFloat;
import openGL.shaders.Uniforms.UniformMatrix;
import openGL.shaders.Uniforms.UniformVec3;
import org.lwjgl.util.vector.Matrix4f;

import openGL.utils.Maths;
import openGL.entities.Camera;
import openGL.entities.Light;

public class WorldShader extends ShaderProgram{
	
	
	private static final String VERTEX_SHADER = "res/shaders/world/vertexShader.glsl";
	private static final String FRAGMENT_SHADER = "res/shaders/world/fragmentShader.glsl";

	public UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");
	public UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
	public UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");
	public UniformVec3 lightPosition = new UniformVec3("lightPosition");
	public UniformVec3 lightColour = new UniformVec3("lightColour");
	public UniformFloat shineDamper = new UniformFloat("shineDamper");
	public UniformFloat reflectivity = new UniformFloat("reflectivity");

	/**
	 * Create a new WorldShader and store all of the Uniforms variables
	 */
	public WorldShader() {
		super(VERTEX_SHADER, FRAGMENT_SHADER);
		super.storeAllUniformLocations(transformationMatrix, projectionMatrix, viewMatrix, lightPosition, lightColour, shineDamper, reflectivity);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoordinates");
		super.bindAttribute(2, "normal");
	}
}
