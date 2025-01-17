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
        fluidPointTransferValue = buildInteger(builder, "fluidNodeTransfer", "all", 50, 1, 1000000, "How much should a fluid node transfer?");
        essencePointTransferValue = buildInteger(builder, "essenceNodeTransfer", "all", 50, 1, 1000000, "How much should an essence node transfer? (Applies to all essence node types)");
        itemPointTransferValue = buildInteger(builder, "itemNodeTransfer", "all", 4, 1, 64, "How much should an item node transfer?");
        essenceBatteryMaxValue = buildInteger(builder, "essenceBatteryMax", "all", 10000, 1, 1000000, "How much should an essence battery hold? (Applies to all essence battery types)");
        maxNodeWiresValue = buildInteger(builder, "maxNodeWires", "all", 4, 1, 50, "How much wires should nodes be able to have? (Applies to all nodes)");
        lowValuePlantEssenceValue = buildDouble(builder, "lowValuePlantEssence", "all", 25, 0, 1000, "How much should a plant with the low_essence_plant tag give in the Industrial Plant Siphon?");
        mediumValuePlantEssenceValue = buildDouble(builder, "mediumValuePlantEssence", "all", 50, 0, 1000, "How much should a plant with the low_essence_plant tag give in the Industrial Plant Siphon?");
        highValuePlantEssenceValue = buildDouble(builder, "highValuePlantEssence", "all", 100, 0, 1000, "How much a plant with the low_essence_plant tag give in the Industrial Plant Siphon?");
    }
    private static ModConfigSpec.BooleanValue buildBoolean(ModConfigSpec.Builder builder, String name, String category, boolean defaultValue, String comment) {
        return builder.comment(comment).translation(name).define(name, defaultValue);
    }
    private static ModConfigSpec.IntValue buildInteger(ModConfigSpec.Builder builder, String name, String category, int defaultValue, int min, int max, String comment) {
        return builder.comment(comment).translation(name).defineInRange(name, defaultValue, min, max);
    }
    private static ModConfigSpec.DoubleValue buildDouble(ModConfigSpec.Builder builder, String name, String category, double defaultValue, double min, double max, String comment) {
        return builder.comment(comment).translation(name).defineInRange(name, defaultValue, min, max);
    }
    public static int fluidPointTransfer = 50;
    public static int essencePointTransfer = 50;
    public static int itemPointTransfer = 4;
    public static int essenceBatteryMax = 50;
    public static int maxNodeWires = 4;
    public static float lowValuePlantEssence = 25;
    public static float mediumValuePlantEssence = 50;
    public static float highValuePlantEssence = 100;
    public final ModConfigSpec.IntValue fluidPointTransferValue;
    public final ModConfigSpec.IntValue essencePointTransferValue;
    public final ModConfigSpec.IntValue itemPointTransferValue;
    public final ModConfigSpec.IntValue essenceBatteryMaxValue;
    public final ModConfigSpec.IntValue maxNodeWiresValue;
    public final ModConfigSpec.DoubleValue lowValuePlantEssenceValue;
    public final ModConfigSpec.DoubleValue mediumValuePlantEssenceValue;
    public final ModConfigSpec.DoubleValue highValuePlantEssenceValue;
    public static void bake(ModConfig config) {
        try {
            fluidPointTransfer = COMMON.fluidPointTransferValue.get();
            essencePointTransfer = COMMON.essencePointTransferValue.get();
            itemPointTransfer = COMMON.itemPointTransferValue.get();
            essenceBatteryMax = COMMON.essenceBatteryMaxValue.get();
            maxNodeWires = COMMON.maxNodeWiresValue.get();
            lowValuePlantEssence = COMMON.lowValuePlantEssenceValue.get().floatValue();
            mediumValuePlantEssence = COMMON.mediumValuePlantEssenceValue.get().floatValue();
            highValuePlantEssence = COMMON.highValuePlantEssenceValue.get().floatValue();
        } catch (Exception e) {
            DataNEssence.LOGGER.warn("Failed to load DataNEssence config");
            e.printStackTrace();
        }
    }
}
