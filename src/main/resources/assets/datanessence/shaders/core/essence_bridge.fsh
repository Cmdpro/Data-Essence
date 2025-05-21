#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform float Time;
uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in vec2 texCoord0;
in vec3 faceDir;
in vec4 vertexColor;
in float vertexDistance;

out vec4 fragColor;

void main() {
    vec2 uv = texCoord0;
    vec4 color = vertexColor;
    if (faceDir.y <= 0.1 && faceDir.y >= -0.1) {
        float alpha = 0.0;
        float direction = 1.0;
        for (int i = 0; i < 6; i++) {
            float initialShift = 0.1;
            float shift = initialShift;
            float timeShift = ((Time*(360.0-(float(i)*22.5)))*direction);
            float density = (12.0*(1.0+(1.0-((1.0/5.0)*float(i)))));
            float xShift = (uv.x*360.0*density);
            float indexShift = (float(i)*125.0);
            float sine = sin(radians(indexShift+xShift+timeShift));
            float sinePositive = ((sine+1.0)/2.0);
            float scale = ((float(i)+1.0)*0.025);
            scale *= ((sin(radians(indexShift+timeShift))+1.0)/2.0);
            shift += sinePositive*scale;
            float intensity = ((1.0/6.0)*(float(i)+1.0));
            if (uv.y > shift) {
                alpha = max((1.0-clamp(((uv.y-shift)/(0.05)), 0.0, 1.0))*intensity, alpha);
            } else {
                alpha = max(intensity, alpha);
            }
            direction = -direction;
        }
        alpha = clamp(alpha, 0.0, 1.0);
        color = vec4(color.rgb, alpha*0.8*color.a);
    } else if (faceDir.y >= 0.1) {
        color = vec4(color.rgb, 0.8*color.a);
    } else {
        color = vec4(0.0, 0.0, 0.0, 0.0);
    }
    //color = vec4(mix(vec3(0.0, 0.0, 0.0), color.rgb, color.a), 1.0);
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
