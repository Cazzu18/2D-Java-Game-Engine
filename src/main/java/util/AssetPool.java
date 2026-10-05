package util;

import components.Sprite;
import components.Spritesheet;
import jade.Sound;
import renderer.Shader;
import renderer.Texture;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static Map<String, Spritesheet> spritesheets = new HashMap<>();
    private static Map<String, Sound> sounds = new HashMap<>();

    public static Shader getShader(String resourceName){
        File file = new File(resourceName); //assume filepath relative to root of the project
        if (AssetPool.shaders.containsKey(file.getAbsolutePath())){
            return AssetPool.shaders.get(file.getAbsolutePath());
        } else {
            Shader shader = new Shader(resourceName);
            shader.compile_and_link();
            AssetPool.shaders.put(file.getAbsolutePath(), shader);//for our hashmap the key is the absolute file path and the value is the reference to the object
            return shader;
        }


    }

    public static Texture getTexture(String resourceName){
        File file = new File(resourceName);
        if(AssetPool.textures.containsKey(file.getAbsolutePath())){
            return AssetPool.textures.get(file.getAbsolutePath());
        } else {
            Texture texture = new Texture();
            texture.init(resourceName);
            AssetPool.textures.put(file.getAbsolutePath(), texture);
            return texture;
        }
    }

    public static void addSpritesheet(String resourceName, Spritesheet spritesheet){
        File file = new File(resourceName);

        //if we don't have the sheet already we add it
        if(!AssetPool.spritesheets.containsKey(file.getAbsolutePath())){
            AssetPool.spritesheets.put(file.getAbsolutePath(), spritesheet);
        }
    }

    public static Spritesheet getSpritesheet(String resourceName){
        File file = new File(resourceName);

        if(!AssetPool.spritesheets.containsKey(file.getAbsolutePath())){
            assert false: "Error: Tried to access spritesheet '" + resourceName + "' that has not been added to asset pool.";
        }
        return AssetPool.spritesheets.getOrDefault(file.getAbsolutePath(), null);//default to null for safety or can add default spritesheet(like pink&black for minecraft)
    }

    public static Collection<Sound> getAllSounds(){
        return sounds.values();
    }

    public static Sound getSound(String soundFile){
        File file = new File(soundFile);

        if(AssetPool.sounds.containsKey(file.getAbsolutePath())){
            return sounds.get(file.getAbsolutePath());
        } else {
            assert false : "Sound file not added '" + soundFile + "' to asset pool.";
        }

        return null;
    }

    public static Sound addSound(String soundFile, boolean loops){
        File file = new File(soundFile);
        if(AssetPool.sounds.containsKey(file.getAbsolutePath())){
            return sounds.get(file.getAbsolutePath());
        } else {
            Sound sound = new Sound(file.getAbsolutePath(), loops);
            AssetPool.sounds.put(file.getAbsolutePath(), sound);
            return sound;
        }
    }
}
