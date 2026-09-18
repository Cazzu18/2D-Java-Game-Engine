package components;

import jade.KeyListener;
import jade.Window;

import static org.lwjgl.glfw.GLFW.*;

public class GizmoSystem extends Component {
    private Spritesheet gizmos;
    private int usingGizmo = 0; //0 is tranlate gizmo and 1 is scale gizmo

    public GizmoSystem(Spritesheet gizmoSprites) {
        this.gizmos = gizmoSprites;
    }

    @Override
    public void start(){
        gameObject.addComponent(new TranslateGizmo(gizmos.getSprite(1), Window.getImGuiLayer().getPropertiesWindow())); //this Window.get().getImGuiLayer().getPropertiesWindow() is horrible. We will update when we implement the Event system
        gameObject.addComponent(new ScaleGizmo(gizmos.getSprite(2), Window.getImGuiLayer().getPropertiesWindow()));
    }

    @Override
    public void update(float dt){
        if(usingGizmo == 0){
            gameObject.getComponent(TranslateGizmo.class).setUsing();
            gameObject.getComponent(ScaleGizmo.class).setNotUsing();
        } else if (usingGizmo == 1){
            gameObject.getComponent(TranslateGizmo.class).setNotUsing();
            gameObject.getComponent(ScaleGizmo.class).setUsing();
        }

        //translate and scale gizmo switching
        if(KeyListener.isKeyPressed(GLFW_KEY_T)){
            usingGizmo = 0;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_S)){
            usingGizmo = 1;
        }
    }
}
