#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;
uniform float colorOffset;
uniform float time;
uniform mat4 invViewMat;
uniform mat4 invProjMat;
uniform vec3 CameraPosition;
uniform vec3 PingPosition;

in vec2 texCoord;
in vec2 oneTexel;
out vec4 fragColor;

const float near = 1.0;
const float far = 100.0;

float linearizeDepth(float depth) {
    return (2.0 * near) / (far + near - depth * (far - near));
}

vec3 reconstructPosition(vec2 uv, float z, mat4 invProjectionMatrix) {
    float x = uv.x * 2.0 - 1.0;
    float y = (1.0 - uv.y) * 2.0 - 1.0;
    vec4 pos_s = vec4(x,y,z,1.0);
    vec4 pos_v = invProjectionMatrix*pos_s;
    return pos_v.xyz/pos_v.w;
}
vec3 worldPos(float depth) {
    float z = depth * 2.0 - 1.0;
    vec4 clipSpacePosition = vec4(texCoord * 2.0 - 1.0, z, 1.0);
    vec4 viewSpacePosition = invProjMat * clipSpacePosition;
    viewSpacePosition /= viewSpacePosition.w;
    vec4 worldSpacePosition = invViewMat * viewSpacePosition;

    return CameraPosition + worldSpacePosition.xyz;
}
void main() {
    float targetDist = time*25;
    float range = 1;
    vec3 color = vec3(1.0, 0.0, 1.0);
    vec3 world = worldPos(texture(DiffuseDepthSampler, texCoord).r);
    float dist = distance(world.xz, PingPosition.xz);
    float blend = 0.0;
    if (dist >= targetDist-(range/2) && dist <= targetDist+(range/2)) {
        blend = 1.0-(time/5.0);
    }
    fragColor = mix(texture(DiffuseSampler, texCoord), vec4(color, 1), blend);
}