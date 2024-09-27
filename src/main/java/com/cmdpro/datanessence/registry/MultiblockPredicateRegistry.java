package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.multiblock.MultiblockPredicateSerializer;
import com.cmdpro.datanessence.multiblock.predicates.serializers.BlockstateMultiblockPredicateSerializer;
import com.cmdpro.datanessence.multiblock.predicates.serializers.TagMultiblockPredicateSerializer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MultiblockPredicateRegistry {
    public static final DeferredRegister<MultiblockPredicateSerializer> MULTIBLOCK_PREDICATE_TYPES = DeferredRegister.create(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "multiblock_predicates"), DataNEssence.MOD_ID);

    public static final Supplier<MultiblockPredicateSerializer> BLOCKSTATE = register("blockstate", () -> new BlockstateMultiblockPredicateSerializer());
    public static final Supplier<MultiblockPredicateSerializer> TAG = register("tag", () -> new TagMultiblockPredicateSerializer());
    private static <T extends MultiblockPredicateSerializer> Supplier<T> register(final String name, final Supplier<T> item) {
        return MULTIBLOCK_PREDICATE_TYPES.register(name, item);
    }
}
