package scenes;

import components.*;
import imgui.ImGui;
import imgui.ImVec2;
import jade.Camera;
import jade.GameObject;
import jade.Prefabs;
import jade.Transform;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import renderer.DebugDraw;
import util.AssetPool;

public class LevelEditorScene extends Scene {
    private GameObject obj1;
    private Spritesheet sprites;
    SpriteRenderer obj2SpriteRenderer;

    MouseControls mouseControls = new MouseControls();

    public LevelEditorScene() {
    }

    @Override
    public void init() {
        loadResources();
        this.camera = new Camera(new Vector2f(-250, 0));
        sprites  = AssetPool.getSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png");


        if(levelLoaded){
            this.activeGameObject = gameObjects.get(0);
            this.activeGameObject.addComponent(new RigidBody());
            return;
        }
        GameObject obj2 = new GameObject("Object 2", new Transform(new Vector2f(400, 400), new Vector2f(256, 256)), -1);
        obj2SpriteRenderer = new SpriteRenderer();
        obj2SpriteRenderer.setColor(new Vector4f(1, 0, 0, 1));
        obj2.addComponent(obj2SpriteRenderer);
        obj2.addComponent(new RigidBody());
        this.addGameObjectToScene(obj2);

        GameObject obj3 = new GameObject("Object 2", new Transform(new Vector2f(200, 400), new Vector2f(256, 256)), 2);
        SpriteRenderer obj3SpriteRenderer = new SpriteRenderer();
        Sprite obj3Sprite = new Sprite();
        obj3Sprite.setTexture(AssetPool.getTexture("assets/images/blendImage2.png"));
        obj3SpriteRenderer.setSprite(obj3Sprite);
        obj3.addComponent(obj3SpriteRenderer);
        this.addGameObjectToScene(obj3);



//        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Component.class, new ComponentDeserializer()).registerTypeAdapter(GameObject.class, new GameObjectDeserializer()).create();//.create() is important
//        String serialized = gson.toJson(obj2);
//        System.out.println(serialized);
//        GameObject obj = gson.fromJson(serialized, GameObject.class);
//        System.out.println(obj);



        /*
        int xOffset = 10;//padding of 10
        int yOffset = 10;

        float totalWidth = (float)(600 - xOffset * 2);
        float totalHeight = (float)(300 - xOffset * 2);

        float sizeX = totalWidth/100.0f;
        float sizeY = totalHeight/100.0f;


        Note: creating 10,00 objects(100 * 100)
        for(int x=0; x < 100; x++){
            for(int y=0; y < 100; y++){
                float xPos = xOffset + (x * sizeX);
                float yPos = yOffset + (y * sizeY);
                GameObject go = new GameObject("Obj" + x + "" + y, new Transform(new Vector2f(xPos, yPos), new Vector2f(sizeX, sizeY)));
                go.addComponent(new SpriteRenderer(new Vector4f(xPos / totalWidth, yPos/totalHeight, 1, 1)));// 1 and 1 is the color, which is based on the position so we should get a gradient
                this.addGameObjectToScene(go);

            }
        }
        */
    }

    private void loadResources(){

        //TODO: Make sure order isnt affecting what is rendered
        AssetPool.getShader("assets/shaders/default.glsl");

        AssetPool.addSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"), 16, 16, 81, 0));
        AssetPool.getTexture("assets/images/blendImage2.png");


    }

//    private int spriteIndex = 0;
//    private float  spriteFlipTime = 0.2f;//moving the sprite every 0.2 seconds
//    private float spriteFlipTimeLeft = 0.0f;


    float t = 0.0f;
    @Override
    public void update(float dt) {

//        spriteFlipTimeLeft -= dt;
//        if(spriteFlipTimeLeft <= 0){
//            spriteFlipTimeLeft = spriteFlipTime;
//            spriteIndex++;
//            if(spriteIndex > 4){
//                spriteIndex = 0;
//            }
//
//            obj1.getComponent(SpriteRenderer.class).setSprite(sprites.getSprite(spriteIndex));
//        }


//        System.out.println("FPS: " + (1.0f/dt));

        mouseControls.update(dt);

        float x = ((float)Math.sin(t) * 200.0f) + 600; //drawing a circle with radius of 200
        float y = ((float)Math.cos(t) * 200.0f) + 400;
        t += 0.05f;
        DebugDraw.addLine2D(new Vector2f(600, 400), new Vector2f(x, y), new Vector3f(0,0, 1), 10);

        for(GameObject go: this.gameObjects){
            go.update(dt);
        }

        this.renderer.render();

    }

    @Override
    public void imgui(){
        ImGui.begin("Test Window");

        ImVec2 windowsPos = new ImVec2();
        ImGui.getWindowPos(windowsPos);//get window position and store it in the variable windowPos
        ImVec2 windowsSize = new ImVec2();
        ImGui.getWindowSize(windowsSize);
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowsPos.x + windowsSize.x;// rightmost coordinate of the window on the screen


        ImVec2 size = new ImVec2();
        ImVec2 uv0 = new ImVec2();
        ImVec2 uv1 = new ImVec2();

        for(int i = 0; i < sprites.size(); i++){
            Sprite sprite = sprites.getSprite(i);

            size.x = sprite.getWidth() * 4;
            size.y = sprite.getHeight() * 4;

            int id = sprite.getTexId();
            Vector2f[] texCoords = sprite.getTexCoords();

            uv0.x = texCoords[0].x;
            uv0.y = texCoords[0].y;
            uv1.x = texCoords[2].x;
            uv1.y = texCoords[2].y;

            //ImGui.pushID(i);
            if(ImGui.imageButton("sprite_" + i, (long) id, size, uv0, uv1)){
                System.out.println("Button " + i + " Clicked");
                GameObject object = Prefabs.generateSpriteObject(sprite, size.x, size.y);

                //Attach to mouse cursor
                mouseControls.pickupObject(object);

            }
            //ImGui.popID();

            ImVec2 lastButtonPos = new ImVec2();
            ImGui.getItemRectMax(lastButtonPos);
            float lastButtonX2 = lastButtonPos.x;
            float nextButtonX2 = lastButtonX2 + itemSpacing.x + (sprite.getWidth() * 4);

            if(i + 1 < sprites.size() && nextButtonX2 < windowX2){
                ImGui.sameLine();//place item on same line as current item
            }

        }

        ImGui.end();
    }

}
