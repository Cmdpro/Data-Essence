package com.cmdpro.datanessence.block.transportation;

import com.cmdpro.databank.model.animation.DatabankAnimationReference;
import com.cmdpro.databank.model.animation.DatabankAnimationState;
import com.cmdpro.datanessence.api.misc.BlockPosNetworks;
import com.cmdpro.datanessence.api.pearlnetwork.PearlNetworkBlockEntity;
import com.cmdpro.datanessence.networking.ModMessages;
import com.cmdpro.datanessence.networking.packet.s2c.vfx.PlayEnderPearlRedirectionEffect;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.graph.DefaultEdge;

import java.util.List;

public class EnderPearlCaptureBlockEntity extends PearlNetworkBlockEntity {
    public DatabankAnimationState animState = new DatabankAnimationState("idle")
            .addAnim(new DatabankAnimationReference("idle", (state, anim) -> {}, (state, anim) -> {}));

    public EnderPearlCaptureBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.ENDER_PEARL_CAPTURE.get(), pos, blockState);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        animState.setLevel(level);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, EnderPearlCaptureBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide) {
            List<ThrownEnderpearl> pearls = pLevel.getEntitiesOfClass(ThrownEnderpearl.class, AABB.ofSize(pPos.getCenter(), 5, 5, 5));
            if (!pearls.isEmpty()) {
                BlockPosNetworks networks = pLevel.getData(AttachmentTypeRegistry.ENDER_PEARL_NETWORKS);
                ShortestPathAlgorithm.SingleSourcePaths<BlockPos, DefaultEdge> paths = networks.path.getPaths(pPos);
                List<GraphPath<BlockPos, DefaultEdge>> ends = networks.graph.vertexSet().stream()
                        .filter((vertex) -> networks.graph.edgesOf(vertex).stream().noneMatch((edge) -> networks.graph.getEdgeSource(edge).equals(vertex)) && paths.getPath(vertex) != null)
                        .map(paths::getPath)
                        .filter((i) -> pLevel.getBlockEntity(i.getEndVertex()) instanceof EnderPearlDestinationBlockEntity).toList();
                if (!ends.isEmpty()) {
                    for (ThrownEnderpearl i : pearls) {
                        if (i.getOwner() != null) {
                            Entity owner = i.getOwner();
                            GraphPath<BlockPos, DefaultEdge> end = ends.get(owner.getRandom().nextInt(0, ends.size()));
                            Vec3 pos = end.getEndVertex().getCenter();
                            owner.teleportTo(pos.x, pos.y, pos.z);
                            List<BlockPos> vertexes = end.getVertexList();
                            for (ServerPlayer j : ((ServerLevel) pLevel).players()) {
                                List<BlockPos> nodes = vertexes.stream().filter((block) -> block.getCenter().distanceTo(j.position()) <= 64).toList();
                                PlayEnderPearlRedirectionEffect message = new PlayEnderPearlRedirectionEffect(nodes);
                                boolean canSend = !nodes.isEmpty();
                                if (canSend) {
                                    ModMessages.sendToPlayer(message, j);
                                }
                            }
                            i.remove(Entity.RemovalReason.DISCARDED);
                        }
                    }
                }
            }
        }
    }
}
