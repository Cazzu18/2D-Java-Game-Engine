package components;

import jade.GameObject;
import jade.KeyListener;
import jade.MouseListener;
import jade.Window;
import org.joml.Vector4f;
import util.JMath;
import util.Settings;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControls extends Component {
    GameObject holdingObject = null; //the object the mouse holding
    private float debounceTime = 0.05f;
    private float debounce = debounceTime;

    public void pickupObject(GameObject go) {
        if(this.holdingObject != null) {
            this.holdingObject.destroy();
        }
        this.holdingObject = go;
        this.holdingObject.getComponent(SpriteRenderer.class).setColor(JMath.Vector4fToInt32(new Vector4f(0.8f, 0.8f, 0.8f, 0.6f)));
        this.holdingObject.addComponent(new NonPickable());//prevent immediate selection of obj that is picked up
        Window.getScene().addGameObjectToScene(go);
    }

    public void place(){
        GameObject newObj = this.holdingObject.copy();
        newObj.getComponent(SpriteRenderer.class).setColor(JMath.Vector4fToInt32(new Vector4f(1, 1, 1, 1)));
        newObj.removeComponent(NonPickable.class);//enable selection once obj on scene
        Window.getScene().addGameObjectToScene(newObj);

        //this.holdingObject = null;

    }

    @Override
    public void editorUpdate(float dt){
        //System.out.println(MouseListener.getWorldY());
        debounce -= dt;
        if(holdingObject != null && debounce <= 0){ //check if mouse controlls holding something

            //snap object position to mouse positions
            holdingObject.transform.position.x = MouseListener.getWorldX();//subtract 0.125f so that centered on the mouse
            holdingObject.transform.position.y = MouseListener.getWorldY() ;
            holdingObject.transform.position.x = ((int)Math.floor(holdingObject.transform.position.x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH) + Settings.GRID_WIDTH / 2.0f;
            holdingObject.transform.position.y = ((int)Math.floor(holdingObject.transform.position.y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT) + Settings.GRID_HEIGHT / 2.0f;

            //if user presses left mouse button, place object
            if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)){
                place();
                debounce = debounceTime;
            }

            if(KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)){
                holdingObject.destroy();
                holdingObject = null;
            }
        }
    }


}
