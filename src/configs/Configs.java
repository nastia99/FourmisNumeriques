package configs;

import engineTester.MainGameLoop;
import openGL.models.TexturedModel;
import openGL.renderEngine.OBJLoader;
import openGL.textures.ModelTexture;

public class Configs {
    public static final String WORLD_TEXTURE = "textures/grass";

    public static final String ANT_MODEL = "models/tree";
    public static final String ANT_TEXTURE = "textures/tree";

    public static final String SPHERE_MODEL = "models/sphere";
    public static final String SPHERE_TEXTURE = "textures/image";

    public static TexturedModel antTexturedModel;
    public static ModelTexture chunkModelTexture;

    public static TexturedModel sphereTexturedModel;



    public static void init() {
        antTexturedModel = new TexturedModel(OBJLoader.loadObjModel(ANT_MODEL, MainGameLoop.loader), new ModelTexture(MainGameLoop.loader.loadTexture(ANT_TEXTURE)));
        chunkModelTexture = new ModelTexture(MainGameLoop.loader.loadTexture(WORLD_TEXTURE));

        sphereTexturedModel = new TexturedModel(OBJLoader.loadObjModel(SPHERE_MODEL, MainGameLoop.loader), new ModelTexture(MainGameLoop.loader.loadTexture(SPHERE_TEXTURE)));
    }

}
