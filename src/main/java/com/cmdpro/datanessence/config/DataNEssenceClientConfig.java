package com.cmdpro.datanessence.config;

import com.cmdpro.datanessence.DataNEssence;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class DataNEssenceClientConfig {
    public static final ModConfigSpec CLIENT_SPEC;
    public static final DataNEssenceClientConfig CLIENT;
    static {
        {
            final Pair<DataNEssenceClientConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(DataNEssenceClientConfig::new);
            CLIENT = specPair.getLeft();
            CLIENT_SPEC = specPair.getRight();
        }
    }
    public DataNEssenceClientConfig(ModConfigSpec.Builder builder) {
        builder.push("shaders");
        genderEuphoriaShaderValue = buildBoolean(builder, "genderEuphoriaShader", true, "Should the gender euphoria shader be enabled?");
        pingShaderValue = buildBoolean(builder, "pingShader", true, "Should the signal tracker ping shader be enabled?");
        progressionShaderValue = buildBoolean(builder, "progressionShader", true, "Should the progression shader be enabled?");
        builder.pop();
        builder.push("accessibility");
        colorAssistValue = buildBoolean(builder, "colorAssist", false, "Whether to enable color-assist mode. When on, various tweaks to color-dependent puzzles will be made to hopefully help in their completion, or in the case of one puzzle, will be outright bypassed.");
        builder.pop();
    }
    private static ModConfigSpec.BooleanValue buildBoolean(ModConfigSpec.Builder builder, String name, boolean defaultValue, String comment) {
        return builder.comment(comment).translation(name).define(name, defaultValue);
    }
    private static ModConfigSpec.IntValue buildInteger(ModConfigSpec.Builder builder, String name, int defaultValue, int min, int max, String comment) {
        return builder.comment(comment).translation(name).defineInRange(name, defaultValue, min, max);
    }
    private static ModConfigSpec.DoubleValue buildDouble(ModConfigSpec.Builder builder, String name, double defaultValue, double min, double max, String comment) {
        return builder.comment(comment).translation(name).defineInRange(name, defaultValue, min, max);
    }
    public static boolean genderEuphoriaShader = true;
    public static boolean pingShader = true;
    public static boolean progressionShader = true;
    public static boolean colorAssist = false;
    public final ModConfigSpec.BooleanValue genderEuphoriaShaderValue;
    public final ModConfigSpec.BooleanValue pingShaderValue;
    public final ModConfigSpec.BooleanValue progressionShaderValue;
    public final ModConfigSpec.BooleanValue colorAssistValue;
    public static void bake(ModConfig config) {
        try {
            genderEuphoriaShader = CLIENT.genderEuphoriaShaderValue.get();
            pingShader = CLIENT.pingShaderValue.get();
            progressionShader = CLIENT.progressionShaderValue.get();
            colorAssist = CLIENT.colorAssistValue.get();
        } catch (Exception e) {
            DataNEssence.LOGGER.warn("[DATANESSENCE] Failed to load client config!");
            e.printStackTrace();
        }
    }
}
