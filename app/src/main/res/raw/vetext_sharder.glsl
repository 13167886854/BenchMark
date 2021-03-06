attribute vec4 aPosition;//顶点位置
attribute vec4 aTexCoord;//S T 纹理坐标opengles显示黑白的
varying vec2 vTexCoord;
uniform mat4 uMatrix;
uniform mat4 uSTMatrix;
void main() {
    vTexCoord = (uSTMatrix * aTexCoord).xy;
    gl_Position = uMatrix*aPosition;
}
