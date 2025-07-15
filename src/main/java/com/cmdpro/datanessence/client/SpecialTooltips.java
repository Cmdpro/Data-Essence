package com.cmdpro.datanessence.client;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.essence.EssenceType;
import com.cmdpro.datanessence.datamaps.DataNEssenceDatamaps;
import com.cmdpro.datanessence.datamaps.PlantSiphonEssenceMap;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.cmdpro.datanessence.screen.IndustrialPlantSiphonScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@EventBusSubscriber(value = Dist.CLIENT, modid = DataNEssence.MOD_ID)
public class SpecialTooltips {
    public static final Function<ItemTooltipEvent, Boolean> PLANT_SIPHON_CONDITION = (event) ->
            event.getItemStack().getItemHolder().getData(DataNEssenceDatamaps.PLANT_SIPHON_ESSENCE) != null
            && Minecraft.getInstance().screen instanceof IndustrialPlantSiphonScreen;
    private static final HashMap<Function<ItemTooltipEvent, Boolean>, Consumer<ItemTooltipEvent>> TOOLTIPS = new HashMap<>();
    static {
        TOOLTIPS.put(PLANT_SIPHON_CONDITION, (event) -> {
            PlantSiphonEssenceMap map = event.getItemStack().getItemHolder().getData(DataNEssenceDatamaps.PLANT_SIPHON_ESSENCE);
            if (map != null) {
                EssenceType essence = EssenceTypeRegistry.ESSENCE.get();
                Color color = new Color(essence.color);
                Color essenceColor = new Color((int)(color.getRed()), (int)(color.getGreen()), (int)(color.getBlue()), color.getAlpha());
                Color numberColor = new Color((int)(color.getRed()), (int)(color.getGreen()), (int)(color.getBlue()), color.getAlpha());
                Color textColor = new Color((int)(color.getRed()), (int)(color.getGreen()), (int)(color.getBlue()), color.getAlpha());
                DecimalFormat format = new DecimalFormat("#.##");
                event.getToolTip().add(Component.translatable("tooltip.datanessence.plant_siphon_display.text").withStyle(ChatFormatting.WHITE, ChatFormatting.ITALIC));
                event.getToolTip().add(Component.translatable("tooltip.datanessence.plant_siphon_display.values", Component.literal(format.format(map.amountPerTick())).withColor(numberColor.getRGB()), Component.literal(String.valueOf(map.ticks())).withColor(numberColor.getRGB()), essence.name.copy().withColor(essenceColor.getRGB())).withColor(textColor.getRGB()));
            }
        });
    }
    @SubscribeEvent
    public static void onTooltipEvent(ItemTooltipEvent event) {
        for (var i : TOOLTIPS.entrySet()) {
            if (i.getKey().apply(event)) {
                i.getValue().accept(event);
            }
        }
    }
}
