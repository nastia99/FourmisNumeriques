package configs;

import engineTester.MainGameLoop;
import openGL.models.TexturedModel;
import openGL.renderEngine.OBJLoader;
import openGL.textures.ModelTexture;
import org.lwjgl.util.vector.Vector3f;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configs {

    /**
     * Constants regarding the rendering pipeline
     */
    private static final String ANT_MODEL = "models/ant";
    private static final String ANT_TEXTURE = "textures/ant";

    private static final String FOOD_MODEL = "models/graine";
    private static final String FOOD_TEXTURE = "textures/graine";

    private static final String ANTHIL_MODEL = "models/anthil";
    private static final String ANTHIL_TEXTURE = "textures/anthil";

    private static final String SPHERE_MODEL = "models/sphere";
    private static final String SPHERE_TEXTURE = "textures/red";

    public static final Vector3f SKY_COLOR = new Vector3f(0.49f, 0.89f, 0.98f);
    public static final float FOG_DENSITY = 0.043f;
    public static final float FOG_GRADIENT = 30f;

    public static ModelTexture chunkModelTexture;
    public static TexturedModel antTexturedModel;
    public static TexturedModel sphereTexturedModel;
    public static TexturedModel foodTexturedModel;
    public static TexturedModel antHilTexturedModel;

    public static final int ACTION_DURATION = 1000;
    public static final int ANT_ANIMATION_DURATION = ACTION_DURATION/3;


    /**
     * Parameters loaded from the config file, characterizing the simulation
     */
    public static boolean worldNeedRegeneration;
    public static int nbAnts;
    public static float generationConservationRatio;
    public static float mutationRate;
    public static int worldSeed;
    public static boolean renderSimulation;
    public static int generationTime;

    public static void initModels() {
        chunkModelTexture = new ModelTexture(0);
        antTexturedModel = new TexturedModel(OBJLoader.loadObjModel(ANT_MODEL, MainGameLoop.loader), new ModelTexture(MainGameLoop.loader.loadTexture(ANT_TEXTURE)));
        sphereTexturedModel = new TexturedModel(OBJLoader.loadObjModel(SPHERE_MODEL, MainGameLoop.loader), new ModelTexture(MainGameLoop.loader.loadTexture(SPHERE_TEXTURE)));
        foodTexturedModel = new TexturedModel(OBJLoader.loadObjModel(FOOD_MODEL, MainGameLoop.loader), new ModelTexture(MainGameLoop.loader.loadTexture(FOOD_TEXTURE)));
        antHilTexturedModel = new TexturedModel(OBJLoader.loadObjModel(ANTHIL_MODEL, MainGameLoop.loader), new ModelTexture(MainGameLoop.loader.loadTexture(ANTHIL_TEXTURE)));

    }

    public static void init(String propertieFilePath) {
        try (InputStream inputStream = new FileInputStream(propertieFilePath)) {

            Properties prop = new Properties();
            prop.load(inputStream);

            worldNeedRegeneration = Boolean.parseBoolean(prop.getProperty("regenerateWorld"));
            nbAnts = Integer.parseInt(prop.getProperty("nbAnts"));
            generationConservationRatio = Float.parseFloat(prop.getProperty("generationConservationRatio"));
            mutationRate = Float.parseFloat(prop.getProperty("mutationRate"));
            worldSeed = Integer.parseInt(prop.getProperty("worldSeed"));
            renderSimulation = Boolean.parseBoolean(prop.getProperty("renderSimulation"));
            generationTime = Integer.parseInt(prop.getProperty("generationTime"));
        } catch (Exception e) {
            System.err.println("Error : " + e.getMessage());
        }
    }
}
