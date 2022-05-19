#version 330 core

layout (location = 0) in vec4 pos;
layout (location = 1) in vec2 tc;

uniform mat4 projection_matrix;
uniform mat4 view_matrix = mat4(1);
uniform mat4 transform_matrix = mat4(1);
uniform vec4 color_vec4;

out vec2 f_tc;
out vec4 f_color;

void main() {
	gl_Position = projection_matrix * view_matrix * transform_matrix * pos;
	f_tc = tc;
	f_color = color_vec4;
}