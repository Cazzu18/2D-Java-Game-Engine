package editor;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import jade.GameObject;
import jade.Window;

import java.util.List;

public class SceneHierarchyWindow {

    private static String payloadDragDropType = "SceneHierarchy";

    public void imgui(){
        ImGui.begin("Scene Hierarchy");

        List<GameObject> gameObjects = Window.getScene().getGameObjects();
        int index = 0;

        for(GameObject go : gameObjects){
            if(!go.doSerialization()){
                continue;
            }

            boolean treeNodeOpen = doTreeNode(go, index);
;
            if(treeNodeOpen){
                ImGui.treePop();
            }

            index++;

        }

        ImGui.end();

    }

    private boolean doTreeNode(GameObject go, int index){
        ImGui.pushID(index);//IDs reset when we end the window
        boolean treeNodeOpen = ImGui.treeNodeEx(go.name,
                ImGuiTreeNodeFlags.DefaultOpen |
                        ImGuiTreeNodeFlags.FramePadding |
                        ImGuiTreeNodeFlags.OpenOnArrow |
                        ImGuiTreeNodeFlags.SpanAvailWidth,
                go.name);

        ImGui.popID();

        if(ImGui.beginDragDropSource()){

            ImGui.setDragDropPayload(payloadDragDropType, go);
            ImGui.text(go.name); //whatever goes in between ends up as object
            //ImGui.button("This is a button");
            ImGui.endDragDropSource();
        }

        if(ImGui.beginDragDropTarget()){

            Object payloadObj = ImGui.acceptDragDropPayload(payloadDragDropType);
            if(payloadObj != null){
                if(payloadObj.getClass().isAssignableFrom(GameObject.class)){
                    GameObject playerGameObject = (GameObject) payloadObj;
                    //System.out.println("Payload accepted '" + go.name + "'");
                }
            }

            ImGui.endDragDropTarget();
        }

        return treeNodeOpen;
    }
}
