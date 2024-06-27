package com.cmdpro.datanessence.init;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.screen.EssenceBurnerMenu;
import com.cmdpro.datanessence.screen.FabricatorMenu;
import com.cmdpro.datanessence.screen.InfuserMenu;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MenuInit {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, DataNEssence.MOD_ID);

    public static final RegistryObject<MenuType<FabricatorMenu>> FABRICATOR_MENU = register(FabricatorMenu::new, "fabricator");
    public static final RegistryObject<MenuType<EssenceBurnerMenu>> ESSENCE_BURNER_MENU = register(EssenceBurnerMenu::new, "essence_burner");
    public static final RegistryObject<MenuType<InfuserMenu>> INFUSER_MENU = register(InfuserMenu::new, "infuser");
    private static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>> register(IContainerFactory<T> factory, String name) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }
}
