#version 150

uniform sampler2D DepthBuffer;
uniform sampler2D ColorBuffer;
uniform mat4 ProjMat;
uniform mat4 ModelViewMatrix;
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
vec2 MirrorUVCoordinates_float(vec2 UVs)
{
    vec2 NewUVs = UVs;
    // Mirror U coordinate
    if (UVs.x < 0.0 || UVs.x > 1.0) {
        NewUVs.x = 1.0 - abs((UVs.x - 2.0 * floor(UVs.x / 2.0)) - 1.0);
    }

    // Mirror V coordinate
    if (UVs.y < 0.0 || UVs.y > 1.0) {
        NewUVs.y = 1.0 - abs((UVs.y - 2.0 * floor(UVs.y / 2.0)) - 1.0);
    }
    return NewUVs;
}
void main()
{
    float distortionExponent = 1;
    float screenSpaceScale = 1;

    vec2 coord = gl_FragCoord.xy/ScreenSize;
    vec3 point = worldPos(texture(DepthBuffer, coord).r, coord, inverse(ProjMat), inverse(ModelViewMatrix), CameraPosition);
    float sphereWidth = 6;
    float insidePercentage = 0.5;
    float hit;
    vec3 hitPosition, hitNormal;
    vec3 pos = worldPos(distance(CameraPosition, ObjectPosition), coord, inverse(ProjMat), inverse(ModelViewMatrix), CameraPosition);
    Raycast_float(CameraPosition, normalize(pos-CameraPosition), ObjectPosition, sphereWidth*insidePercentage, hit, hitPosition, hitNormal);
    float hit2;
    vec3 hitPosition2, hitNormal2;
    Raycast_float(CameraPosition, normalize(pos-CameraPosition), ObjectPosition, sphereWidth, hit2, hitPosition2, hitNormal2);
    vec2 objScreen = worldToScreen(ObjectPosition-CameraPosition, ProjMat, ModelViewMatrix);
    vec2 posScreen = worldToScreen(pos-CameraPosition, ProjMat, ModelViewMatrix);

    vec2 screenRadius = (vec2(sphereWidth, sphereWidth)/(tan(radians(degrees(FOV)*vec2(0.5, 2)))*(2*distance(ObjectPosition, CameraPosition))))*screenSpaceScale; //Radius of sphere in screen space, or at least a close enough approximation
    vec2 vecToCenter = normalize(posScreen-objScreen)*screenRadius; //Screen Space vector towards center
    vec2 distortion = 1-(acos(clamp(dot(hitNormal2, CameraPosition-ObjectPosition), 0, 1))/vec2(1.57, 2)); //Increase distortion towards black hole
    distortion = pow((1-insidePercentage)/distortion, vec2(distortionExponent, distortionExponent)); //Remap so it goes from 0-1 from the edge of the sphere to the edge of the black hole
    distortion = distortion*clamp(dot(normalize(pos-CameraPosition)*-1, normalize(ObjectPosition-CameraPosition)), 0, 1); //Minimize distortion at grazing angles

    vec3 color = getTexture(MirrorUVCoordinates_float(coord+(distortion*vecToCenter)));
    fragColor = vec4(mix(color, vec3(0, 0, 0), hit), 1);
}