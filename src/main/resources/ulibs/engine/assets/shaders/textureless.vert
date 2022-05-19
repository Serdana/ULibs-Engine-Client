#version 330 core

layout (location = 0) in vec4 pos;

uniform mat4 projection_matrix;
uniform mat4 view_matrix = mat4(1);
uniform mat4 transform_matrix = mat4(1);
uniform vec4 color_vec4;

out vec4 f_color;

void main() {
	gl_Position = projection_matrix * view_matrix * transform_matrix * pos;
	f_color = color_vec4;
}