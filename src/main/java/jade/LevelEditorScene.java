package jade;

import components.Sprite;
import components.SpriteRenderer;
import components.Spritesheet;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

public class LevelEditorScene extends Scene {
    private GameObject obj1;
    private Spritesheet sprites;

    public LevelEditorScene() {
    }

    @Override
    public void init() {
        loadResources();
        this.camera = new Camera(new Vector2f());

        sprites  = AssetPool.getSpritesheet("assets/images/spritesheet.png");

        obj1 = new GameObject("Object 1", new Transform(new Vector2f(200, 100), new Vector2f(256, 256)), 2);//position and scale(pixel size of projection)
        obj1.addComponent(new SpriteRenderer(sprites.getSprite(0)));
        //obj1.addComponent(new SpriteRenderer(new Sprite(AssetPool.getTexture("assets/images/blendImage1.png"))));
        this.addGameObjectToScene(obj1);

        GameObject obj2 = new GameObject("Object 2", new Transform(new Vector2f(400, 400), new Vector2f(256, 256)), -1);
        obj2.addComponent(new SpriteRenderer(new Sprite(AssetPool.getTexture("assets/images/blendImage2.png"))));
        this.addGameObjectToScene(obj2);

        GameObject obj3 = new GameObject("Object 2", new Transform(new Vector2f(200, 400), new Vector2f(256, 256)), -1);
        obj3.addComponent(new SpriteRenderer(new Sprite(AssetPool.getTexture("assets/images/blendImage1.png"))));
        this.addGameObjectToScene(obj3);

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
        AssetPool.getShader("assets/shaders/default.glsl");

        AssetPool.addSpritesheet("assets/images/spritesheet.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheet.png"), 16, 16, 26, 0));

    }

    private int spriteIndex = 0;
    private float  spriteFlipTime = 0.2f;//moving the sprite every 0.2 seconds
    private float spriteFlipTimeLeft = 0.0f;
    @Override
    public void update(float dt) {
        spriteFlipTimeLeft -= dt;
        if(spriteFlipTimeLeft <= 0){
            spriteFlipTimeLeft = spriteFlipTime;
            spriteIndex++;
            if(spriteIndex > 4){
                spriteIndex = 0;
            }

            obj1.getComponent(SpriteRenderer.class).setSprite(sprites.getSprite(spriteIndex));
        }


        System.out.println("FPS: " + (1.0f/dt));

        for(GameObject go: this.gameObjects){
            go.update(dt);
        }

        this.renderer.render();

    }

}
