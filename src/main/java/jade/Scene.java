package jade;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;
import renderer.Renderer;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

    protected Renderer renderer = new Renderer();
    protected Camera camera;
    private boolean isRunning = false;
    protected List<GameObject> gameObjects = new ArrayList<>();
    protected GameObject activeGameObject = null;
    protected boolean levelLoaded = false;

    public Scene(){

    }

    public void init(){

    }

    //starting all the game objects
    public void start(){
        for(GameObject go: gameObjects){
            go.start();
            this.renderer.add(go);
        }
        isRunning = true;
    }

    public void addGameObjectToScene(GameObject go){
        if(!isRunning){
            gameObjects.add(go);
        } else {
            gameObjects.add(go);
            go.start();
            this.renderer.add(go);
        }
    }

    public abstract void update(float dt);


    public Camera camera(){
        return this.camera;
    }

    public void sceneImgui(){
        if (activeGameObject!=null){
            ImGui.begin("Inspector");
            activeGameObject.imgui();// the game object we are inspecting
            ImGui.end();

        }

        imgui();
    }

    //custom scene integrated imguis
    public void imgui(){

    }

    //save a file and exit(serialize)
    public void saveExit(){
        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Component.class, new ComponentDeserializer()).registerTypeAdapter(GameObject.class, new GameObjectDeserializer()).create();//.create() is important

        try{
            //possibly need an if to stop level.txt from growing after every relaunch
            FileWriter writer = new FileWriter("level.txt");
            writer.write(gson.toJson(this.gameObjects));
            writer.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    //loading a save(Deserialization)
    public void load(){
        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Component.class, new ComponentDeserializer()).registerTypeAdapter(GameObject.class, new GameObjectDeserializer()).create();//.create() is important
        String  inFile ="";
        try {
            inFile = new String(Files.readAllBytes(Paths.get("level.txt")));//TODO: check whether large files cause readAllBytes to truncate
        } catch(IOException e){
            e.printStackTrace();
        }

        if(!inFile.equals("")){
            GameObject[] objects = gson.fromJson(inFile, GameObject[].class);
            for(int i = 0; i < objects.length; i++){
                addGameObjectToScene(objects[i]);//adding the object to scene
            }
            this.levelLoaded = true;
        }

    }
}
