package jade;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {

    private Matrix4f projectionMatrix, viewMatrix; //4f means 4*4
    public Vector2f position; //2*2 matrix. this is the position of the camera in the world

    public Camera(Vector2f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        adjustProjection();
    }

    //projection matrix sets window size
    public void adjustProjection(){
        projectionMatrix.identity(); //identity matrix(1 diagonal)
        projectionMatrix.ortho(0.0f, 32.0f * 40.0f, 0.0f, 32.0f * 21.0f, 0.0f, 100.0f); //40 grid tiles wide that are 32*32 each and 21 tiles tall. left, right, bottom, top, zNear, zFar

    }

    //view matrix moves the world so that the camera becomes the origin
    public Matrix4f getViewMatrix() {
        Vector3f cameraFront = new Vector3f(0.0f,0.0f,-1.0f); //Camera is looking at -1 in the z(like me looking at my screen if i'm the camera)
        Vector3f cameraUp = new Vector3f(0.0f,1.0f,0.0f);//"up" on your screen is the positive Y axis.
        this.viewMatrix.identity();
        viewMatrix.lookAt(new Vector3f(position.x, position.y, 20.0f),
                                            cameraFront.add(position.x, position.y, 0.0f),
                                            cameraUp); //the first parameter specifies where the camera is located(allow movement on X and Y and Z is fixed 20 units away from scene), the second specifies where the camera is looking cameraFront starts as (0, 0, -1), then we add camera's position so it looks at (cameraX, cameraY, -1)
        return this.viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

}
