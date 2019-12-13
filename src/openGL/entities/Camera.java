package openGL.entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

	private float distanceFromFocus = 5;
	private float angleAroundFocus = 90;
	private static final float Y_OFFSET = .3f;

	private Vector3f position = new Vector3f(5,5,25);
	private float pitch = 10;
	private float yaw ;
	private float roll;

	private RenderableObject focus;
	
	public Camera(RenderableObject focus) {
		this.focus = focus;
	}
	
	public void update(){
		calculateAngleAroundFocus();
		calculatePitch();
		calculateZoom();
		float hDist = calculateHorizontalDistance();
		float vDist = calculateVerticalDistance();
		calculateCameraPosition(hDist, vDist);
		this.yaw = 180 - (focus.getRotY() + angleAroundFocus);
	}

	public RenderableObject getFocus() {
		return focus;
	}

	public void setFocus(RenderableObject focus) {
		angleAroundFocus = 90;
		this.focus = focus;
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
	
	private void calculateZoom() {
		float zoomLevel = Mouse.getDWheel() * 0.01f;
		distanceFromFocus -= zoomLevel;
		if (distanceFromFocus < 0.5f)
			distanceFromFocus = 0.5f;
		if (distanceFromFocus > 50f)
			distanceFromFocus = 50f;
	}

	private void calculatePitch() {
		if (Mouse.isButtonDown(1)) {
			float pitchChange = Mouse.getDY() * 0.1f;
			pitch -= pitchChange;
			if (pitch < 0)
				pitch = 0;
			if (pitch > 90)
				pitch = 90;
		}
	}

	private void calculateAngleAroundFocus() {
		if (Mouse.isButtonDown(1)) {
			float angleChange = Mouse.getDX() * 0.3f;
			angleAroundFocus -= angleChange;
			angleAroundFocus %= 360;
		}
	}

	private float calculateHorizontalDistance() {
		return (float) (distanceFromFocus * Math.cos(Math.toRadians(pitch)));
	}

	private float calculateVerticalDistance() {
		return (float) (distanceFromFocus * Math.sin(Math.toRadians(pitch)));
	}

	private void calculateCameraPosition(float hDist, float vDist) {
		position.y = focus.getPosition().y + vDist + Y_OFFSET;
		float xOffset = (float) (hDist * Math.sin(Math.toRadians(focus.getRotY() + angleAroundFocus)));
		float zOffset = (float) (hDist * Math.cos(Math.toRadians(focus.getRotY() + angleAroundFocus)));
		position.x = focus.getPosition().x - xOffset;
		position.z = focus.getPosition().z - zOffset;
	}
}
