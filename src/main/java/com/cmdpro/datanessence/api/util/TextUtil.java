package com.cmdpro.datanessence.api.util;

import com.ibm.icu.lang.UCharacter;
import net.minecraft.client.resources.language.FormattedBidiReorder;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.SubStringSource;
import net.minecraft.util.FormattedCharSequence;

import java.text.DecimalFormat;

public class TextUtil {
    public static Component getFluidText(int amount) {
        float endAmount = amount;
        String type = "millibucket";
        if (endAmount >= 1000f) {
            endAmount /= 1000f;
            type = "bucket";
            if (endAmount >= 1000f) {
                endAmount /= 1000f;
                type = "kilobucket";
            }
            if (endAmount >= 1000f) {
                endAmount /= 1000f;
                type = "megabucket";
            }
        }
        String num = String.valueOf(Math.round(endAmount));
        if (!type.equals("millibucket")) {
            DecimalFormat format = new DecimalFormat("0.0##");
            num = format.format(endAmount);
        }
        return Component.translatable("datanessence.fluid_display." + type, num);
    }
}
