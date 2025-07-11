package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.datatablet.PageSerializer;
import com.cmdpro.datanessence.data.datatablet.pages.serializers.CraftingPageSerializer;
import com.cmdpro.datanessence.data.datatablet.pages.serializers.ItemPageSerializer;
import com.cmdpro.datanessence.data.datatablet.pages.serializers.MultiblockPageSerializer;
import com.cmdpro.datanessence.data.datatablet.pages.serializers.TextPageSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class PageTypeRegistry {
    public static final DeferredRegister<PageSerializer> PAGE_TYPES = DeferredRegister.create(DataNEssenceRegistries.PAGE_TYPE_REGISTRY_KEY, DataNEssence.MOD_ID);

    public static final Supplier<CraftingPageSerializer> CRAFTINGPAGE = register("crafting", () -> CraftingPageSerializer.INSTANCE);
    public static final Supplier<TextPageSerializer> TEXTPAGE = register("text", () -> TextPageSerializer.INSTANCE);
    public static final Supplier<ItemPageSerializer> ITEMPAGE = register("item", () -> ItemPageSerializer.INSTANCE);
    public static final Supplier<MultiblockPageSerializer> MULTIBLOCK_PAGE = register("multiblock", () -> MultiblockPageSerializer.INSTANCE);
    private static <T extends PageSerializer> Supplier<T> register(final String name, final Supplier<T> item) {
        return PAGE_TYPES.register(name, item);
    }
}
