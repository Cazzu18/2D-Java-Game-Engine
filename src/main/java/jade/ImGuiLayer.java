package jade;

import imgui.*;
//import imgui.ImGui;
//import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.glfw.ImGuiImplGlfw;
import imgui.gl3.ImGuiImplGl3;
import imgui.type.ImBoolean;
import scenes.Scene;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Dear ImGui layer for imgui-java 1.90.0 + LWJGL 3.3.6.
 * Key points:
 * - Using ImGuiImplGlfw + ImGuiImplGl3 backends (no manual IO updates/callback plumbing).
 * - Call backend newFrame() methods every frame before ImGui.newFrame().
 * - Call ImGui.render() exactly once per frame.
 */
public class ImGuiLayer {

    private final long glfwWindow;
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private static final String GLSL_VERSION = "#version 330 core";

    public ImGuiLayer(long glfwWindow) {
        this.glfwWindow = glfwWindow;
    }

    public void initImGui() {
        ImGui.createContext();
        ImGui.styleColorsDark();

        //Configuring ImGui
        ImGuiIO io = ImGui.getIO();
        io.setIniFilename("imgui.ini"); // Don't write imgui.ini

        //so I don't overwrite flags accidentally.
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);

        //Init backends:
        //- true = install GLFW callbacks (recommended)
        imGuiGlfw.init(glfwWindow, true);
        imGuiGl3.init(GLSL_VERSION);

        //must be enabled before font atlas generation
        io.getFonts().setFreeTypeRenderer(true);

        //a b c d e f g ! 1 2 3 4 5 6
        final ImFontAtlas fontAtlas = io.getFonts();
        final ImFontConfig fontConfig = new ImFontConfig(); // Natively allocated object, should be explicitly destroyed

        // Fonts merge example
        fontConfig.setPixelSnapH(true);//snap glyph positions to whole pixels on the horizontal axis when building/rendering fonts

        // Glyphs could be added per-font as well as per config used globally like here
        fontAtlas.addFontFromFileTTF("assets/fonts/segoeui.ttf", 32, fontConfig, fontAtlas.getGlyphRangesDefault());

        fontConfig.destroy(); // After all fonts were added we don't need this config more
    }

    /**
     * Call once per frame.
     * Typical order in main loop:
     *   glfwPollEvents();
     *   imguiLayer.update(dt);
     *   glfwSwapBuffers(window);
     */
    public void update(float dt, Scene currentScene) {
        //allowing the backends update IO (delta time, input, display size, etc.)
        //GLFW backend will set io.DeltaTime internally.
        imGuiGlfw.newFrame();
        imGuiGl3.newFrame();
        ImGui.newFrame();
        setupDockspace();
        currentScene.sceneImgui();//calling every frame
        ImGui.showDemoWindow();
        ImGui.end();
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        //Multi-viewport support (only runs if enabled)
        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            long backup = glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            glfwMakeContextCurrent(backup);
        }
    }

    /** Cleanup. Call on shutdown. */
    public void destroyImGui() {
        imGuiGl3.shutdown();
        imGuiGlfw.shutdown();
        ImGui.destroyContext();
    }

    public void setupDockspace() {
        //"parent" window
        int windowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking;
        ImGui.setNextWindowPos(0.0f, 0.0f, ImGuiCond.Always); //start at topleft and make sure its window width and window height
        ImGui.setNextWindowSize(Window.getWidth(), Window.getHeight());
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
        windowFlags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;


        ImGui.begin("Dockspace Demo", new ImBoolean(true), windowFlags);

        ImGui.popStyleVar();
        ImGui.popStyleVar();

        //Dockspace
        ImGui.dockSpace(ImGui.getID("Dockspace"));

    }
}
