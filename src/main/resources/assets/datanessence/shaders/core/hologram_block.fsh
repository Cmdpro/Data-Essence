#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec4 normal;

out vec4 fragColor;

void main() {
    vec4 color = vertexColor * ColorModulator;
    color = color * vec4(0.5, 0.5, 1.0, 0.5);
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
