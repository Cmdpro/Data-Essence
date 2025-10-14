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
        builder.push("balancing").comment("Values able to be adjusted to fit your modpack's balance, or personal tastes.");
        fluidPointTransferValue = buildInteger(builder, "fluidNodeTransfer", 125, 1, 1000000, "The base, un-upgraded rate that Fluid Nodes can transfer, in mB per tick. (Also applies to Chemical Nodes if Mekanism is installed)"); // 125mB should cap out at 2B once 16x speed upgrade exists
        essencePointTransferValue = buildInteger(builder, "essenceNodeTransfer", 50, 1, 1000000, "The base, un-upgraded rate that all Essence Nodes can transfer per tick.");
        itemPointTransferValue = buildInteger(builder, "itemNodeTransfer", 4, 1, 64, "The base, un-upgraded rate that Item Nodes can transfer, in items per tick.");
        essenceBatteryMaxValue = buildInteger(builder, "essenceBatteryMax", 10000, 1, 1000000, "The capacity for all Essence Batteries.");
        maxNodeWiresValue = buildInteger(builder, "maxNodeWires", 4, 1, 50, "Connection limit for all Nodes - a Node cannot have more Wires than this.");
        wireDistanceLimitValue = buildInteger(builder, "wireDistanceLimit", 24, 1, 64, "Distance limit for wire length, in blocks.");
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

    public static int fluidPointTransfer = 125;
    public static int essencePointTransfer = 50;
    public static int itemPointTransfer = 4;
    public static int essenceBatteryMax = 50;
    public static int maxNodeWires = 4;
    public static int wireDistanceLimit = 24;
    public final ModConfigSpec.IntValue fluidPointTransferValue;
    public final ModConfigSpec.IntValue essencePointTransferValue;
    public final ModConfigSpec.IntValue itemPointTransferValue;
    public final ModConfigSpec.IntValue essenceBatteryMaxValue;
    public final ModConfigSpec.IntValue maxNodeWiresValue;
    public final ModConfigSpec.IntValue wireDistanceLimitValue;

    public static void bake(ModConfig config) {
        try {
            fluidPointTransfer = COMMON.fluidPointTransferValue.get();
            essencePointTransfer = COMMON.essencePointTransferValue.get();
            itemPointTransfer = COMMON.itemPointTransferValue.get();
            essenceBatteryMax = COMMON.essenceBatteryMaxValue.get();
            maxNodeWires = COMMON.maxNodeWiresValue.get();
            wireDistanceLimit = COMMON.wireDistanceLimitValue.get();
        } catch (Exception e) {
            DataNEssence.LOGGER.warn("[DATANESSENCE] Failed to load config!");
            e.printStackTrace();
        }
    }
}
