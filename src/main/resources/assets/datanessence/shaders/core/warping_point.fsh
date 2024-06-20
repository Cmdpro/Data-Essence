#version 150

uniform sampler2D DepthBuffer;
uniform sampler2D ColorBuffer;
uniform mat4 ProjMat;
uniform mat4 ModelViewMat;
uniform mat4 ViewMat;
uniform vec2 ScreenSize;
uniform vec3 lookVector;
uniform vec3 CameraPosition;
uniform vec3 ObjectPosition;
uniform float FOV;

in vec4 screenUV;
out vec4 fragColor;

vec3 getTexture(vec2 coord){
    return texture(ColorBuffer, coord).rgb;
}
void Raycast_float(vec3 RayOrigin, vec3 RayDirection, vec3 SphereOrigin, float SphereSize, out float Hit, out vec3 HitPosition, out vec3 HitNormal) {
    HitPosition = vec3(0.0, 0.0, 0.0);
    HitNormal = vec3(0.0, 0.0, 0.0);

    float t = 0.0;
    vec3 L = SphereOrigin - RayOrigin;
    float tca = dot(L, -RayDirection);

    if (tca < 0)
    {
        Hit = 0.0;
        return;
    }

    float d2 = dot(L, L) - tca * tca;
    float radius2 = SphereSize * SphereSize;

    if (d2 > radius2)
    {
        Hit = 0.0;
        return;
    }

    float thc = sqrt(radius2 - d2);
    t = tca - thc;

    Hit = 1.0;
    HitPosition = RayOrigin - RayDirection * t;
    HitNormal = normalize(HitPosition - SphereOrigin);
}
vec3 worldPos(float z, vec2 texCoord, mat4 invProjMat, mat4 invViewMat, vec3 cameraPos) {
    vec4 clipSpacePosition = vec4(texCoord * 2.0 - 1.0, z, 1.0);
    vec4 viewSpacePosition = invProjMat * clipSpacePosition;
    viewSpacePosition /= viewSpacePosition.w;
    vec4 localSpacePosition = invViewMat * viewSpacePosition;
    return cameraPos + localSpacePosition.xyz;
}
vec2 worldToScreen(vec3 worldPos, mat4 projMat, mat4 viewMat) {
    vec4 clipSpacePosition = projMat * (viewMat * vec4(worldPos, 1.0));
    clipSpacePosition /= clipSpacePosition.w;
    return (vec2(clipSpacePosition.x, 1.0-clipSpacePosition.y) + 1.0) / 2.0;
}

void main()
{
    vec2 coord = gl_FragCoord.xy/ScreenSize;
    vec3 point = worldPos(texture(DepthBuffer, coord).r, coord, inverse(ProjMat), inverse(ViewMat), CameraPosition);
    float sphereWidth = 4;
    float insideDivide = 4;
    float hit;
    vec3 hitPosition, hitNormal;
    vec3 pos = worldPos(distance(CameraPosition, ObjectPosition), coord, inverse(ProjMat), inverse(ViewMat), CameraPosition);
    Raycast_float(CameraPosition, normalize(pos-CameraPosition), ObjectPosition, sphereWidth/insideDivide, hit, hitPosition, hitNormal);
    float hit2;
    vec3 hitPosition2, hitNormal2;
    Raycast_float(CameraPosition, normalize(pos-CameraPosition), ObjectPosition, sphereWidth, hit2, hitPosition2, hitNormal2);
    vec2 objScreen = worldToScreen(ObjectPosition-CameraPosition, ProjMat, ViewMat);
    vec2 posScreen = worldToScreen(pos-CameraPosition, ProjMat, ViewMat);
    vec2 distortion = normalize(objScreen-posScreen)*0.2;
    float distortion2 = 1-(acos(clamp(dot(normalize(hitNormal2), normalize(CameraPosition-ObjectPosition)), 0, 1)));
    float distortion3 = sphereWidth/(tan(radians(degrees(FOV)*0.5)) * (distance(ObjectPosition, CameraPosition)*2));
    vec3 color = getTexture(coord+(distortion2*distortion*distortion3));
    fragColor = vec4(mix(color, vec3(0, 0, 0), hit), 1);
}