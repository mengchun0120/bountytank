precision mediump float;

uniform bool u_UseFixedColor;
uniform vec4 u_FixedColor;

varying vec4 v_Color;

void main() {
    if(u_UseFixedColor) {
        gl_FragColor = u_FixedColor;
    } else {
        gl_FragColor = v_Color;
    }
}