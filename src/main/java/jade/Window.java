//TODO: WRITE NOTES ABOUT THE LISTENERS AND CALLBACKS

package jade;

import observers.EventSystem;
import observers.Observer;
import observers.events.Event;
import observers.events.EventType;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.opengl.GL;
import renderer.*;
import scenes.LevelEditorSceneInitializer;
import scenes.Scene;
import scenes.SceneInitializer;
import util.AssetPool;
import util.Time;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements Observer {

    //we can control behavior
    private int width, height;
    private String title;
    private long glfwWindow;
    private ImGuiLayer imGuiLayer;
    private Framebuffer framebuffer;
    private PickingTexture pickingTexture;
    private boolean runtimePlay = false;

    //singleton. We'll only ever have one instance of window
    private static Window window = null;

    private long audioContext;
    private long audioDevice;

    private static Scene currentScene;

    private Window(){
        this.width = 1920;
        this.height = 1080;
        this.title = "Jade";

        EventSystem.addObserver(this);
    }

    public static void changeScene(SceneInitializer sceneInitializer){
        if(currentScene != null){
            //destroy it
            currentScene.destroy();
        }

        getImGuiLayer().getPropertiesWindow().setActiveGameObject(null);
        currentScene = new Scene(sceneInitializer);
        currentScene.load();
        currentScene.init();
        currentScene.start();
    }

    //the only time the window will be created is when we call Window.get()
    public static Window get(){
        if (Window.window == null){
            Window.window = new Window();
        }

        return Window.window;
    }

    public static Scene getScene(){
        return get().currentScene;
    }

    public void run(){
        System.out.println("Hello LWJGL" + Version.getVersion() + "!");

        init();
        loop();

        //Destroy audio context and devices
        alcDestroyContext(audioContext);
        alcCloseDevice(audioDevice);

        //Since we're using C bindings in Java
        //free memory once loop has exited
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        //terminate glfw and free the error callbacks
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init(){
        //Setup an error callback(where GLFW will print to if there is an error)
        GLFWErrorCallback.createPrint(System.err).set();

        //initialize GLFW
        if (!glfwInit()){
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        //configure glfw
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);


        //Create the window(number is the memory address where the window is)
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);

        if (glfwWindow == NULL){
            throw new IllegalStateException("Failed to create GLFW window");
        }


        //:: forwards to a function. forward poscallback to a function when there is a cursor callback
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::KeyCallback);


        //make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);

        //enable vsync(no wait time between frames)
        glfwSwapInterval(1);

        //make the window visible
        glfwShowWindow(glfwWindow);

        //Intit the audion device(do before glcreatecapabilites)
        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        audioDevice = alcOpenDevice(defaultDeviceName);

        int[] attributes = {0};
        audioContext = alcCreateContext(audioDevice, attributes); //not using any attributes
        alcMakeContextCurrent(audioContext);

        ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

        if(!alCapabilities.OpenAL10){
            assert false: "Audio library not supported.";
        }

        //This line is critical for LWJGL's interpolation with GLFW's
        // OpenGl context, or any context that is managed externally
        // LWJGL detects the context that is current in the current thread
        // creates the GLCapabilities instance and makes the OpenGl
        // bindings available for use.
        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);//(sfactor, dfactor) source and destination

        //TODO: Query for monitor screen size
        //framebuffer and picking texture arguments MUST match
        this.framebuffer = new Framebuffer(1920, 1080);
        this.pickingTexture = new PickingTexture(1920, 1080);
        glViewport(0, 0, 1920, 1080);

        this.imGuiLayer = new ImGuiLayer(glfwWindow, pickingTexture);
        this.imGuiLayer.initImGui();


        Window.changeScene(new LevelEditorSceneInitializer());
    }

    public void loop(){
        float beginTime = Time.getTime(); //time that frame began
        float endTime; //time that the frame ended
        float dt = -1.0f;

        Shader defaultShader = AssetPool.getShader("assets/shaders/default.glsl");
        //defaultShader.compile_and_link();
        Shader pickingShader = AssetPool.getShader("assets/shaders/pickingShader.glsl");

        while(!glfwWindowShouldClose(glfwWindow)){
            //poll events
            glfwPollEvents();

            //Render pass 1. Render to picking texture
            glDisable(GL_BLEND);
            pickingTexture.enableWriting();

            glViewport(0, 0, 1920, 1080);
            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clearing these two buffer bits

            Renderer.bindShader(pickingShader);
            currentScene.render();

//            if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)){
//                int x = (int)MouseListener.getScreenX();
//                int y = (int)MouseListener.getScreenY();
//                System.out.println(pickingTexture.readPixel(x,y));
//            }

            pickingTexture.disableWriting();
            glEnable(GL_BLEND);
            this.imGuiLayer.update(dt, currentScene); //more like begin frame

            //Render pass 2. Render actual game

            DebugDraw.beginFrame();

            this.framebuffer.bind();

            //every frame
            glClearColor(1, 1, 1, 1);

            //telling OpenGl to use the color buffer bit
            glClear(GL_COLOR_BUFFER_BIT); //flush clear color to entire screen

            //a lag of two frames before we start updating
            if(dt >= 0) { //since we initialize dt below this code
                DebugDraw.draw();//draw line and then everything else
                Renderer.bindShader(defaultShader);
                if(runtimePlay){
                    currentScene.update(dt);
                } else {
                    currentScene.editorUpdate(dt);
                }
                currentScene.render();

            }

            this.framebuffer.unbind();

            /*
            * if you use double buffering you can update the screen
            * in the background without the user seeing everything
            * being drawn incrementally. This allows the current frame
            * to be replaced with the new one in a single monitor refresh cycle
            * and seamless transition.
            */

            //this.imGuiLayer.update(dt, currentScene);
            this.imGuiLayer.endFrame();
            glfwSwapBuffers(glfwWindow);

            MouseListener.endFrame();

            endTime = Time.getTime();
            dt = endTime - beginTime; //time elapsed(delta time)
            beginTime = endTime;

        }

    }

    public static int getWidth() {
        return get().width;
    }

    public static int getHeight() {
        return get().height;
    }

    public static void setWidth(int newWidth) {
        get().width = newWidth;
    }

    public static void setHeight(int newHeight) {
        get().height = newHeight;
    }

    public static Framebuffer getFrameBuffer() {
        return get().framebuffer;
    }

    public static float getTargetAspectRatio() {
        return 16.0f / 9.0f;
    }

    public static ImGuiLayer getImGuiLayer() {
        return get().imGuiLayer;
    }

    @Override
    public void onNotify(GameObject gameObject, Event event) {

        switch(event.type) {
            case GameEngineStartPlay:
                this.runtimePlay = true;
                currentScene.save();
                Window.changeScene(new LevelEditorSceneInitializer());
                break;
            case GameEngineStopPlay:
                this.runtimePlay = false;
                Window.changeScene(new LevelEditorSceneInitializer());
                break;
            case LoadLevel:
                Window.changeScene(new LevelEditorSceneInitializer());
                break;
            case SaveLevel:
                currentScene.save();
                break;

        }
    }
}
