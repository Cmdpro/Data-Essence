package com.cmdpro.datanessence.api.util;

import net.minecraft.network.chat.Component;

import java.text.DecimalFormat;

public class TextUtil {
    public static Component getFluidText(int amount) {
        float endAmount = amount;
        String type = "mb";
        if (endAmount >= 1000f) {
            endAmount /= 1000f;
            type = "b";
            if (endAmount >= 1000f) {
                endAmount /= 1000f;
                type = "kb";
            }
        }
        String num = String.valueOf(Math.round(endAmount));
        if (!type.equals("mb")) {
            DecimalFormat format = new DecimalFormat("0.0##");
            num = format.format(endAmount);
        }
        return Component.translatable("datanessence.fluid_display." + type, num);
    }
}
