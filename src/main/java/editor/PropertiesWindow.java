package editor;

import imgui.ImGui;
import jade.GameObject;
import jade.MouseListener;
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

    public void update(float dt, Scene currentScene){
        debounceTime -= dt;

        if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounceTime < 0){
            int x = (int)MouseListener.getScreenX();
            int y = (int)MouseListener.getScreenY();
            int gameObjectId = pickingTexture.readPixel(x,y);
            this.debounceTime = 0.2f;

            //TODO: Verify the PickingTexture GetWidth and GetHeight function!
            if(!(x < 0 || x > pickingTexture.GetWidth() || y < 0 || y > pickingTexture.GetHeight())){
                activeGameObject = currentScene.getGameObject(gameObjectId); // pickingTexture.readPixel RETURNS THE ID OF A GAMEOBJECT
            }
        }
    }

    public void imgui(){
        if (activeGameObject!=null){
            ImGui.begin("Properties");
            activeGameObject.imgui();// the game object we are inspecting
            ImGui.end();
        }
    }

    public GameObject getActiveGameObject(){
        return this.activeGameObject;
    }
}
