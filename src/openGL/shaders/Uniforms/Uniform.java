package openGL.shaders.Uniforms;

import org.lwjgl.opengl.GL20;

public abstract class Uniform {
	
	private static final int NOT_FOUND = -1;
	
	private String name;
	private int location;
	
	protected Uniform(String name){
		this.name = name;
	}
	
	public void storeUniformLocation(int programID){
		location = GL20.glGetUniformLocation(programID, name);
		if(location == NOT_FOUND){
			System.err.println("Uniform \"" + name + "\" non trouvée pour le shader : "+programID);
		}
	}
	
	protected int getLocation(){
		return location;
	}

}
