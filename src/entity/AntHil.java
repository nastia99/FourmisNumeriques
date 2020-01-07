package entity;

import configs.Configs;
import openGL.entities.RenderableObject;
import org.lwjgl.util.vector.Vector3f;

public class AntHil extends RenderableObject {

    public AntHil(Vector3f position, float rotX, float rotY, float rotZ) {
        super(Configs.antHilTexturedModel, position, rotX, rotY, rotZ, .8f);
    }
}
