package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.auxiliary.EnticingLureBlockEntity;
import com.cmdpro.datanessence.screen.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MenuRegistry {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, DataNEssence.MOD_ID);

    public static final Supplier<MenuType<FabricatorMenu>> FABRICATOR_MENU = register(FabricatorMenu::new, "fabricator");
    public static final Supplier<MenuType<EssenceBurnerMenu>> ESSENCE_BURNER_MENU = register(EssenceBurnerMenu::new, "essence_burner");
    public static final Supplier<MenuType<InfuserMenu>> INFUSER_MENU = register(InfuserMenu::new, "infuser");
    public static final Supplier<MenuType<SynthesisChamberMenu>> SYNTHESIS_CHAMBER_MENU = register(SynthesisChamberMenu::new, "synthesis_chamber");
    public static final Supplier<MenuType<ChargerMenu>> CHARGER_MENU = register(ChargerMenu::new, "charger");
    public static final Supplier<MenuType<LaserEmitterMenu>> LASER_EMITTER_MENU = register(LaserEmitterMenu::new, "laser_emitter");
    public static final Supplier<MenuType<AutoFabricatorMenu>> AUTO_FABRICATOR_MENU = register(AutoFabricatorMenu::new, "auto_fabricator");
    public static final Supplier<MenuType<FluidBottlerMenu>> FLUID_BOTTLER_MENU = register(FluidBottlerMenu::new, "fluid_bottler");
    public static final Supplier<MenuType<EntropicProcessorMenu>> ENTROPIC_PROCESSOR_MENU = register(EntropicProcessorMenu::new, "entropic_processor");
    public static final Supplier<MenuType<EssenceFurnaceMenu>> ESSENCE_FURNACE_MENU = register(EssenceFurnaceMenu::new, "essence_furnace");
    public static final Supplier<MenuType<FluidMixerMenu>> FLUID_MIXER_MENU = register(FluidMixerMenu::new, "fluid_mixer");
    public static final Supplier<MenuType<EnticingLureMenu>> ENTICING_LURE_MENU = register(EnticingLureMenu::new, "enticing_lure");
    public static final Supplier<MenuType<MineralPurificationChamberMenu>> MINERAL_PURIFICATION_CHAMBER_MENU = register(MineralPurificationChamberMenu::new, "mineral_purification_chamber");
    public static final Supplier<MenuType<ItemFilterMenu>> ITEM_FILTER_MENU = register(ItemFilterMenu::new, "item_filter");
    public static final Supplier<MenuType<MusicDiscPlayerMenu>> MUSIC_DISC_PLAYER_MENU = register(MusicDiscPlayerMenu::new, "music_disc_player");
    public static final Supplier<MenuType<FilterNodeUpgradeMenu>> FILTER_NODE_UPGRADE_MENU = register(FilterNodeUpgradeMenu::new, "filter_node_upgrade");
    public static final Supplier<MenuType<MetalShaperMenu>> METAL_SHAPER_MENU = register(MetalShaperMenu::new, "metal_shaper");
    public static final Supplier<MenuType<IndustrialPlantSiphonMenu>> INDUSTRIAL_PLANT_SIPHON_MENU = register(IndustrialPlantSiphonMenu::new, "industrial_plant_siphon");
    public static final Supplier<MenuType<MelterMenu>> MELTER_MENU = register(MelterMenu::new, "melter");
    public static final Supplier<MenuType<DryingTableMenu>> DRYING_TABLE_MENU = register(DryingTableMenu::new, "drying_table");
    private static <T extends AbstractContainerMenu> Supplier<MenuType<T>> register(IContainerFactory<T> factory, String name) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }
}
