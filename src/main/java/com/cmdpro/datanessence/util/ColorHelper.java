package com.cmdpro.datanessence.util;

import org.joml.Math;

import java.awt.*;

public class ColorHelper {

    /**
     * Takes two colors, and blends them together.
     * @param color1 the first color
     * @param color2 the second color
     * @param blend the blend factor
     * @return the blended color
     */
    public static Color blendColors(Color color1, Color color2, float blend) {
        return new Color(
                Math.lerp(color1.getRed()/255f, color2.getRed()/255f, blend),
                Math.lerp(color1.getGreen()/255f, color2.getGreen()/255f, blend),
                Math.lerp(color1.getBlue()/255f, color2.getBlue()/255f, blend),
                Math.lerp(color1.getAlpha()/255f, color2.getAlpha()/255f, blend)
        );
    }
}
