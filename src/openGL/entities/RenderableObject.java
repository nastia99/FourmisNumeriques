package openGL.entities;

import openGL.models.TexturedModel;

import org.lwjgl.util.vector.Vector3f;

public class RenderableObject {

	private TexturedModel model;
	private Vector3f position;
	private float rotX, rotY, rotZ;
	private float scale;
	private float boundingSphereRadius;
	private Vector3f boundingSphereOffset;

	private boolean canBeRendered = true;

	public RenderableObject(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		this.model = model;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		boundingSphereRadius = scale/2;
		this.boundingSphereOffset = new Vector3f(0, scale, 0);
	}

	public Vector3f getBoundingSphereOffset() {
		return boundingSphereOffset;
	}

	public void increasePosition(float dx, float dy, float dz) {
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
	}

	public void increaseRotation(float dx, float dy, float dz) {
		this.rotX += dx;
		this.rotY += dy;
		this.rotZ += dz;
	}

	public TexturedModel getModel() {
		return model;
	}

	public void setModel(TexturedModel model) {
		this.model = model;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public float getRotX() {
		return rotX;
	}

	public void setRotX(float rotX) {
		this.rotX = rotX;
	}

	public float getRotY() {
		return rotY;
	}

	public void setRotY(float rotY) {
		this.rotY = rotY;
	}

	public float getRotZ() {
		return rotZ;
	}

	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public float getBoundingSphereRadius() {
		return boundingSphereRadius;
	}

	public boolean canBeRendered() {
		return canBeRendered;
	}

	public void setCanBeRendered(boolean canBeRendered) {
		this.canBeRendered = canBeRendered;
	}

	public Float getDistanceSquaredFromCamera(Camera camera) {
		return Vector3f.sub(position, camera.getPosition(), null).lengthSquared();
	}

}
