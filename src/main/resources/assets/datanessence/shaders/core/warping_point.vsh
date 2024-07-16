#version 150

#moj_import <light.glsl>
#moj_import <fog.glsl>
#moj_import <projection.glsl>

in vec3 Position;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 ChunkOffset;
uniform int FogShape;

uniform mat4 InvViewMat;

out vec4 screenUV;

void main() {
    vec3 pos = Position + ChunkOffset;
    vec4 clipSpacePosition = ProjMat * ModelViewMat * vec4(pos, 1.0);
    gl_Position = clipSpacePosition;
    screenUV = projection_from_position(clipSpacePosition);
}
