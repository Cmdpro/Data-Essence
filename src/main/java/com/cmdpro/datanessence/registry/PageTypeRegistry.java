package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.datatablet.PageSerializer;
import com.cmdpro.datanessence.datatablet.pages.serializers.CraftingPageSerializer;
import com.cmdpro.datanessence.datatablet.pages.serializers.ItemPageSerializer;
import com.cmdpro.datanessence.datatablet.pages.serializers.MultiblockPageSerializer;
import com.cmdpro.datanessence.datatablet.pages.serializers.TextPageSerializer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class PageTypeRegistry {
    public static final DeferredRegister<PageSerializer> PAGE_TYPES = DeferredRegister.create(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "page_types"), DataNEssence.MOD_ID);

    public static final Supplier<CraftingPageSerializer> CRAFTINGPAGE = register("crafting", () -> CraftingPageSerializer.INSTANCE);
    public static final Supplier<TextPageSerializer> TEXTPAGE = register("text", () -> TextPageSerializer.INSTANCE);
    public static final Supplier<ItemPageSerializer> ITEMPAGE = register("item", () -> ItemPageSerializer.INSTANCE);
    public static final Supplier<MultiblockPageSerializer> MULTIBLOCK_PAGE = register("multiblock", () -> MultiblockPageSerializer.INSTANCE);
    private static <T extends PageSerializer> Supplier<T> register(final String name, final Supplier<T> item) {
        return PAGE_TYPES.register(name, item);
    }
}
