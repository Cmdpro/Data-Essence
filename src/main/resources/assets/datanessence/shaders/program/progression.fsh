#version 150

uniform sampler2D DiffuseSampler;
uniform float time;

in vec2 texCoord;
in vec2 oneTexel;
out vec4 fragColor;

vec3 rgb2hsv(vec3 c)
{
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main() {
    float levels = 3.0;
    float swapTime = 1.5;
    float maxLevels = 25;
    float saturation = 2.5;
    if (time <= swapTime) {
        levels = (((swapTime-time)/swapTime)*(maxLevels-levels))+levels;
        saturation = ((time/swapTime)*saturation)+1;
    } else {
        float time2 = time-swapTime;
        levels = ((time2/(15-swapTime))*(maxLevels-levels))+levels;
        saturation = ((1-(time2/(15-swapTime)))*saturation)+1;
    }
    vec4 color = texture(DiffuseSampler, texCoord);
    vec4 origColor = color;
    float grayscale = max(color.r, max(color.g, color.b));

    float lower = floor(grayscale * levels) / levels;
    float lowerDiff = abs(grayscale - lower);
    float upper = ceil(grayscale * levels) / levels;
    float upperDiff = abs(upper - grayscale);
    float level = lowerDiff <= upperDiff ? lower : upper;
    float adjustment = level / grayscale;
    color.rgb *= adjustment;
    vec3 hsv = rgb2hsv(color.rgb);
    hsv.g = clamp(hsv.g*saturation, 0, 1);
    color.rgb = hsv2rgb(hsv);
    if (time >= 14.0) {
        fragColor = vec4(mix(color.rgb, origColor.rgb, 1.0-(time-14.0)), 1.0);
    } else {
        fragColor = vec4(color.rgb, 1.0);
    }
}