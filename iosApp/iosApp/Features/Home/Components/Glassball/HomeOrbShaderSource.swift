import simd
import SpriteKit
import SwiftUI
import UIKit

enum HomeOrbShaderUniform {
    static let frontDepth = "u_frontDepth"
    static let edgeFalloff = "u_edgeFalloff"
    static let hardLightAmount = "u_hardLightAmount"
    static let orbProgressX = "u_orbProgressX"
    static let positiveShare = "u_positiveShare"
    static let positiveTint = "u_positiveTint"
    static let transitionWidth = "u_transitionWidth"
    static let warningTint = "u_warningTint"
    static let shadowAmount = "u_shadowAmount"
}

enum HomeOrbShaderSource {
    static let depth = """
    uniform float u_frontDepth;
    uniform float u_edgeFalloff;
    uniform float u_hardLightAmount;
    uniform float u_orbProgressX;
    uniform float u_positiveShare;
    uniform vec3 u_positiveTint;
    uniform float u_transitionWidth;
    uniform vec3 u_warningTint;
    uniform float u_shadowAmount;

    vec3 hardLight(vec3 base, vec3 blend) {
        vec3 multiplyBranch = 2.0 * base * blend;
        vec3 screenBranch = 1.0 - 2.0 * (1.0 - base) * (1.0 - blend);
        return mix(multiplyBranch, screenBranch, step(vec3(0.5), blend));
    }

    void main() {
        vec4 color = SKDefaultShading();
        vec2 centered = v_tex_coord - vec2(0.5, 0.5);
        float spriteEdge = smoothstep(0.30, 0.78, length(centered));
        float warningMix = smoothstep(
            max(0.0, u_positiveShare - u_transitionWidth),
            min(1.0, u_positiveShare + u_transitionWidth),
            u_orbProgressX
        );
        warningMix = mix(warningMix, 0.0, step(0.999, u_positiveShare));
        warningMix = mix(warningMix, 1.0, step(u_positiveShare, 0.001));
        vec3 orbBase = mix(u_positiveTint, u_warningTint, warningMix);
        vec3 hardLightColor = hardLight(orbBase, color.rgb);
        float depthLight = mix(0.68, 1.08, u_frontDepth);
        float rimShade = 1.0 - spriteEdge * mix(0.16, 0.34, u_edgeFalloff);
        color.rgb = mix(color.rgb, hardLightColor, u_hardLightAmount);
        color.rgb *= depthLight * rimShade * (1.0 - u_shadowAmount);
        color.a *= mix(0.72, 1.0, u_frontDepth) * (1.0 - spriteEdge * 0.16);
        gl_FragColor = color;
    }
    """

    static func makeDepthShader(
        positiveTint: Color = OBRitColors.backgroundPositiveDefault,
        warningTint: Color = OBRitColors.backgroundWarningDefault
    ) -> SKShader {
        let shader = SKShader(source: depth)
        shader.uniforms = [
            SKUniform(name: HomeOrbShaderUniform.frontDepth, float: 1),
            SKUniform(name: HomeOrbShaderUniform.edgeFalloff, float: 0),
            SKUniform(name: HomeOrbShaderUniform.hardLightAmount, float: 0.62),
            SKUniform(name: HomeOrbShaderUniform.orbProgressX, float: 0.5),
            SKUniform(name: HomeOrbShaderUniform.positiveShare, float: 0.62),
            SKUniform(name: HomeOrbShaderUniform.positiveTint, vectorFloat3: positiveTint.shaderRGB),
            SKUniform(name: HomeOrbShaderUniform.transitionWidth, float: Float(HomeOrbVisualConfig.gradientTransitionWidth)),
            SKUniform(name: HomeOrbShaderUniform.warningTint, vectorFloat3: warningTint.shaderRGB),
            SKUniform(name: HomeOrbShaderUniform.shadowAmount, float: 0)
        ]
        return shader
    }
}

private extension Color {
    var shaderRGB: vector_float3 {
        UIColor(self).shaderRGB
    }
}

private extension UIColor {
    var shaderRGB: vector_float3 {
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        var alpha: CGFloat = 0

        guard getRed(&red, green: &green, blue: &blue, alpha: &alpha) else {
            return vector_float3(1, 1, 1)
        }

        return vector_float3(Float(red), Float(green), Float(blue))
    }
}
