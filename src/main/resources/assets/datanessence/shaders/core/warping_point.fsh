#version 150

uniform sampler2D DepthBuffer;
uniform sampler2D ColorBuffer;
uniform vec2 InSize;
uniform mat4x4 ProjMat;
uniform mat4x4 ModelViewMat;
uniform vec2 ScreenSize;
uniform vec3 lookVector;
uniform vec3 CameraPosition;
uniform vec3 ObjectPosition;

in vec2 texCoord;
in vec4 screenUV;
out vec4 fragColor;

vec3 getTexture(vec4 coord){
    return textureProj(ColorBuffer, coord).rgb;
}

void main()
{
    vec2 uv = screenUV.xy;
    vec2 texC = uv;
    vec4 clipSpacePosition = ProjMat * (ModelViewMat * vec4(ObjectPosition-CameraPosition, 1));
    clipSpacePosition /= clipSpacePosition.w;
    vec2 lpos = ((clipSpacePosition.xy + 1.0) / 2.0);
    vec2 texC2 = screenUV.xy;
    texC = mix(texC2, (texC * 2.0 - lpos * 2.0) * 0.0 * 0.5 + lpos, (1.0 / (distance((texC2 * 2.0 - lpos * 2.0) * 10 * 0.5 + lpos, lpos) - 1.0)));
    vec3 getColor = getTexture(vec4(texC.x, texC.y, screenUV.z, screenUV.w));
    getColor *= clamp(pow(distance(texC2, vec2(lpos)),8.8) * 300000000.0,0.0,1.0);
    fragColor = vec4(getColor,1.0);
}