#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D MachineOutputSampler;
uniform float time;

in vec2 texCoord;
in vec2 oneTexel;
in vec2 pixelPos;
out vec4 fragColor;

void main() {
    vec2 texShift = vec2(sin(((texCoord.y*360.0)*90.0)+((time/20.0)*45.0))*(oneTexel.x*2.0), 0);
    vec4 color = texture(MachineOutputSampler, texCoord+texShift);
    float blend = color.a;
    if (blend > 0.0) {
        blend /= 1.25;
        blend += (sin((time/20.0)*90.0)*0.05);
        blend = clamp(blend, 0.0, 1.0);
    }
    color.r = (color.r+color.g+color.b)/3.0;
    color.g = color.b/4.0;
    color.b = color.r;
    if (mod(pixelPos.y, 2.0) <= 1.0) {
        blend /= 2.0;
        color /= 2.0;
    }
    fragColor = mix(texture(DiffuseSampler, texCoord), vec4(color.rgb, 1), blend);
}