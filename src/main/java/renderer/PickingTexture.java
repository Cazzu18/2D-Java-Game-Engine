package renderer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT32;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;

//FUTURE: SHOULD BE ENGINE EDITOR SPECIFIC(INSTEAD OF ACTUAL ENGINE)
public class PickingTexture {
    private int pickingTextureId;
    private int fbo;
    private int depthTexture; //NECESSARY FOR 3D
    private float width;
    private float height;

    //width and height will be the same size as game window(1920 * 1080)
    public PickingTexture(int width, int height) {
        assert init(width, height) : "Error initializing picking texture";
        this.width = width;
        this.height = height;
    }

    public boolean init(int width, int height){
        //Generate framebuffer
        fbo= glGenFramebuffers(); //FUTURE: Create a factory that creates framebuffer with different options
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);


        //create the texture to render the data to, and attach it to our frame buffer
        pickingTextureId= glGenTextures(); //generating the texture ID
        glBindTexture(GL_TEXTURE_2D, pickingTextureId);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT); //x(S) direction wrap
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT); //y(T) direction wrap
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST); //if stretching picture, choose nearest pixel so we pixelate instead of blending
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB32F, width, height, 0, GL_RGB, GL_FLOAT, 0); //rgb data of size 32 bits float(4 bytes). Here we are specifying a two-dimensional texture image(with no pixel data)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.pickingTextureId, 0); //level(0) specifies the mipmap level of the texture image to be attached, which must be 0

        //Create the texture object for the depth buffer SO THAT IF ONE SPRITE BEHIND EACH OTHER IT IS RENDERED APPROPRIATELY
        glEnable(GL_TEXTURE_2D); // TODO: THIS MIGHT BE GL_DEPTH_TEST
        depthTexture= glGenTextures();
        glBindTexture(GL_TEXTURE_2D, depthTexture);
        glTexImage2D(GL_TEXTURE_2D, 0 , GL_DEPTH_COMPONENT, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, this.depthTexture, 0);

        //Disable the reading
        glReadBuffer(GL_NONE); //Right Now we WILL NOT READ from ANY Framebuffer
        glDrawBuffer(GL_COLOR_ATTACHMENT0); //draw to GL_COLOR_ATTACHMENT0

        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE){
            assert false: "ERROR: Framebuffer not complete";
            return false;
        }

        //Unbind the texture and framebuffer
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);//unbinds current framebuffer

        return true;
    }

    public void enableWriting(){
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fbo);
    }

    public void disableWriting(){
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
    }

    public int readPixel(int x, int y){
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fbo); //reading from the framebuffer
        glReadBuffer(GL_COLOR_ATTACHMENT0); //reading from GL_COLOR_ATTACHMENT0 which we are drawing everything to

        float[] pixels = new float[3]; //rgb data
        glReadPixels(x, y , 1, 1, GL_RGB, GL_FLOAT, pixels); //x and y represent what pixel we start from

        return (int) (pixels[0]) - 1;//index 0 gives us WRONG results
    }

    public float GetWidth(){
        return this.width;
    }

    public float GetHeight(){
        return this.height;
    }



}
