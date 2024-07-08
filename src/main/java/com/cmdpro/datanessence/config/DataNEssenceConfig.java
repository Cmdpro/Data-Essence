package com.cmdpro.datanessence.config;

import com.cmdpro.datanessence.DataNEssence;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class DataNEssenceConfig {
    public static final ModConfigSpec COMMON_SPEC;
    public static final DataNEssenceConfig COMMON;

    static {
        {
            final Pair<DataNEssenceConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(DataNEssenceConfig::new);
            COMMON = specPair.getLeft();
            COMMON_SPEC = specPair.getRight();
        }
    }
    public DataNEssenceConfig(ModConfigSpec.Builder builder) {
        builder.push("balancing");
        fluidPointTransferValue = buildInteger(builder, "fluidPointTransfer", "all", 50, 1, 1000000, "How much should a fluid point transfer?");
        essencePointTransferValue = buildInteger(builder, "essencePointTransfer", "all", 50, 1, 1000000, "How much should an essence point transfer? (Applies to all essence point types)");
        itemPointTransferValue = buildInteger(builder, "itemPointTransfer", "all", 4, 1, 64, "How much should an item point transfer?");
        essenceBatteryMaxValue = buildInteger(builder, "essenceBatteryMax", "all", 10000, 1, 1000000, "How much should an essence battery hold? (Applies to all essence point types)");
    }
    private static ModConfigSpec.BooleanValue buildBoolean(ModConfigSpec.Builder builder, String name, String category, boolean defaultValue, String comment) {
        return builder.comment(comment).translation(name).define(name, defaultValue);
    }
    private static ModConfigSpec.IntValue buildInteger(ModConfigSpec.Builder builder, String name, String category, int defaultValue, int min, int max, String comment) {
        return builder.comment(comment).translation(name).defineInRange(name, defaultValue, min, max);
    }
    public static int fluidPointTransfer = 50;
    public static int essencePointTransfer = 50;
    public static int itemPointTransfer = 4;
    public static int essenceBatteryMax = 50;
    public final ModConfigSpec.IntValue fluidPointTransferValue;
    public final ModConfigSpec.IntValue essencePointTransferValue;
    public final ModConfigSpec.IntValue itemPointTransferValue;
    public final ModConfigSpec.IntValue essenceBatteryMaxValue;
    public static void bake(ModConfig config) {
        try {
            fluidPointTransfer = COMMON.fluidPointTransferValue.get();
            essencePointTransfer = COMMON.essencePointTransferValue.get();
            itemPointTransfer = COMMON.itemPointTransferValue.get();
            essenceBatteryMax = COMMON.essenceBatteryMaxValue.get();
        } catch (Exception e) {
            DataNEssence.LOGGER.warn("Failed to load DataNEssence config");
            e.printStackTrace();
        }
    }
}
