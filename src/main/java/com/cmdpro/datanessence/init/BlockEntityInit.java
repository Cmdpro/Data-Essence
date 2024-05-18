package com.cmdpro.datanessence.init;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.entity.FabricatorBlockEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class BlockEntityInit {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, DataNEssence.MOD_ID);
    public static final RegistryObject<BlockEntityType<FabricatorBlockEntity>> FABRICATOR =
            register("fabricatorblockentity", () ->
                    BlockEntityType.Builder.of(FabricatorBlockEntity::new,
                            BlockInit.FABRICATOR.get()).build(null));


    private static <T extends BlockEntityType<?>> RegistryObject<T> register(final String name, final Supplier<T> blockentity) {
        return BLOCK_ENTITIES.register(name, blockentity);
    }
}
