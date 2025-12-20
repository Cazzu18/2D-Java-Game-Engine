#type vertex
#version 330 core

//a stands for attribute
//vec4 is a 4 component floating point vector type used for storing 4d coordinates, colors with alpha transparency, or other data
//vec3 is a 3 component floating point vector type. Used for various purposes like positions(3D), Normals, Colors, Directions
layout(location=0) in vec3 aPos; //layout(location=0) explicitly assigns a specific index (0 in this case) to a shader variable
layout(location=1) in vec4 aColor; //rgba
layout(location=2) in vec2 aTexCoords;//vertex Array in LevelEditorScnene(Tex coordinates)
layout(location=3) in float aTexId;

//we upload these from Shader class
uniform mat4 uProjection;
uniform mat4 uView;

//prefix with f because going to fragement shader
out vec4 fColor;
out vec2 fTexCoords;
out float fTexId;
//out vec2 fTexCoords;

void main(){
    fColor = aColor;
    fTexCoords = aTexCoords;
    fTexId = aTexId;
    gl_Position = uProjection * uView * vec4(aPos, 1.0); //will create a vector4 with aPos as the first three elements and 1.0 as the 4th

}

#type fragment
#version 330 core

//uniform float uTime;
//uniform sampler2D TEX_SAMPLER;

in vec4 fColor;
in vec2 fTexCoords;
in float fTexId;

/*
sampler2D is an opaque data type representing a 2D texture used in shaders
(like fragment shaders) to sample (read) color values (texels) from a 2D image
at specific coordinates (UVs) to apply details like color, roughness, or normals
to 3D models
*/
uniform sampler2D uTextures[8];

out vec4 color;

void main(){
    if(fTexId > 0){
        int id = int(fTexId);
        //since fColor is initially white(1, 1, 1, 1) * (0.4, 2, 3, 1) = (0.4, 2, 3, 1)
        color = fColor * texture(uTextures[id], fTexCoords);//sampling color values from 2D texture to specific UV coordinates
        //color  = vec4(fTexCoords, 0, 1); //(x, y, 0, 1) rgba
    } else {
        color = fColor;
    }

    //we convert the product of the sin of the dot product(returns a scalar that represents the degree to which two vectors point in the same direction)of fColor.xy and the random vector and the random number 43758... into a fraction
    //noise formula found online. Sin clamps between 0 and 1
    //float noise = fract(sin(dot(fColor.xy, vec2(12.9898,78.233))) * 43758.5453);
    //color = fColor * noise;
    //float avg = (fColor.r + fColor.g + fColor.b) / 3;
    //vec4(avg, avg, avg, 1); makes the color black and white
    //color = sin(uTime) * fColor; sin of the uTime(time at every frame). This means the color should blink
}

