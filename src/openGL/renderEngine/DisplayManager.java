package openGL.renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.*;

public class DisplayManager {
	
	private static final int WIDTH = 1920;
	private static final int HEIGHT = 1080;
	private static final int FPS_CAP = 120;

	private static long lastFrameTime;
	private static float delta; //Frame time in seconds

	private static long intervalTimer;
	
	public static void createDisplay(){		
		ContextAttribs attribs = new ContextAttribs(3,2)
		.withForwardCompatible(true)
		.withProfileCore(true);
		
		try {
			Display.setDisplayMode(new DisplayMode(WIDTH,HEIGHT));
			Display.create(new PixelFormat().withSamples(8), attribs);
			Display.setTitle("Projet APO, Demo moteur graphique");
			GL11.glEnable(GL13.GL_MULTISAMPLE);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		GL11.glViewport(0,0, WIDTH, HEIGHT);
		lastFrameTime = getCurrentTime();
		intervalTimer = getCurrentTime();
	}
	
	public static void updateDisplay(){
		Display.sync(FPS_CAP);
		Display.update();
		long currentTime = getCurrentTime();
		delta = (float)(currentTime - lastFrameTime) / 1000.0f;
		lastFrameTime = getCurrentTime();
		Display.setTitle("Projet APO, Demo moteur graphique       FPS : " + (int)(1f/delta));
	}

	public static float getFrameTimeSeconds() {
		return delta;
	}

	public static void closeDisplay(){
		Display.destroy();
	}

	public static long getCurrentTime() {
		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}

	public static boolean intervalHasPassed(int ms) {
		if (getCurrentTime() - intervalTimer > ms) {
			intervalTimer = getCurrentTime();
			return true;
		}
		return false;
	}

	public static long timeSinceLastInterval() {
		return getCurrentTime() - intervalTimer;
	}
}
