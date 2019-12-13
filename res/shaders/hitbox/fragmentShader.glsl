#version 400 core

in vec2 pass_textureCoordinates;

out vec4 out_Color;

uniform sampler2D modelTexture;

void main(void){
	out_Color = texture(modelTexture,pass_textureCoordinates);
}