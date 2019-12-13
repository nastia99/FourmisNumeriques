package openGL.utils;
import openGL.entities.RenderableObject;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import openGL.entities.Camera;

import java.util.Comparator;
import java.util.List;

public class MousePicker {

    private static final float RAY_RANGE = 600;
    private static final float RAY_RANGE_SQR = RAY_RANGE * RAY_RANGE;

    private Vector3f currentRay = new Vector3f();
    private boolean canSelectEntity = true;

    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;
    private Camera camera;

    private List<RenderableObject> entities;

    public MousePicker(Camera cam, Matrix4f projection, List<RenderableObject> entities) {
        camera = cam;
        projectionMatrix = projection;
        viewMatrix = Maths.createViewMatrix(camera);
        this.entities = entities;
    }

    public Vector3f getCurrentRay() {
        return currentRay;
    }

    public void update() {
        viewMatrix = Maths.createViewMatrix(camera);
        currentRay = calculateMouseRay();
        if (!Mouse.isButtonDown(0))
            canSelectEntity = true;
    }

    public boolean canSelectEntity() {
        return canSelectEntity;
    }

    public RenderableObject getClickedEntity(RenderableObject exclude) {
        canSelectEntity = false;
        entities.sort(distanceFromCamComp);
        for (RenderableObject e : entities) {
            if (e == exclude) continue;
            if (e.getDistanceSquaredFromCamera(camera) < RAY_RANGE_SQR) {

                Vector3f oc = Vector3f.sub(camera.getPosition(), Vector3f.add(e.getPosition(), e.getBoundingSphereOffset(), null), null);
                float a = Vector3f.dot(currentRay, currentRay);
                float b = (float) (2.0 * Vector3f.dot(oc, currentRay));
                float c = Vector3f.dot(oc,oc) - e.getBoundingSphereRadius()*e.getBoundingSphereRadius();
                float discriminant = b*b - 4*a*c;
                if (discriminant>0)
                    return e;
            } else {
                break;
            }
        }
        return null;
    }

    private Vector3f calculateMouseRay() {
        float mouseX = Mouse.getX();
        float mouseY = Mouse.getY();
        Vector2f normalizedCoords = getNormalisedDeviceCoordinates(mouseX, mouseY);
        Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1.0f, 1.0f);
        Vector4f eyeCoords = toEyeCoords(clipCoords);
        Vector3f worldRay = toWorldCoords(eyeCoords);
        return worldRay;
    }

    private Vector3f toWorldCoords(Vector4f eyeCoords) {
        Matrix4f invertedView = Matrix4f.invert(viewMatrix, null);
        Vector4f rayWorld = Matrix4f.transform(invertedView, eyeCoords, null);
        Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
        mouseRay.normalise();
        return mouseRay;
    }

    private Vector4f toEyeCoords(Vector4f clipCoords) {
        Matrix4f invertedProjection = Matrix4f.invert(projectionMatrix, null);
        Vector4f eyeCoords = Matrix4f.transform(invertedProjection, clipCoords, null);
        return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
    }

    private Vector2f getNormalisedDeviceCoordinates(float mouseX, float mouseY) {
        float x = (2.0f * mouseX) / Display.getWidth() - 1f;
        float y = (2.0f * mouseY) / Display.getHeight() - 1f;
        return new Vector2f(x, y);
    }

    private Comparator<RenderableObject> distanceFromCamComp = new Comparator<RenderableObject>() {
        public int compare(RenderableObject e1, RenderableObject e2) {
            return e1.getDistanceSquaredFromCamera(camera).compareTo(e2.getDistanceSquaredFromCamera(camera));
        }};

}