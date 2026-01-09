package renderer;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {

    private int shaderProgramID;
    private boolean beingUsed = false;

    private String vertexSource;
    private String fragmentSource;
    private String filepath;

    public Shader(String filepath){
        this.filepath = filepath;
        try{
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");//+ in regex means one or more occurences

            //finding first pattern after #type 'pattern'
            int index = source.indexOf("#type") + 6;//finds the first index of the string, index 0 in our case. 6 is the index of the beginning of the next word
            int eol = source.indexOf("\r\n", index);//eol stands for end of line. starting from index. Here we capture index of the end of"vertex" or "fragment" etc
            String firstPattern = source.substring(index, eol).trim(); //vertex or fragment

            //finding second pattern after #type 'pattern'
            index = source.indexOf("#type", eol) + 6;//starting from eol
            eol = source.indexOf("\r\n", index);
            String secondPattern = source.substring(index, eol).trim();

            if(firstPattern.equals("vertex")){
                vertexSource = splitString[1]; //garbage for the 0 index, index 1 contains the first pattern shader and index 2 contains the second pattern shader
            } else if(firstPattern.equals("fragment")){
                fragmentSource = splitString[1];
            } else {
                throw new IOException("Unexpected token '" + firstPattern + "'");
            }

            if(secondPattern.equals("vertex")){
                vertexSource = splitString[2]; //garbage for the 0 index, index 1 contains the first pattern shader and index 2 contains the second pattern shader
            } else if(secondPattern.equals("fragment")){
                fragmentSource = splitString[2];
            } else {
                throw new IOException("Unexpected token '" + secondPattern + "'");
            }

        } catch(IOException e){
            e.printStackTrace();
            assert false: "Error: Could not load shader file: '" + filepath + "'";
        }

//        System.out.println(vertexSource);
//        System.out.println(fragmentSource);
    }

    public void compile_and_link(){
        // ==================================
        // Compile and link shaders
        // ==================================

        int vertexID, fragmentID;

        ///first load and compile vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);

        //pass shader source code to GPU
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);

        //checking for errors in compilation
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS); //0 if did not succeed

        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: '" + filepath + "'\n\tVertex shader compilation failed.");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : ""; //breaking out of program
        }

        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);

        //pass shader source code to GPU
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);

        //checking for errrors in compilation
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS); //0 if did not succeed

        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: '" + filepath + "'\n\tFragment shader compilation failed.");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : ""; //breaking out of program
        }

        //link shaders and check for errors
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        //checking for linking errors
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: '" + filepath + "'\n\tlinking of shaders failed.");
            System.out.println(glGetProgramInfoLog(shaderProgramID, len));
            assert false : ""; //breaking out of program
        }
    }

    public void use(){
        //bind shader program
        if(!beingUsed){
            //bind shader program
            glUseProgram(shaderProgramID);
            beingUsed = true;
        }
    }

    public void detach(){
        glUseProgram(0);
        beingUsed = false;
    }

    public void uploadMat4f(String varName, Matrix4f mat4){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);//location of variable in shader program
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16); //16 since 4*4 matrix
        mat4.get(matBuffer); //flatten matrix into 1D array [1, 1, 1, 1, ...]16 elements
        glUniformMatrix4fv(varLocation, false, matBuffer);//transpose is a linear algebraic function

    }

    public void uploadMat3f(String varName, Matrix3f mat3){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);//location of variable in shader program
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9); //9 since 3*3 matrix
        mat3.get(matBuffer); //flatten matrix into 1D array [1, 1, 1, 1, ...]16 elements
        glUniformMatrix3fv(varLocation, false, matBuffer);//transpose is a linear algebraic function
    }

    public void uploadVec4f(String varName, Vector4f vec){

        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
    }

    public void uploadVec3f(String varName, Vector3f vec){

        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform3f(varLocation, vec.x, vec.y, vec.z);
    }
    public void uploadVec2f(String varName, Vector2f vec){

        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform2f(varLocation, vec.x, vec.y);
    }

    public void uploadFloat(String varName, float val){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1f(varLocation, val);
    }

    public void uploadInt(String varName, int val){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, val);
    }

    public void uploadTexture(String varName, int slot){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, slot);
    }

    public void uploadIntArray(String varName, int[] array){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1iv(varLocation, array);
    }
}
//glGetUniformLocation returns an integer that represents the location of a specific uniform variable within a program object