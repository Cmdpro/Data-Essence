#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;
uniform float colorOffset;
uniform float fade;
uniform mat4 invViewMat;
uniform mat4 invProjMat;
uniform vec3 CameraPosition;

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

vec3 calculateNormal(vec2 uv0) {
    mat4 invProj = invProjMat;
    vec2 depthDimensions = textureSize(DiffuseDepthSampler, 0);

    vec2 uv1 = uv0 + vec2(1.0, 0.0) / depthDimensions;
    vec2 uv2 = uv0 + vec2(0.0, 1.0) / depthDimensions;

    float depth0 = linearizeDepth(texture2D(DiffuseDepthSampler, uv0).r);
    float depth1 = linearizeDepth(texture2D(DiffuseDepthSampler, uv1).r);
    float depth2 = linearizeDepth(texture2D(DiffuseDepthSampler, uv2).r);

    vec3 p0 = reconstructPosition(uv0, depth0, invProj);
    vec3 p1 = reconstructPosition(uv1, depth1, invProj);
    vec3 p2 = reconstructPosition(uv2, depth2, invProj);

    vec3 normal = normalize(cross(p2-p0, p1-p0));
    return normal;
}
vec3 worldPos(float depth) {
    float z = depth * 2.0 - 1.0;
    vec4 clipSpacePosition = vec4(texCoord * 2.0 - 1.0, z, 1.0);
    vec4 viewSpacePosition = invProjMat * clipSpacePosition;
    viewSpacePosition /= viewSpacePosition.w;
    vec4 worldSpacePosition = invViewMat * viewSpacePosition;

    return CameraPosition + worldSpacePosition.xyz;
}
vec4 sobel() {
    float kernel[9] = float[](1, 2, 1, 0, 0, 0, -1, -2, -1);
    vec2 offset[9] = vec2[](
    vec2(-oneTexel.x, oneTexel.y),
    vec2(0, oneTexel.y),
    vec2(oneTexel.x, oneTexel.y),
    vec2(-oneTexel.x, 0),
    vec2(0, 0),
    vec2(oneTexel.x, 0),
    vec2(-oneTexel.x, -oneTexel.y),
    vec2(0, -oneTexel.y),
    vec2(oneTexel.x, -oneTexel.y)
    );

    float Gx = 0.0;
    float Gy = 0.0;
    for (int i = 0; i < 9; i++) {
        float intensity;
        vec2 coord = texCoord + offset[i];
        coord.x = clamp(coord.x, 0, 1);
        coord.y = clamp(coord.y, 0, 1);
        vec3 sampleVar = calculateNormal(coord);
        intensity = (sampleVar.r + sampleVar.g + sampleVar.b) / 3.0;

        if (i != 4) {
            Gx += intensity * kernel[i];
        }
        int j = (i % 3) * 3 + i / 3;
        if (j != 4) {
            Gy += intensity * kernel[j];
        }
    }
    float G = sqrt(Gx * Gx + Gy * Gy);
    float edgeThreshold = 0.2;
    float alpha = G > edgeThreshold ? 1.0 : 0.0;
    return vec4(G, G, G, alpha);
}
void main() {
    float coord = texCoord.x*20;
    coord += colorOffset;
    while (coord > 1) {
        coord -= 1;
    }
    if (coord > 0.5) {
        coord = 0.5-(coord-0.5);
    }
    vec3 color = mix(vec3(252.0/255.0, 146.0/255.0, 186.0/255.0), vec3(105.0/255.0, 179.0/255.0, 252.0/255.0), coord*2);
    float blend = sobel().a;
    vec3 world = worldPos(texture2D(DiffuseDepthSampler, texCoord).r);
    blend *= 1-clamp(distance(world, CameraPosition)/50, 0, 1);
    float actualFade = sin(radians(fade*(360/5)));
    blend *= (actualFade*0.25)+0.75;
    fragColor = mix(texture2D(DiffuseSampler, texCoord), vec4(color, 1), blend);
}