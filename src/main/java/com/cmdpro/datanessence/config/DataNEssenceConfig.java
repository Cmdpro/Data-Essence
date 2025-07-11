package com.cmdpro.datanessence.config;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.generation.IndustrialPlantSiphonBlockEntity;
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
        fluidPointTransferValue = buildInteger(builder, "fluidNodeTransfer", "all", 125, 1, 1000000, "How much should a fluid node transfer? (Also applies to chemical nodes if Mekanism is installed)"); // 125mB should cap out at 2B once 16x speed upgrade exists
        essencePointTransferValue = buildInteger(builder, "essenceNodeTransfer", "all", 50, 1, 1000000, "How much should an essence node transfer? (Applies to all essence node types)");
        itemPointTransferValue = buildInteger(builder, "itemNodeTransfer", "all", 4, 1, 64, "How much should an item node transfer?");
        essenceBatteryMaxValue = buildInteger(builder, "essenceBatteryMax", "all", 10000, 1, 1000000, "How much should an essence battery hold? (Applies to all essence battery types)");
        maxNodeWiresValue = buildInteger(builder, "maxNodeWires", "all", 4, 1, 50, "How much wires should nodes be able to have? (Applies to all nodes)");
        lowValuePlantEssenceValue = buildDouble(builder, "lowValuePlantEssence", "all", 10, 0, 1000, "How many ticks should a plant with the low_essence_plant tag last in the Industrial Plant Siphon? (total Essence produced is "+ IndustrialPlantSiphonBlockEntity.essenceProducedFromLowValuePlants +"x this value)");
        mediumValuePlantEssenceValue = buildDouble(builder, "mediumValuePlantEssence", "all", 50, 0, 1000, "How many ticks should a plant with the medium_essence_plant tag last in the Industrial Plant Siphon? (total Essence produced is "+ IndustrialPlantSiphonBlockEntity.essenceProducedFromMediumValuePlants +"x this value)");
        highValuePlantEssenceValue = buildDouble(builder, "highValuePlantEssence", "all", 100, 0, 1000, "How many ticks should a plant with the high_essence_plant tag last in the Industrial Plant Siphon? (total Essence produced is "+ IndustrialPlantSiphonBlockEntity.essenceProducedFromHighValuePlants +"x this value)");
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
    public static int fluidPointTransfer = 125;
    public static int essencePointTransfer = 50;
    public static int itemPointTransfer = 4;
    public static int essenceBatteryMax = 50;
    public static int maxNodeWires = 4;
    public static float lowValuePlantEssence = 10;
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
            DataNEssence.LOGGER.warn("[DATANESSENCE] Failed to load config!");
            e.printStackTrace();
        }
    }
}
