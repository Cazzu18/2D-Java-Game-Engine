# JavaEngine (2D) - Work In Progress

JavaEngine is a custom 2D game engine/editor written in Java using LWJGL and OpenGL.  
The project is already functional for sprite-based level editing and rendering, and it is actively being expanded toward a fuller runtime/gameplay engine.

## Project Status

This project is **not finished yet**.  
Current implementation is strongest on:
- Window + main loop infrastructure
- Scene management
- Component-driven game objects
- 2D batched sprite rendering
- Dear ImGui editor tooling
- Basic level save/load via JSON

Planned/partial systems (already scaffolded or in progress):
- Runtime gameplay scene logic
- Physics/rigid body behavior
- Expanded tooling and scene pipeline
- Improved render target/framebuffer usage

## Technology Stack

### Core Runtime
- **Java**
- **LWJGL 3.3.6**
  - `lwjgl`, `glfw`, `opengl`, `stb`
  - Also included for future growth: `assimp`, `nfd`, `openal`
- **OpenGL 3.3 Core** shaders
- **JOML 1.10.8** (math: vectors/matrices)

### Editor + Data
- **imgui-java 1.90.0** (`imgui-java-binding`, LWJGL3 backend)
- **Gson 2.13.2** (component and game object serialization)

### Build + Test
- **Gradle Wrapper** (`gradlew` / `gradlew.bat`)
- **JUnit 5.10.0** configured (no test sources yet)

## Engine Methodologies and Design Approach

This project uses a practical, iterative engine architecture with clear separation of concerns.

### 1) Component-Oriented Entity Design
- `GameObject` is a container of `Component` instances.
- Systems are behavior-driven through `Component.start()` and `Component.update(dt)`.
- Components are decoupled and reusable (for example: `SpriteRenderer`, `MouseControls`, `GridLines`, `RigidBody`).

Why this matters:
- Encourages composition over deep inheritance trees.
- Lets new gameplay/editor features be added as components with minimal coupling.

### 2) Scene Lifecycle Management
- Base `Scene` class provides:
  - `load()`
  - `init()`
  - `start()`
  - `update(dt)`
  - `saveExit()`
- `Window.changeScene(...)` handles scene switching and lifecycle bootstrapping.
- Current scenes:
  - `LevelEditorScene` (main implemented workflow)
  - `LevelScene` (runtime stub for future expansion)

Why this matters:
- Clean boundaries between tooling scene and play scene.
- Predictable lifecycle for initialization, updates, and persistence.

### 3) Data-Driven Serialization
- Uses Gson with custom serializers/deserializers:
  - `ComponentDeserializer`
  - `GameObjectDeserializer`
- Scene saves to `level.txt` on exit and reloads object/component state on start.
- IDs are restored and counters are reinitialized to avoid ID collisions on load.

Why this matters:
- Enables editor-created content to become persistent game data.
- Keeps level content external to code.

### 4) Performance-Oriented 2D Rendering
- Batched sprite renderer (`RenderBatch`) minimizes draw calls.
- Sorting by `zIndex` supports layered 2D rendering.
- Multi-texture batching supports up to 8 texture slots in the default shader.
- Dirty-flag updates reduce unnecessary vertex buffer uploads.

Why this matters:
- Scales better than per-sprite draw calls.
- Matches common production 2D renderer patterns.

### 5) Editor-First Workflow
- Dear ImGui dockspace + inspector tooling.
- Sprite palette in editor window for rapid placement.
- Grid snapping and cursor-driven object placement.
- Debug drawing primitives (`line`, `box`, `circle`) for visualization.

Why this matters:
- Fast iteration loop for world building and system debugging.
- Engine features are validated visually while being built.

### 6) Asset Caching / Pooling
- `AssetPool` caches shaders, textures, and spritesheets by absolute path.
- Avoids duplicate GPU uploads and repeated loads.

Why this matters:
- Better runtime efficiency and simpler resource reuse.

## High-Level Runtime Flow

1. `Main` starts `Window.get().run()`
2. `Window.init()` configures GLFW/OpenGL/input callbacks and ImGui
3. Scene is selected (`LevelEditorScene` by default)
4. Per frame:
   - Poll input
   - Build debug draw frame
   - Update active scene (`update(dt)`)
   - Render editor UI
   - Swap buffers
5. On shutdown, scene data is saved (`level.txt`)

## Current Feature Snapshot

- Orthographic camera and camera matrices
- Sprite + spritesheet UV slicing
- Batched quad rendering with custom GLSL shaders
- Basic input listeners:
  - Keyboard
  - Mouse position/buttons/scroll
- Editor placement tools:
  - Sprite picking
  - Grid snapping
  - Click-to-place
- Debug geometry rendering
- Level persistence (JSON-like serialized object graph)

## Project Structure

```text
JavaEngine/
  assets/
    fonts/
    images/
    shaders/
  src/main/java/
    Main.java
    jade/         # window, lifecycle, core objects, input bridge, imgui layer
    scenes/       # Scene base + editor/runtime scenes
    components/   # reusable gameplay/editor components
    renderer/     # shader, texture, batching, debug draw, framebuffer
    util/         # asset pool, math helpers, time, settings
  build.gradle
  settings.gradle
  level.txt       # serialized scene state
```

## Getting Started

### Prerequisites
- JDK installed
- Windows environment (current natives target is `natives-windows`)
- OpenGL-capable GPU/driver

### Build

From `JavaEngine/`:

```powershell
.\gradlew.bat build --console=plain
```

### Test

```powershell
.\gradlew.bat test --console=plain
```

Current state: Gradle/JUnit are configured, but there are no test source files yet.

### Run

There is currently no Gradle `run` task configured (`application` plugin is not yet applied).  
Run the engine from your IDE by launching `src/main/java/Main.java`.

## Known Limitations (Current Snapshot)

- Runtime gameplay scene is minimal (`LevelScene` is a stub).
- Physics/collision systems are not complete yet.
- Framebuffer class exists, but scene rendering is currently direct-to-screen in main loop.
- Some dependencies are pre-added for planned systems and are not fully used yet.
- Test coverage has not started.

## Roadmap Ideas

- Add full runtime scene/gameplay pipeline
- Expand physics and collision layers
- Add camera controls in editor/runtime
- Complete framebuffer/editor viewport rendering path
- Build import/content tools and richer prefab workflows
- Add automated tests for serialization, math utilities, and scene lifecycle

## Development Philosophy

This engine is being built with a **learn-build-iterate** method:
- Build a vertical slice (editor + rendering + persistence)
- Validate visually and functionally
- Refactor for reusability (components, scenes, asset pool)
- Expand toward gameplay/runtime systems with stable foundations

That makes this repository both a functional engine-in-progress and a strong base for continued feature growth.

