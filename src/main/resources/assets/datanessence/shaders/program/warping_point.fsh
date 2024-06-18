#version 150


uniform sampler2D DiffuseSampler;
uniform vec3 CameraPosition;
uniform vec2 InSize;
uniform mat4x4 ProjMat;
uniform vec3 Point;
uniform mat4x4 ModelViewMat;
uniform vec2 ScreenSize;

in vec2 texCoord;
in vec2 oneTexel;
out vec4 fragColor;

float distanceTo(vec3 pointA, vec3 pointB) {
    float d0 = pointA.x - pointB.x;
    float d1 = pointA.y - pointB.y;
    float d2 = pointA.z - pointB.z;
    return sqrt(d0 * d0 + d1 * d1 + d2 * d2);
}
vec3 getTexture(vec2 coord){
    return texture(DiffuseSampler,coord.st).rgb;
}
vec2 worldToScreen(vec3 worldPos) {
    vec4 clipSpacePosition = ProjMat * (ModelViewMat * vec4(worldPos, 1));
    clipSpacePosition /= clipSpacePosition.w;
    return ((clipSpacePosition.xy + 1.0) / 2.0);
}
void main()
{
    vec2 uv = texCoord.xy;

    vec2 texC = uv;

    vec2 lpos = worldToScreen(Point);

    vec2 texC2 = texCoord.xy;

    float dist = distanceTo(CameraPosition, Point);

    texC = mix(texC2, (texC * 2.0 - lpos * 2.0) * 0.0 * 0.5 + lpos, (1.0 / (distance((texC2 * 2.0 - lpos * 2.0) * (10*(dist*2)) * 0.5 + lpos, lpos) - 1.0)));

    vec3 getColor = getTexture(texC);

    getColor *= clamp(pow(distance(texC2, vec2(lpos)),8.8/dist) * 300000000.0,0.0,1.0);

    fragColor = vec4(getColor,1.0);
}
