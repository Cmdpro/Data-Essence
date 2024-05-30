package com.cmdpro.datanessence.config;

import com.cmdpro.datanessence.DataNEssence;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class DataNEssenceConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final DataNEssenceConfig COMMON;

    static {
        {
            final Pair<DataNEssenceConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(DataNEssenceConfig::new);
            COMMON = specPair.getLeft();
            COMMON_SPEC = specPair.getRight();
        }
    }
    public DataNEssenceConfig(ForgeConfigSpec.Builder builder) {
        builder.push("balancing");
        fluidPointTransferValue = buildInteger(builder, "fluidPointTransfer", "all", 50, 1, 1000000, "How much should a fluid point transfer?");
        essencePointTransferValue = buildInteger(builder, "essencePointTransfer", "all", 50, 1, 1000000, "How much should an essence point transfer? (Applies to all essence point types)");
        itemPointTransferValue = buildInteger(builder, "itemPointTransfer", "all", 4, 1, 64, "How much should an item point transfer?");
    }
    private static ForgeConfigSpec.BooleanValue buildBoolean(ForgeConfigSpec.Builder builder, String name, String category, boolean defaultValue, String comment) {
        return builder.comment(comment).translation(name).define(name, defaultValue);
    }
    private static ForgeConfigSpec.IntValue buildInteger(ForgeConfigSpec.Builder builder, String name, String category, int defaultValue, int min, int max, String comment) {
        return builder.comment(comment).translation(name).defineInRange(name, defaultValue, min, max);
    }
    public static int fluidPointTransfer = 50;
    public static int essencePointTransfer = 50;
    public static int itemPointTransfer = 4;
    public final ForgeConfigSpec.IntValue fluidPointTransferValue;
    public final ForgeConfigSpec.IntValue essencePointTransferValue;
    public final ForgeConfigSpec.IntValue itemPointTransferValue;
    public static void bake(ModConfig config) {
        try {
            fluidPointTransfer = COMMON.fluidPointTransferValue.get();
            essencePointTransfer = COMMON.essencePointTransferValue.get();
            itemPointTransfer = COMMON.itemPointTransferValue.get();
        } catch (Exception e) {
            DataNEssence.LOGGER.warn("Failed to load DataNEssence config");
            e.printStackTrace();
        }
    }
}
