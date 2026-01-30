package components;

import jade.GameObject;
import jade.MouseListener;
import jade.Window;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControls extends Component {
    GameObject holdingObject = null; //the object the mouse holding

    public void pickupObject(GameObject go) {
        this.holdingObject = go;
        Window.getScene().addGameObjectToScene(go);
    }

    public void place(){
        this.holdingObject = null;
    }

    @Override
    public void update(float dt){
        if(holdingObject != null){ //check if mouse controlls holding something

            //snap object position to mouse positions
            holdingObject.transform.position.x = MouseListener.getOrthoX() - 16;//subtract 16 so that centered on the mouse
            holdingObject.transform.position.y = MouseListener.getOrthoY() - 16;

            //if user presses left mouse button, place object
            if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)){
                place();
            }
        }
    }


}
