package EsetKalenko.Halcyon.api.node.block;

import EsetKalenko.Halcyon.api.util.BlockPosEdge;
import com.cmdpro.databank.model.animation.DatabankAnimationReference;
import com.cmdpro.databank.model.animation.DatabankAnimationState;
import EsetKalenko.Halcyon.DataNEssence;
import EsetKalenko.Halcyon.api.misc.BlockPosNetworks;
import EsetKalenko.Halcyon.api.node.item.INodeUpgrade;
import EsetKalenko.Halcyon.client.particle.CircleParticleOptions;
import EsetKalenko.Halcyon.registry.AttachmentTypeRegistry;
import com.jgalgo.alg.common.Path;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.joml.Vector3f;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public abstract class BaseEssencePointBlockEntity extends BlockEntity {
    public DatabankAnimationState animState = new DatabankAnimationState("idle")
            .addAnim(new DatabankAnimationReference("idle", (state, anim) -> {}, (state, anim) -> {}));

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        animState.setLevel(level);
    }

    public boolean isRelay;
    boolean wasRelay;

    public final ItemStackHandler universalUpgrade = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    public final ItemStackHandler uniqueUpgrade = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    @SuppressWarnings("unchecked")
    public <T> T getValue(ResourceLocation id, T defaultValue) {
        T value = defaultValue;
        if (universalUpgrade.getStackInSlot(0).getItem() instanceof INodeUpgrade upgrade) {
            Object modified = upgrade.getValue(universalUpgrade.getStackInSlot(0), id, value, this);
            if (modified != null) {
                value = (T)modified;
            }
        }
        if (uniqueUpgrade.getStackInSlot(0).getItem() instanceof INodeUpgrade upgrade) {
            Object modified = upgrade.getValue(uniqueUpgrade.getStackInSlot(0), id, value, this);
            if (modified != null) {
                value = (T)modified;
            }
        }
        return value;
    }
    public float getFinalSpeed(float value) {
        return value*getValue(DataNEssence.locate("speed_multiplier"), 1f);
    }
    public int getFinalSpeed(int value) {
        return (int)(value*getValue(DataNEssence.locate("speed_multiplier"), 1f));
    }

    public BaseEssencePointBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }
    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null) {
            if (!level.isClientSide) {
                BlockPosNetworks networks = level.getData(AttachmentTypeRegistry.ESSENCE_NODE_NETWORKS);
                if (!networks.graph.vertices().contains(getBlockPos())) {
                    networks.graph.addVertex(getBlockPos());
                    level.syncData(AttachmentTypeRegistry.ESSENCE_NODE_NETWORKS);
                }
                updateBlock();
            }
        }
    }

    public abstract Color[] linkColor();

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, BaseEssencePointBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide()) {
            BlockPosNetworks networks = pLevel.getData(AttachmentTypeRegistry.ESSENCE_NODE_NETWORKS);
            if (networks.graph.inEdges(pPos).isEmpty() && !networks.graph.outEdges(pPos).isEmpty()) {
                var paths = networks.graph.getPaths(pPos);
                List<Path<BlockPos, BlockPosEdge>> ends = networks.graph.vertices().stream().filter((vertex) -> pLevel.isLoaded(vertex) && networks.graph.outEdges(vertex).isEmpty()).map(paths::getPath).filter(Objects::nonNull).toList();
                pBlockEntity.preTransferHooks(pBlockEntity, ends);
                pBlockEntity.transfer(pBlockEntity, ends);
                pBlockEntity.postTransferHooks(pBlockEntity, ends);
            }
        } else {
            if (pBlockEntity.wasRelay != pBlockEntity.isRelay) {
                Color color = pBlockEntity.linkColor()[0];
                for (int i = 0; i < 32; i++) {
                    CircleParticleOptions options = new CircleParticleOptions();
                    options.setColor(color);
                    Vec3 center = pPos.getCenter();
                    float angle = (360f/32f)*(float)i;
                    Vector3f spd = new Vector3f((float)Math.sin(Math.toRadians(angle)), 0, (float)Math.cos(Math.toRadians(angle))).mul(0.2f);
                    AttachFace attachFace = pState.getValue(BaseCapabilityPoint.FACE);
                    Direction direction = pState.getValue(BaseCapabilityPoint.FACING);
                    if (attachFace.equals(AttachFace.WALL)) {
                        spd.rotate(direction.getRotation());
                    }
                    pLevel.addParticle(options, center.x, center.y, center.z, spd.x, spd.y, spd.z);
                }
            }
            pBlockEntity.wasRelay = pBlockEntity.isRelay;
        }
    }

    public boolean preTransferHooks(BlockEntity from, List<Path<BlockPos, BlockPosEdge>> other) {
        boolean cancel = false;
        if (universalUpgrade.getStackInSlot(0).getItem() instanceof INodeUpgrade upgrade) {
            if (upgrade.preTransfer(universalUpgrade.getStackInSlot(0), from, other, cancel)) {
                cancel = true;
            }
        }
        if (uniqueUpgrade.getStackInSlot(0).getItem() instanceof INodeUpgrade upgrade) {
            if (upgrade.preTransfer(uniqueUpgrade.getStackInSlot(0), from, other, cancel)) {
                cancel = true;
            }
        }
        return cancel;
    }

    public void postTransferHooks(BlockEntity from, List<Path<BlockPos, BlockPosEdge>> other) {
        if (universalUpgrade.getStackInSlot(0).getItem() instanceof INodeUpgrade upgrade) {
            upgrade.postTransfer(universalUpgrade.getStackInSlot(0), from, other);
        }
        if (uniqueUpgrade.getStackInSlot(0).getItem() instanceof INodeUpgrade upgrade) {
            upgrade.postTransfer(uniqueUpgrade.getStackInSlot(0), from, other);
        }
    }

    public abstract void transfer(BaseEssencePointBlockEntity from, List<Path<BlockPos, BlockPosEdge>> other);

    public Direction getDirection() {
        if (getBlockState().getValue(BaseCapabilityPoint.FACE).equals(AttachFace.CEILING)) {
            return Direction.DOWN;
        }
        if (getBlockState().getValue(BaseCapabilityPoint.FACE).equals(AttachFace.WALL)) {
            return getBlockState().getValue(BaseCapabilityPoint.FACING);
        }
        return Direction.UP;
    }
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket(){
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider pRegistries) {
        CompoundTag tag = pkt.getTag();
        uniqueUpgrade.deserializeNBT(pRegistries, tag.getCompound("uniqueUpgrade"));
        universalUpgrade.deserializeNBT(pRegistries, tag.getCompound("universalUpgrade"));
        isRelay = tag.getBoolean("Relay");
    }
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = new CompoundTag();
        tag.put("uniqueUpgrade", uniqueUpgrade.serializeNBT(pRegistries));
        tag.put("universalUpgrade", universalUpgrade.serializeNBT(pRegistries));
        tag.putBoolean("Relay", isRelay);
        return tag;
    }
    public void updateBlock() {
        BlockPosNetworks networks = level.getData(AttachmentTypeRegistry.ESSENCE_NODE_NETWORKS);
        var incoming = networks.graph.inEdges(getBlockPos());
        var outgoing = networks.graph.outEdges(getBlockPos());
        isRelay = (!incoming.isEmpty() && !outgoing.isEmpty());
        BlockState blockState = level.getBlockState(this.getBlockPos());
        this.level.sendBlockUpdated(this.getBlockPos(), blockState, blockState, 3);
        this.setChanged();
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(tag, pRegistries);
        uniqueUpgrade.deserializeNBT(pRegistries, tag.getCompound("uniqueUpgrade"));
        universalUpgrade.deserializeNBT(pRegistries, tag.getCompound("universalUpgrade"));
        isRelay = tag.getBoolean("Relay");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        tag.put("uniqueUpgrade", uniqueUpgrade.serializeNBT(pRegistries));
        tag.put("universalUpgrade", universalUpgrade.serializeNBT(pRegistries));
        tag.putBoolean("Relay", isRelay);
    }

}
