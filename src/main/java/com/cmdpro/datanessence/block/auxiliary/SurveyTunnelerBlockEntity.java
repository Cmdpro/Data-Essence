package com.cmdpro.datanessence.block.auxiliary;

import com.cmdpro.databank.model.animation.DatabankAnimationReference;
import com.cmdpro.databank.model.animation.DatabankAnimationState;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class SurveyTunnelerBlockEntity extends BlockEntity implements EssenceBlockEntity {
    public DatabankAnimationState animState = new DatabankAnimationState("idle")
            .addAnim( new DatabankAnimationReference(
                    "idle",
                    (state, anim) -> {},
                    (state, anim) -> {}))
            .addAnim( new DatabankAnimationReference(
                    "axle_turn",
                    (state, anim) -> {},
                    (state, anim) -> {}))
            .addAnim( new DatabankAnimationReference(
                    "gears_turn",
                    (state, anim) -> {},
                    (state, anim) -> {}));

    public SingleEssenceContainer storage = new SingleEssenceContainer(EssenceTypeRegistry.ESSENCE.get(), 1000);

    public int maxDepth;      // deepest Y we will go to (usually world min Y)
    public int currentDepth;  // current Y we’re working at
    public boolean isDone;    // no more work to do
    public int breakTime;     // ticks needed to break current block
    public int progress;      // how many progress “ticks” we have on current block

    @Override
    public EssenceStorage getStorage() {
        return storage;
    }

    public SurveyTunnelerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.SURVEY_TUNNELER.get(), pos, state);
        System.out.println("SurveyTunnelerBlockEntity created at " + pos);

        // Start just below the block
        this.currentDepth = pos.below().getY();

        // Default, will be corrected on first tick
        this.maxDepth = -64;

        this.isDone = false;
        this.breakTime = -1;
        this.progress = -1;
    }

    //just to be safe
    @Override
    public void onLoad() {
        super.onLoad();
        if (this.level != null) {
            this.animState.setLevel(this.level);
        }
    }


    public static void tick(Level world, BlockPos pos, BlockState state, SurveyTunnelerBlockEntity tunneler) {
        // Make sure animation time uses this world's clock
        if (tunneler.animState.level != world) {
            tunneler.animState.setLevel(world);
        }

        if (world.isClientSide) {
            clientTick(world, pos, state, tunneler);
            return;
        }

        // Initialize maxDepth to current dimension's minimum build height
        int minY = world.getMinBuildHeight();
        if (tunneler.maxDepth < minY) {
            tunneler.maxDepth = minY;
        }

        if (tunneler.isDone) {
            return;
        }

        // If we somehow went below min Y, stop
        if (tunneler.currentDepth < tunneler.maxDepth) {
            tunneler.isDone = true;
            tunneler.progress = 0;
            return;
        }

        BlockPos breakPos = new BlockPos(pos.getX(), tunneler.currentDepth, pos.getZ());
        BlockState blockState = world.getBlockState(breakPos);

        // Stop if we hit bedrock or any unbreakable block
        if (blockState.is(Blocks.BEDROCK) || blockState.getDestroySpeed(world, breakPos) < 0.0F) {
            tunneler.maxDepth = breakPos.getY();
            tunneler.isDone = true;
            tunneler.progress = -1;
            return;
        }

        // Skip air and keep going down
        if (blockState.isAir()) {
            tunneler.currentDepth--;
            if (tunneler.currentDepth < tunneler.maxDepth) {
                tunneler.isDone = true;
                tunneler.progress = -1;
            }
            return;
        }

        // If we don't yet know how long to break this block, calculate it
        if (tunneler.breakTime <= 0) {
            tunneler.breakTime = tunneler.getBlockBreakTime(pos, world);
            // Unbreakable
            if (tunneler.breakTime < 0) {
                tunneler.maxDepth = breakPos.getY();
                tunneler.isDone = true;
                tunneler.progress = -1;
                return;
            }
        }


        if (tunneler.storage.getEssence(EssenceTypeRegistry.ESSENCE.get()) >= 1) {
            tunneler.storage.removeEssence(EssenceTypeRegistry.ESSENCE.get(), 1); //maybe change this
            tunneler.progress++;
        } else {
            // No fuel, can't dig this tick
            return;
        }

        // Only actually break the block once we've reached the break time
        if (tunneler.progress >= tunneler.breakTime) {
            world.destroyBlock(breakPos, false);
            Block.dropResources(blockState, world, pos.above(), tunneler, null, ItemStack.EMPTY);

            // Reset for next block
            tunneler.breakTime = -1;
            tunneler.progress = 0;
            tunneler.currentDepth--;

            // If we've gone as deep as we can, stop
            if (tunneler.currentDepth < tunneler.maxDepth) {
                tunneler.isDone = true;
                tunneler.progress = -1;
            }
        }
        tunneler.updateBlock();
    }

    public static void clientTick(Level world, BlockPos pos, BlockState state, SurveyTunnelerBlockEntity tunneler) {
        if (!world.isClientSide) return;

        boolean hasFuel = tunneler.storage.getEssence(EssenceTypeRegistry.ESSENCE.get()) >= 1;
        boolean isDigging = tunneler.progress >= 0;
        //idk eset tweak this yourself im super confused and i am not gonna do this rn, works strangely
        // 😔😔😔😔
        //nvm it works now
        //delete this
        if (hasFuel && isDigging) {
            tunneler.animState.setAnim("gears_turn");
        } else {
            tunneler.animState.setAnim("idle");
        }
    }

    /**
     * Returns amount of ticks the Tunneler takes to break a given block.
     * Negative means "unbreakable -> stop".
     */
    public int getBlockBreakTime(BlockPos pos, Level world) {
        var breakPos = new BlockPos(pos.getX(), currentDepth, pos.getZ());
        var blockState = world.getBlockState(breakPos);

        // If it's air, treat as cheap "skip" block
        if (blockState.isAir()) {
            return 1;
        }

        float destroyTime = blockState.getBlock().defaultDestroyTime();

        // Unbreakable according to block's destroy time
        if (destroyTime < 0.0F) {
            return -1;
        }

        return Math.max(1, (int) (destroyTime * 10));
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

    //idk if this helps at all
    protected void updateBlock() {
        if (this.level == null) return;
        BlockState blockState = level.getBlockState(this.getBlockPos());
        level.sendBlockUpdated(this.getBlockPos(), blockState, blockState, 3);
        setChanged();
    }


}
