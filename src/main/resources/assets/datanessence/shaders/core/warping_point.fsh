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

void main()
{
    /*
    vec2 uv = gl_FragCoord.xy/ScreenSize;
    vec2 texC = uv;
    vec4 clipSpacePosition = ProjMat * (ModelViewMat * vec4(ObjectPosition-CameraPosition, 1));
    clipSpacePosition /= clipSpacePosition.w;
    vec2 lpos = ((clipSpacePosition.xy + 1.0) / 2.0);
    vec2 texC2 = uv.xy;
    texC = mix(texC2, (texC * 2.0 - lpos * 2.0) * 0.0 * 0.5 + lpos, (1.0 / (distance((texC2 * 2.0 - lpos * 2.0) * 10 * 0.5 + lpos, lpos) - 1.0)));
    vec3 getColor = getTexture(texC);
    getColor *= clamp(pow(distance(texC2, vec2(lpos)),8.8) * 300000000.0,0.0,1.0);
    fragColor = vec4(getColor,1.0);
    */
    vec2 coord = gl_FragCoord.xy/ScreenSize;
    vec2 coord2 = vec2(gl_FragCoord.x, ScreenSize.y-gl_FragCoord.y)/ScreenSize;

    vec3 color = getTexture(coord);
    float sphereWidth = 4;
    float insideDivide = 4;
    float hit;
    vec3 hitPosition, hitNormal;
    vec3 pos = worldPos(distance(CameraPosition, ObjectPosition)+(sphereWidth/2), coord2, inverse(ProjMat), inverse(ModelViewMat), CameraPosition);
    Raycast_float(CameraPosition, normalize(CameraPosition-pos), ObjectPosition, sphereWidth/insideDivide, hit, hitPosition, hitNormal);

    fragColor = vec4(1.0-hit, 1.0-hit, 1.0-hit, 1);
}