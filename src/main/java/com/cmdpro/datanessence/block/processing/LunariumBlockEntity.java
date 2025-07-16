package com.cmdpro.datanessence.block.processing;

import com.cmdpro.databank.model.animation.DatabankAnimationReference;
import com.cmdpro.databank.model.animation.DatabankAnimationState;
import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.block.BaseFabricatorBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceType;
import com.cmdpro.datanessence.client.FactorySong;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.cmdpro.datanessence.registry.SoundRegistry;
import com.cmdpro.datanessence.screen.LunariumMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LunariumBlockEntity extends BaseFabricatorBlockEntity implements MenuProvider {
    public DatabankAnimationState animState = new DatabankAnimationState("pose")
            .addAnim(new DatabankAnimationReference("pose", (state, anim) -> {}, (state, anim) -> {}));

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        animState.setLevel(level);
    }

    public LunariumBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.LUNARIUM.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.empty();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new LunariumMenu(pContainerId, pInventory, this);
    }

    @Override
    public List<EssenceType> getSupportedEssenceTypes() {
        return List.of(EssenceTypeRegistry.ESSENCE.get(), EssenceTypeRegistry.LUNAR_ESSENCE.get());
    }

    @Override
    public void tick(Level world, BlockPos pos, BlockState state, BaseFabricatorBlockEntity baseFabricator) {
        super.tick(world, pos, state, baseFabricator);

        if (baseFabricator instanceof LunariumBlockEntity lunarium && world.isClientSide()) {
            if (lunarium.time >= 0 && lunarium.essenceCost != null) {
                if (lunarium.essenceCost.containsKey(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(EssenceTypeRegistry.ESSENCE.get()))) {
                    ClientHandler.markIndustrialFactorySong(pos);
                }
                if (lunarium.essenceCost.containsKey(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(EssenceTypeRegistry.LUNAR_ESSENCE.get()))) {
                    ClientHandler.markLunarFactorySong(pos);
                }
            }
        }
    }

    @Override
    public float getMeterSideLength(Direction direction) {
        if (direction.equals(Direction.UP)) {
            return super.getMeterSideLength(direction)*2.8f;
        }
        return super.getMeterSideLength(direction);
    }

    @Override
    public float getMaxEssence() {
        return 2000;
    }

    private static class ClientHandler {
        static FactorySong.FactoryLoop industrialSound = FactorySong.getLoop(SoundRegistry.FABRICATOR_LOOP.value());
        static FactorySong.FactoryLoop lunarSound = FactorySong.getLoop(SoundRegistry.LUNARIUM_LOOP.value());

        public static void markIndustrialFactorySong(BlockPos pos) {
            industrialSound.addSource(pos);
        }
        public static void markLunarFactorySong(BlockPos pos) {
            lunarSound.addSource(pos);
        }
    }
}
