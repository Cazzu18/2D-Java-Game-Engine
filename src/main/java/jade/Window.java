//TODO: WRITE NOTES ABOUT THE LISTENERS AND CALLBACKS

package jade;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import util.Time;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    //we can control behavior
    private int width, height;
    private String title;
    private long glfwWindow;
    private ImGuiLayer imGuiLayer;
    public float r, g, b, a;

    //singleton. We'll only ever have one instance of window
    private static Window window = null;

    private static Scene currentScene;

    private Window(){
        this.width = 1920;
        this.height = 1080;
        this.title = "Mario";
        r = 1;
        g = 1;
        b = 1;
        a = 1;
    }

    public static void changeScene(int newScene){
        switch(newScene){
            case 0:
                currentScene = new LevelEditorScene();
                break;
            case 1:
                currentScene = new LevelScene();
                break;
            default:
                assert false: "Unknown Scene '" + newScene + "'";
                break;
        }

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

        //This line is critical for LWJGL's interpolation with GLFW's
        // OpenGl context, or any context that is managed externally
        // LWJGL detects the context that is current in the current thread
        // creates the GLCapabilities instance and makes the OpenGl
        // bindings available for use.
        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);//(sfactor, dfactor) source and destination

        this.imGuiLayer = new ImGuiLayer(glfwWindow);
        this.imGuiLayer.initImGui();


        Window.changeScene(0);
    }

    public void loop(){
        float beginTime = Time.getTime(); //time that frame began
        float endTime; //time that the frame ended
        float dt = -1.0f;

        while(!glfwWindowShouldClose(glfwWindow)){
            //poll events
            glfwPollEvents();

            //every frame
            glClearColor(r, g, b, a);

            //telling OpenGl to use the color buffer bit
            glClear(GL_COLOR_BUFFER_BIT); //flush clear color to entire screen

            //a lag of two frames before we start updating
            if(dt >= 0) { //since we initialize dt below this code
                currentScene.update(dt);
            }

            /*
            * if you use double buffering you can update the screen
            * in the background without the user seeing everything
            * being drawn incrementally. This allows the current frame
            * to be replaced with the new one in a single monitor refresh cycle
            * and seamless transition.
            */

            this.imGuiLayer.update(dt, currentScene);
            glfwSwapBuffers(glfwWindow);

            endTime = Time.getTime();
            dt = endTime - beginTime; //time elapsed(delta time)
            beginTime = endTime;

        }

        currentScene.saveExit();

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
}
