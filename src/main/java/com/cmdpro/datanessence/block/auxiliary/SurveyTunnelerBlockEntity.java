package com.cmdpro.datanessence.block.auxiliary;

import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceStorage;
import com.cmdpro.datanessence.api.essence.container.SingleEssenceContainer;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class SurveyTunnelerBlockEntity extends BlockEntity implements EssenceBlockEntity {
    public SingleEssenceContainer storage = new SingleEssenceContainer(EssenceTypeRegistry.ESSENCE.get(), 1000);
    int maxDepth; // the deepest Y level of the current dimension, or of the first unbreakable block we find
    int currentDepth; // what Y level are we currently on?
    boolean isDone; // whether currentDepth <= maxDepth - i.e. there is no work to do
    int breakTime; // how many ticks does it take to break the current block we are working at?
    int progress; // how far along are we in the current block break?

    @Override
    public EssenceStorage getStorage() {
        return storage;
    }

    public SurveyTunnelerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegistry.SURVEY_TUNNELER.get(), pPos, pBlockState);
    }

    public static void tick(Level world, BlockPos pos, BlockState state, SurveyTunnelerBlockEntity tunneler) {
        if (world.isClientSide) {
            clientTick(world, pos, state, tunneler);
        } else {

            if (tunneler.isDone)
                return;

            if (tunneler.currentDepth <= tunneler.maxDepth) {
                tunneler.isDone = true;
                tunneler.progress = -1;
            }

            if (tunneler.currentDepth >= tunneler.worldPosition.getY()) {
                tunneler.currentDepth = tunneler.worldPosition.getY()-1;
                tunneler.maxDepth = world.getMinBuildHeight();
            }

            if (tunneler.breakTime == -1) {
                var breakPos = new BlockPos(pos.getX(), tunneler.currentDepth, pos.getZ());
                var block = world.getBlockState(breakPos);

                if ( (block.is(BlockTags.MINEABLE_WITH_PICKAXE) ||
                        block.is(BlockTags.MINEABLE_WITH_AXE) ||
                        block.is(BlockTags.MINEABLE_WITH_HOE) ||
                        block.is(BlockTags.MINEABLE_WITH_SHOVEL)) &&
                        !block.is(BlockTags.INCORRECT_FOR_DIAMOND_TOOL) &&
                        !(block.getDestroySpeed(world, breakPos) <= -1.0f)
                ) {
                    tunneler.breakTime = tunneler.getBlockBreakTime(block.getBlock());
                }
                else {
                    tunneler.maxDepth = breakPos.getY(); // assume block is unbreakable, stop work
                    return;
                }
            }

            if (tunneler.progress >= tunneler.breakTime && tunneler.storage.getEssence(EssenceTypeRegistry.ESSENCE.get()) >= tunneler.breakTime) {
                var breakPos = new BlockPos(pos.getX(), tunneler.currentDepth, pos.getZ());
                var blockState = world.getBlockState(breakPos);

                world.destroyBlock(breakPos, false);
                Block.dropResources(blockState, world, pos.above(), tunneler, null, ItemStack.EMPTY);
                tunneler.storage.removeEssence(EssenceTypeRegistry.ESSENCE.get(), tunneler.breakTime);

                tunneler.breakTime = -1;
                tunneler.progress = 0;
                tunneler.currentDepth--;
            }

            tunneler.progress++;
        }
    }

    public static void clientTick(Level world, BlockPos pos, BlockState state, SurveyTunnelerBlockEntity tunneler) {

    }

    /**
     * Returns amount of ticks the Tunneler takes to break a given block, based on its hardness
     */
    public int getBlockBreakTime(Block block) {
        return 5;
    }



    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket(){
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider registries){
        CompoundTag tag = pkt.getTag();
        storage.fromNbt(tag.getCompound("EssenceStorage"));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.put("EssenceStorage", storage.toNbt());
        return tag;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("EssenceStorage", storage.toNbt());
        super.saveAdditional(tag, registries);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        storage.fromNbt(tag.getCompound("EssenceStorage"));
    }
}
