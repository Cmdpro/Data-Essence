#version 150

uniform sampler2D DiffuseSampler;
uniform float time;

in vec2 texCoord;
in vec2 oneTexel;
out vec4 fragColor;

void main() {
    float levels = 2.0;
    if (time <= 0.5) {
        levels = ((1.0-(time/0.5))*45.0)+2.0;
    } else {
        float time2 = time-0.5;
        levels = ((time2/14.5)*45.0)+2.0;
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
    if (time >= 14.0) {
        fragColor = mix(color, origColor, 1.0-(time-14.0));
    } else {
        fragColor = color;
    }
}