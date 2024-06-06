#version 150

uniform sampler2D DiffuseSampler;
uniform float time;

in vec2 texCoord;
in vec2 oneTexel;
out vec4 fragColor;

void main() {
    float max = 5.0;
    float speed = 0.02;
    float waveyAmount = clamp(time, 0.0, 1.0);
    speed = time*4;
    if (time >= max/2) {
        speed = (max/2)-time;
        waveyAmount = clamp(max-time, 0.0, 1.0);
    }
    vec2 cp = -1.0 + 2.0 * texCoord;
    float cl = length(cp);
    vec2 uv = texCoord + (cp / cl) * cos(cl * 12.0 - time * 4.0) * (0.02*(waveyAmount));
    vec3 col = texture(DiffuseSampler, uv).xyz;
    fragColor = vec4(col, 1.0);
}