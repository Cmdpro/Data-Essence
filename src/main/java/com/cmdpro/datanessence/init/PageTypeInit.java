package com.cmdpro.datanessence.init;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.screen.datatablet.PageSerializer;
import com.cmdpro.datanessence.screen.datatablet.pages.serializers.CraftingPageSerializer;
import com.cmdpro.datanessence.screen.datatablet.pages.serializers.TextPageSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class PageTypeInit {
    public static final DeferredRegister<PageSerializer> PAGE_TYPES = DeferredRegister.create(new ResourceLocation(DataNEssence.MOD_ID, "pagetypes"), DataNEssence.MOD_ID);

    public static final RegistryObject<CraftingPageSerializer> CRAFTINGPAGE = register("crafting", () -> CraftingPageSerializer.INSTANCE);
    public static final RegistryObject<TextPageSerializer> TEXTPAGE = register("text", () -> TextPageSerializer.INSTANCE);
    private static <T extends PageSerializer> RegistryObject<T> register(final String name, final Supplier<T> item) {
        return PAGE_TYPES.register(name, item);
    }
}
