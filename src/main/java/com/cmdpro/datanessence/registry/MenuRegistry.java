package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
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
    public static final Supplier<MenuType<ChargerMenu>> CHARGER_MENU = register(ChargerMenu::new, "charger");
    public static final Supplier<MenuType<LaserEmitterMenu>> LASER_EMITTER_MENU = register(LaserEmitterMenu::new, "laser_emitter");
    public static final Supplier<MenuType<AutoFabricatorMenu>> AUTO_FABRICATOR_MENU = register(AutoFabricatorMenu::new, "auto_fabricator");
    private static <T extends AbstractContainerMenu> Supplier<MenuType<T>> register(IContainerFactory<T> factory, String name) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }
}
