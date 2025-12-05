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

    public SurveyTunnelerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.SURVEY_TUNNELER.get(), pos, state);

        if (hasLevel()) {
            maxDepth = level.getMinBuildHeight();
            currentDepth = pos.below().getY();
            isDone = false;
        }
    }

    public static void tick(Level world, BlockPos pos, BlockState state, SurveyTunnelerBlockEntity tunneler) {
        if (world.isClientSide) {
            clientTick(world, pos, state, tunneler);
        } else {

            if (tunneler.isDone)
                return;

            if (tunneler.breakTime <= 0) {
                tunneler.breakTime = tunneler.getBlockBreakTime(pos, world);
            }

            if (tunneler.progress >= tunneler.breakTime) {
                var breakPos = new BlockPos(pos.getX(), tunneler.currentDepth, pos.getZ());
                var blockState = world.getBlockState(breakPos);

                world.destroyBlock(breakPos, false);
                Block.dropResources(blockState, world, pos.above(), tunneler, null, ItemStack.EMPTY);

                tunneler.breakTime = -1;
                tunneler.progress = 0;
                tunneler.currentDepth--;

                if (tunneler.currentDepth <= tunneler.maxDepth) {
                    tunneler.isDone = true;
                    tunneler.progress = -1;
                }
            }

            if ( tunneler.storage.getEssence(EssenceTypeRegistry.ESSENCE.get()) >= 1 ) {
                tunneler.storage.removeEssence(EssenceTypeRegistry.ESSENCE.get(), 1);
                tunneler.progress++;
            }
        }
    }

    public static void clientTick(Level world, BlockPos pos, BlockState state, SurveyTunnelerBlockEntity tunneler) {

    }

    /**
     * Returns amount of ticks the Tunneler takes to break a given block
     */
    public int getBlockBreakTime(BlockPos pos, Level world) {
        var breakPos = new BlockPos(pos.getX(), currentDepth, pos.getZ());
        var block = world.getBlockState(breakPos);

        if ( (block.is(BlockTags.MINEABLE_WITH_PICKAXE) ||
                block.is(BlockTags.MINEABLE_WITH_AXE) ||
                block.is(BlockTags.MINEABLE_WITH_HOE) ||
                block.is(BlockTags.MINEABLE_WITH_SHOVEL)) &&
                !block.is(BlockTags.INCORRECT_FOR_DIAMOND_TOOL) &&
                !(block.getDestroySpeed(world, breakPos) <= -1.0f)
        ) {
            return Math.max( 1, (int) block.getBlock().defaultDestroyTime() * 10 );
        }
        else {
            maxDepth = breakPos.getY(); // assume block is unbreakable, stop work
            return -1;
        }
    }



    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket(){
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider registries){
        CompoundTag tag = pkt.getTag();
        storage.fromNbt(tag.getCompound("EssenceStorage"));
        maxDepth = tag.getInt("MaxDepth");
        currentDepth = tag.getInt("CurrentDepth");
        isDone = tag.getBoolean("IsDone");
        breakTime = tag.getInt("BreakTime");
        progress = tag.getInt("Progress");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.put("EssenceStorage", storage.toNbt());
        tag.putInt("MaxDepth", maxDepth);
        tag.putInt("CurrentDepth", currentDepth);
        tag.putBoolean("IsDone", isDone);
        tag.putInt("BreakTime", breakTime);
        tag.putInt("Progress", progress);
        return tag;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("EssenceStorage", storage.toNbt());
        tag.putInt("MaxDepth", maxDepth);
        tag.putInt("CurrentDepth", currentDepth);
        tag.putBoolean("IsDone", isDone);
        tag.putInt("BreakTime", breakTime);
        tag.putInt("Progress", progress);
        super.saveAdditional(tag, registries);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        storage.fromNbt(tag.getCompound("EssenceStorage"));
        maxDepth = tag.getInt("MaxDepth");
        currentDepth = tag.getInt("CurrentDepth");
        isDone = tag.getBoolean("IsDone");
        breakTime = tag.getInt("BreakTime");
        progress = tag.getInt("Progress");
    }
}
