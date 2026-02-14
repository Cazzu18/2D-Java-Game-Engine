package renderer;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

    private String filepath;
    private transient int texID; //we do not want to serialize texture ID
    private int width, height;

    public Texture(){
        texID = -1;
        width = -1;
        height = -1;
    }

    public Texture(int width, int height){
        this.filepath = "Generated";

        //generate texture on GPU
        texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);


        //defining what happens when overflow occurs
        //here we use linear that blurs or pixelates
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);//0 is null in C++
    }

    public void init(String filepath) {
        this.filepath = filepath;

        //generate texture on GPU
        texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);

        //set the texture parameters
        //repeat image in both directions
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);//wrap s wraps u or x
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);//wrap t is y or v

        //when minifying image we are pixelating
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST); //min filter represents minification and instead of blurring, its going to choose the nearest pixel(bigger pixel)

        //when magnifying image we want to pixelate
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);//mag filtler represents magnification


        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        stbi_set_flip_vertically_on_load(true);//so image appears right side up
        ByteBuffer image = stbi_load(filepath, width, height, channels, 0);//filepath is converted to char sequence, IntBuffer x, IntBuffer y, IntBuffer channels(rgba or rgb), desired channels(0)


        if(image != null){
            this.width = width.get(0);
            this.height = height.get(0);

            //uploading pixel to GPU
            if(channels.get(0) == 3){
                //level represents mipmap level
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, image);//only 3 channels so rgb
            }else if(channels.get(0) == 4) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);//rgba
            } else {
                assert false: "ERROR: (TEXTURE) Unknown number of channels '" + channels.get(0) + "'";
            }

        } else {
            assert false: "ERROR: (TEXTURE) Could not load image '" + filepath + "'" ;
        }

        stbi_image_free(image); //frees memory allocated when load
    }

    public void bind(){
        glBindTexture(GL_TEXTURE_2D, texID);
    }

    public void unbind(){
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getId(){
        return this.texID;
    }

    public String getFilepath() {
        return this.filepath;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(!(obj instanceof Texture)) return false;
        Texture other = (Texture) obj;
        return other.getWidth() == this.width && other.getHeight() == this.height && other.getId() == this.texID && other.getFilepath().equals(this.filepath);
    }
}
