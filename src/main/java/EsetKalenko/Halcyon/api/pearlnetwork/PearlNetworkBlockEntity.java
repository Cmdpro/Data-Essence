package EsetKalenko.Halcyon.api.pearlnetwork;

import EsetKalenko.Halcyon.api.misc.BlockPosNetworks;
import EsetKalenko.Halcyon.registry.AttachmentTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class PearlNetworkBlockEntity extends BlockEntity {
    public PearlNetworkBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public Vec3 getLinkShift() {
        return Vec3.ZERO;
    }

    public void updateBlock() {
        BlockState blockState = level.getBlockState(this.getBlockPos());
        this.level.sendBlockUpdated(this.getBlockPos(), blockState, blockState, 3);
        this.setChanged();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket(){
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null) {
            if (!level.isClientSide) {
                BlockPosNetworks networks = level.getData(AttachmentTypeRegistry.ENDER_PEARL_NETWORKS);
                if (!networks.graph.vertices().contains(getBlockPos())) {
                    networks.graph.addVertex(getBlockPos());
                }
            }
        }
    }
}
