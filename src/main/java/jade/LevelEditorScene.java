package jade;
import components.FontRenderer;
import components.SpriteRenderer;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import renderer.Shader;
import renderer.Texture;
import util.Time;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {
    //(x, y,z)
    private float[] vertexArray = {
            //position                        //color                       //UV coordinates
            100.5f,  0.5f,   0.0f,            1.0f, 0.0f, 0.0f, 1.0f,       1, 0, //Bottom right 0
            0.5f,    100.5f, 0.0f,            0.0f, 1.0f, 0.0f, 1.0f,       0, 1, //Top left 1
            100.5f,  100.5f, 0.0f,            0.0f, 0.0f, 1.0f, 1.0f,       1, 1, //Top right 2
            0.5f,    0.5f,   0.0f,            1.0f, 1.0f, 0.0f, 1.0f,       0, 0, //Bottom left 3
    };

    //IMPORTANT: MUST BE IN COUNTER-CLOCKWISE ORDER
    private int[] elementArray = {
        /*
                2x3       x2

                3x       1x1
         */
            2, 1, 0, //top right triangle
            0, 1, 3 //bottom left traingle

    };

    private int vaoID, vboID, eboID;

    private Shader defaultShader;
    private Texture testTexture;
    GameObject testObj;
    private boolean firstTime = true;

    public LevelEditorScene() {
    }

    @Override
    public void init() {

        System.out.println("Creating test object");
        this.testObj = new GameObject("test object");
        this.testObj.addComponent(new SpriteRenderer());
        this.testObj.addComponent(new FontRenderer());
        this.addGameObjectToScene(this.testObj);


        //we can use this.camera because LevelEditorScene extends Scene
        this.camera = new Camera(new Vector2f()); //camera at (0,0)

        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compile_and_link();

        this.testTexture = new Texture("assets/images/testImage.png");
        // ==============================================================
        // Generating VAO, VBO, and EBO buffer objects, and send to GPU
        // ==============================================================
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        //create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        //create the VBO and upload verttex buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW); //working with array buffer, send that specific buffer to id, and only draw statically

        //create the indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        //adding vertex attribute pointers(pointer then colors)
        int positionSize = 3; //xyz
        int colorSize = 4; //rgba
        int uvSize = 2;
        int vertexSizeBytes = (positionSize + colorSize + uvSize) * Float.BYTES;

        //remember in our shader script we said that location=0 is for aPos (attribute position)
        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0); //the vertexSizeBytes is the stride in bytes, adn the offset is 0
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes,positionSize * Float.BYTES) ; // the last argumen(offset) is positionSize in bytes
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeBytes, (positionSize + colorSize)* Float.BYTES) ;
        glEnableVertexAttribArray(2);
    }

    @Override
    public void update(float dt) {

        camera.position.x -= dt * 50.0f;
        camera.position.y -= dt * 20.0f;

        //bind shader program
        defaultShader.use();

        //upload texture to shader
        defaultShader.uploadTexture("TEX_SAMPLER", 0);//we want to upload the texture id that is in slot 0
        glActiveTexture(GL_TEXTURE0);//activate slot 0
        testTexture.bind();

        //uploading uniforms to shaders
        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
        defaultShader.uploadFloat("uTime", Time.getTime());

        //bind VAO that we're using
        glBindVertexArray(vaoID);

        //enable vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        //unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0); //BIND NOTHING

        defaultShader.detach();//use nothing


        if(firstTime) {
            System.out.println("Creating gameObject");
            GameObject go = new GameObject("Game test 2");
            go.addComponent(new SpriteRenderer());
            this.addGameObjectToScene(go);
            firstTime = false;
        }

        for(GameObject go: this.gameObjects){
            go.update(dt);
        }


    }

}
