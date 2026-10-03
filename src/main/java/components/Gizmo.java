package components;

import editor.PropertiesWindow;
import jade.*;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.JMath;

import static org.lwjgl.glfw.GLFW.*;

public class Gizmo extends Component {
    private Vector4f xAxisColor = new Vector4f(1,0.3f,0.3f,1);
    private Vector4f xAxisColorHover = new Vector4f(1, 0, 0, 1);
    private Vector4f yAxisColor = new Vector4f(0.3f,1,0.3f,1);
    private Vector4f yAxisColorHover = new Vector4f(0, 1, 0, 1);

    private GameObject xAxisObject;
    private GameObject yAxisObject;
    private SpriteRenderer xAxisSprite;
    private SpriteRenderer yAxisSprite;
    protected GameObject activeGameObject = null;

    private Vector2f xAxisOffset = new Vector2f(24f / 80f, -6f / 80f);
    private Vector2f yAxisOffset = new Vector2f(-7f / 80f, 21f / 80f);

    private float gizmoWidth = 16 / 80f; //width of gizmo in world units
    private float gizmoHeight = 48 / 80f;

    protected boolean xAxisActive = false;
    protected boolean yAxisActive = false;

    private boolean using = false;

    private PropertiesWindow propertiesWindow;

    public Gizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow){
        this.xAxisObject = Prefabs.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight);
        this.yAxisObject = Prefabs.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight);
        this.xAxisSprite = xAxisObject.getComponent(SpriteRenderer.class);
        this.yAxisSprite = yAxisObject.getComponent(SpriteRenderer.class);
        this.propertiesWindow = propertiesWindow;

        this.xAxisObject.addComponent(new NonPickable());
        this.yAxisObject.addComponent(new NonPickable());

        Window.getScene().addGameObjectToScene(this.xAxisObject);
        Window.getScene().addGameObjectToScene(this.yAxisObject);
    }

    @Override
    public void start(){
        this.xAxisObject.transform.rotation = 90;
        this.yAxisObject.transform.rotation = 180;
        this.xAxisObject.transform.zIndex = 200;
        this.yAxisObject.transform.zIndex = 200;
        this.xAxisObject.setNoSerialize();
        this.yAxisObject.setNoSerialize();
    }

    @Override
    public void update(float dt){
        if(using) {
            this.setInactive();
        }
    }

    @Override
    public void editorUpdate(float dt){

        if(!using){
            return;
        }

        this.activeGameObject = this.propertiesWindow.getActiveGameObject();
        if(this.activeGameObject != null){
            this.setActive();

            //TODO: Move this into its own KeyEditorBinding component class
            if(KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL)
            && KeyListener.isKeyBeginPress(GLFW_KEY_D)){
                GameObject newObj = this.activeGameObject.copy();
                Window.getScene().addGameObjectToScene(newObj);
                newObj.transform.position.add(0.1f, 0.1f);
                this.propertiesWindow.setActiveGameObject(newObj);
                return;
            } else if(KeyListener.isKeyBeginPress(GLFW_KEY_DELETE)){
                activeGameObject.destroy();
                this.setInactive();
                this.propertiesWindow.setActiveGameObject(null);
                return;
            }
        } else {
            this.setInactive();
            return;
        }

        boolean xAxisHot = checkXHoverState();
        boolean yAxisHot = checkYHoverState();

        if((xAxisHot || xAxisActive) && MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)){
            xAxisActive = true;
            yAxisActive = false;
        } else if((yAxisHot || yAxisActive) && MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)){
            yAxisActive = true;
            xAxisActive = false;
        } else {
            xAxisActive = false;
            yAxisActive = false;
        }

        if(this.activeGameObject != null){
            this.xAxisObject.transform.position.set(this.activeGameObject.transform.position);
            this.yAxisObject.transform.position.set(this.activeGameObject.transform.position);
            this.xAxisObject.transform.position.add(this.xAxisOffset);
            this.yAxisObject.transform.position.add(this.yAxisOffset);
        }


    }

    private void setActive(){
        this.xAxisSprite.setColor(new JMath().Vector4fToInt32(xAxisColor));
        this.yAxisSprite.setColor(new JMath().Vector4fToInt32(yAxisColor));
    }

    private void setInactive(){
        this.activeGameObject = null;
        this.xAxisSprite.setColor(new JMath().Vector4fToInt32(new Vector4f(0, 0, 0, 0)));
        this.yAxisSprite.setColor(new JMath().Vector4fToInt32(new Vector4f(0, 0, 0, 0)));
    }

    private boolean checkXHoverState(){
        Vector2f mousePos = MouseListener.getWorld();

        //checking if mouse hovering gizmos(box detection)
        if(mousePos.x <= xAxisObject.transform.position.x + (gizmoHeight / 2.0f) &&
                mousePos.x >= xAxisObject.transform.position.x - (gizmoWidth / 2.0f) &&
                mousePos.y >= xAxisObject.transform.position.y - (gizmoHeight / 2.0f) &&
                mousePos.y <= xAxisObject.transform.position.y + (gizmoWidth / 2.0f))
        {
            xAxisSprite.setColor(new JMath().Vector4fToInt32(xAxisColorHover));
            return true;
        }

        xAxisSprite.setColor(new JMath().Vector4fToInt32(xAxisColor));
        return false;
    }

    private boolean checkYHoverState(){
        Vector2f mousePos = MouseListener.getWorld();

        //checking if mouse hovering gizmos(box detection)
        if(mousePos.x <= yAxisObject.transform.position.x + (gizmoWidth / 2.0f) &&
                mousePos.x >= yAxisObject.transform.position.x - (gizmoWidth / 2.0f) &&
                mousePos.y <= yAxisObject.transform.position.y + (gizmoHeight / 2.0f) &&
                mousePos.y >= yAxisObject.transform.position.y - (gizmoHeight / 2.0f))
        {
            yAxisSprite.setColor(new JMath().Vector4fToInt32(yAxisColorHover));
            return true;
        }

        yAxisSprite.setColor(new JMath().Vector4fToInt32(yAxisColor));
        return false;
    }

    public void setUsing(){
        this.using = true;
    }

    public void setNotUsing(){
        this.using = false;
        this.setInactive();
    }
}
