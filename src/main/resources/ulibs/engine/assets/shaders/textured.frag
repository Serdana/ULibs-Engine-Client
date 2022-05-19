#version 330 core

layout (location = 0) out vec4 color;

in vec2 f_tc;
in vec4 f_color;

uniform sampler2D tex;

void main() {
	color = texture(tex, vec2(f_tc.x, -f_tc.y));
	color = color * f_color;
	
	if (color.w == 0) {
		discard;
	}
}