package components;

import jade.Camera;
import jade.KeyListener;
import jade.MouseListener;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class EditorCamera extends Component{
    /*
    * 16.67 milliseconds (ms) per frame
    *
    * Why?
    *
    * one second contains 1,000 milliseconds
    * Dividing 1,000 ms by 60 frames give roughly 16.66 ms PER FRAME
    * */

    private float dragDebounce = 0.032f; //after 0.032ms(2 frames because 0.016 * 2 = 0.032ms) drag initiated
    private Camera levelEditorCamera;
    private Vector2f clickOrigin;
    private boolean reset = false;

    private float lerpTime = 0.0f;
    private float dragSensitivity = 30.0f;
    private float scrollSensitivity = 0.1f;

    public EditorCamera(Camera levelEditorCamera) {
        this.levelEditorCamera = levelEditorCamera;
        this.clickOrigin = new Vector2f();
    }

    @Override
    public void update(float dt){
        if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE) && dragDebounce > 0){
            this.clickOrigin = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY()); //mouse position in world coordinates
            dragDebounce -= dt;
            return;
        } else if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)){
            // if we get to this if block, then dragDebounce is lt or gt than 0 meaning we arrived

            Vector2f mousePos =  new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
            //getting delta distance between where we first clicked and where we are in the next frame
            Vector2f delta = new Vector2f(mousePos).sub(clickOrigin);

            levelEditorCamera.position.sub(delta.mul(dt).mul(dragSensitivity));

            /*
            * To calculate a value between two other values,
            * using linear interpolation, a certain factor,
            * often called "step", must be used.
            * The value has to be between value 1 (v0) and value 2 (v1).
            * If "step" is 0.0 the interpolated value is equal to v0 and if step is 1.0,
            * it's equal to v1. So the formula is as follows:
            *
            * v0 * (1.0 - step) + v1 * step
            *
            * or computationally more efficiently,
            * v0 + (v1 - v0) * step
            * */

            /*
            * Given two points on a graph,
            * a value (xi, yi) can be calculated like:
            *
            * yi = y0 * (1.0 - ((xi - x0)/ (x1 - x0))) + y1 * ((xi-x0)/(x1-x0))
            *
            * or
            *
            * yi = y0 + (y1 - y0) * ((xi - x0)/(x1- x0))
            *
            * where ((xi - x0)/(x1- x0)) is the STEP value
            * */
            this.clickOrigin.lerp(mousePos, dt);

        }

        if(dragDebounce <= 0.0f && !MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)){
            dragDebounce = 0.032f; //0.1f
        }

        if(MouseListener.getScrollY() != 0.0f){
            float addValue = (float)Math.pow(Math.abs(MouseListener.getScrollY() * scrollSensitivity), 1/levelEditorCamera.getZoom());

            addValue *= -Math.signum(MouseListener.getScrollY());//getting sign of the scroll
            levelEditorCamera.addZoom(addValue);
        }

        //TODO: decide on a better return to home hotkey
        if(KeyListener.isKeyPressed(GLFW_KEY_E)){
            reset = true;
        }

        if(reset){
            levelEditorCamera.position.lerp(new Vector2f(), lerpTime);

            levelEditorCamera.setZoom(this.levelEditorCamera.getZoom() + ((1.0f - levelEditorCamera.getZoom()) * lerpTime));

            this.lerpTime += 0.1f * dt; //adding a portion of dt to lerp time so it(step) gets bigger everytime

            //clamping if we get close
            if(Math.abs(levelEditorCamera.position.x) <= 5.0f
                    && Math.abs(levelEditorCamera.position.y) <= 5.0f){

                this.lerpTime = 0.0f;
                levelEditorCamera.position.set(0f, 0f);

                this.levelEditorCamera.setZoom(1.0f);
                reset = false;

            }
        }
    }
}
