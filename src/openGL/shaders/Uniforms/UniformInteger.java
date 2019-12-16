package openGL.shaders.Uniforms;

import org.lwjgl.opengl.GL20;

public class UniformInteger extends Uniform{

	private int currentValue;

	public UniformInteger(String name){
		super(name);
	}
	
	public void loadInteger(int value){
		if(currentValue!=value){
			GL20.glUniform1i(super.getLocation(), value);
			currentValue = value;
		}
	}

}
