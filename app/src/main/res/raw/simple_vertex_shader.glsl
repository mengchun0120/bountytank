uniform bool u_RelativeToViewport;
uniform vec2 u_ViewportOrigin;
uniform vec2 u_ObjRef;
uniform mat4 u_ProjectionMatrix;

attribute vec2 a_Position;
attribute vec4 a_Color;

varying vec4 v_Color;

void main() {
    v_Color = a_Color;

    vec2 pos = a_Position + u_ObjRef;
    if(u_RelativeToViewport) {
        pos -= u_ViewportOrigin;
    }

    gl_Position = u_ProjectionMatrix * vec4(pos, 0.0, 1.0);
}