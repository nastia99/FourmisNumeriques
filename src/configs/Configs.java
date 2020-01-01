package configs;

import engineTester.MainGameLoop;
import openGL.models.TexturedModel;
import openGL.renderEngine.OBJLoader;
import openGL.textures.ModelTexture;
import org.lwjgl.util.vector.Vector3f;

public class Configs {

    private static final String ANT_MODEL = "models/ant";
    private static final String ANT_TEXTURE = "textures/ant";

    private static final String FOOD_MODEL = "models/sphere";
    private static final String FOOD_TEXTURE = "textures/red";

    private static final String SPHERE_MODEL = "models/sphere";
    private static final String SPHERE_TEXTURE = "textures/red";

    public static final Vector3f SKY_COLOR = new Vector3f(0.49f, 0.89f, 0.98f);
    public static final float FOG_DENSITY = 0.043f;
    public static final float FOG_GRADIENT = 30f;

    public static ModelTexture chunkModelTexture;
    public static TexturedModel antTexturedModel;
    public static TexturedModel sphereTexturedModel;
    public static TexturedModel foodTexturedModel;

    public static final int ACTION_DURATION = 500;
    public static final int ANT_ANIMATION_DURATION = ACTION_DURATION/3;

    public static void init() {
        chunkModelTexture = new ModelTexture(0);
        antTexturedModel = new TexturedModel(OBJLoader.loadObjModel(ANT_MODEL, MainGameLoop.loader), new ModelTexture(MainGameLoop.loader.loadTexture(ANT_TEXTURE)));
        sphereTexturedModel = new TexturedModel(OBJLoader.loadObjModel(SPHERE_MODEL, MainGameLoop.loader), new ModelTexture(MainGameLoop.loader.loadTexture(SPHERE_TEXTURE)));
        foodTexturedModel = new TexturedModel(OBJLoader.loadObjModel(FOOD_MODEL, MainGameLoop.loader), new ModelTexture(MainGameLoop.loader.loadTexture(FOOD_TEXTURE)));
    }
}
