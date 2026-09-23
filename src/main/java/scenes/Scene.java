package scenes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Component;
import components.ComponentDeserializer;
import imgui.ImGui;
import jade.Camera;
import jade.GameObject;
import jade.GameObjectDeserializer;
import jade.Transform;
import renderer.Renderer;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public GameObject getGameObject(int gameObjectId){
        Optional<GameObject> result = this.gameObjects.stream()
                .filter(gameObject -> gameObject.getUid() == gameObjectId)
                .findFirst();

        return result.orElse(null); //orElse() If a value is present, returns the value, otherwise returns other
    }

    public abstract void update(float dt);
    public abstract void render();

    public Camera camera(){
        return this.camera;
    }

    //custom scene integrated imguis
    public void imgui(){

    }

    public GameObject createGameObject(String name){
        GameObject go = new GameObject(name);
        go.addComponent(new Transform());
        go.transform = go.getComponent(Transform.class);
        return go;
    }

    //save a file and exit(serialize)
    public void saveExit(){
        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Component.class, new ComponentDeserializer()).registerTypeAdapter(GameObject.class, new GameObjectDeserializer()).create();//.create() is important

        try{
            //possibly need an if to stop level.txt from growing after every relaunch
            FileWriter writer = new FileWriter("level.txt");
            List<GameObject> objsToSerialize = new ArrayList<>();
            for(GameObject obj: this.gameObjects){
                if(obj.doSerialization()){
                    objsToSerialize.add(obj);
                }
            }
            writer.write(gson.toJson(objsToSerialize));
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
            int maxGoId = -1;
            int maxCompID = -1;

            GameObject[] objects = gson.fromJson(inFile, GameObject[].class);
            for(int i = 0; i < objects.length; i++){
                addGameObjectToScene(objects[i]);//adding the object to scene

                for(Component c : objects[i].getAllComponents()){
                    if(c.getUid() > maxCompID){
                        maxCompID = c.getUid(); //maximum component id
                    }
                }

                if(objects[i].getUid() > maxGoId){
                    maxGoId = objects[i].getUid();
                }
            }

            maxGoId++;
            maxCompID++;
            GameObject.init(maxGoId);
            Component.init(maxCompID);

            this.levelLoaded = true;
        }

    }
}
