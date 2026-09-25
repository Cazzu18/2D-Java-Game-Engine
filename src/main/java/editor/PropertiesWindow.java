package editor;

import components.NonPickable;
import imgui.ImGui;
import jade.GameObject;
import jade.MouseListener;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;
import physics2d.components.Rigidbody2D;
import renderer.PickingTexture;
import scenes.Scene;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesWindow {
    private GameObject activeGameObject = null;
    private PickingTexture pickingTexture;

    private float debounceTime = 0.2f; //0.2 seconds

    public PropertiesWindow(PickingTexture pickingTexture) {
        this.pickingTexture = pickingTexture;
    }

    public void update(float dt, Scene currentScene, boolean canPick){
        debounceTime -= dt;

        if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounceTime < 0){
            int x = (int)MouseListener.getScreenX();
            int y = (int)MouseListener.getScreenY();

            if(x < 0 || x >= pickingTexture.GetWidth() || y < 0 || y >= pickingTexture.GetHeight()){
                return;
            }

            int gameObjectId = pickingTexture.readPixel(x,y);

            GameObject pickedObj = currentScene.getGameObject(gameObjectId);

            if(pickedObj != null && pickedObj.getComponent(NonPickable.class) == null){
                activeGameObject = pickedObj; // pickingTexture.readPixel RETURNS THE ID OF A GAMEOBJECT
            } else if(pickedObj == null && !MouseListener.isDragging()){
                activeGameObject = null;
            }
            this.debounceTime = 0.2f;
        }
    }

    public void imgui(){
        if (activeGameObject!=null){
            ImGui.begin("Properties");

            if(ImGui.beginPopupContextWindow("ComponentAdder")){
                if(ImGui.menuItem("Add RigidBody")){
                    //GO doesn't already have a RigidBody
                    if(activeGameObject.getComponent(Rigidbody2D.class) == null){
                        activeGameObject.addComponent(new Rigidbody2D());
                    }
                }

                if(ImGui.menuItem("Add Box Collider")){
                    if(activeGameObject.getComponent(Box2DCollider.class) == null &&
                            activeGameObject.getComponent(CircleCollider.class) == null){
                        activeGameObject.addComponent(new Box2DCollider());
                    }
                }

                if(ImGui.menuItem("Add Circle Collider")){
                    if(activeGameObject.getComponent(CircleCollider.class) == null &&
                            activeGameObject.getComponent(Box2DCollider.class) == null){
                        activeGameObject.addComponent(new CircleCollider());
                    }
                }

                ImGui.endPopup();
            }

            activeGameObject.imgui();// the game object we are inspecting
            ImGui.end();
        }
    }

    public GameObject getActiveGameObject(){
        return this.activeGameObject;
    }
}
