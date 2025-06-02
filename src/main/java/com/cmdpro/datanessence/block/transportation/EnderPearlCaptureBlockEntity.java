package com.cmdpro.datanessence.block.transportation;

import com.cmdpro.datanessence.api.misc.BlockPosNetworks;
import com.cmdpro.datanessence.api.pearlnetwork.PearlNetworkBlockEntity;
import com.cmdpro.datanessence.client.particle.CircleParticleOptions;
import com.cmdpro.datanessence.networking.ModMessages;
import com.cmdpro.datanessence.networking.packet.s2c.PlayEnderPearlRedirectionEffect;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.cmdpro.datanessence.registry.BlockEntityRegistry;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.RandomUtils;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.graph.DefaultEdge;

import java.awt.*;
import java.util.List;

public class EnderPearlCaptureBlockEntity extends PearlNetworkBlockEntity {
    public AnimationState animState = new AnimationState();
    public EnderPearlCaptureBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.ENDER_PEARL_CAPTURE.get(), pos, blockState);
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
                        Entity owner = i.getOwner();
                        GraphPath<BlockPos, DefaultEdge> end = ends.get(owner.getRandom().nextInt(0, ends.size()));
                        Vec3 pos = end.getEndVertex().getCenter();
                        owner.teleportTo(pos.x, pos.y, pos.z);
                        List<BlockPos> vertexes = end.getVertexList();
                        PlayEnderPearlRedirectionEffect message = new PlayEnderPearlRedirectionEffect(vertexes);
                        for (ServerPlayer j : ((ServerLevel)pLevel).players()) {
                            boolean canSend = false;
                            for (BlockPos k : vertexes) {
                                if (j.position().distanceTo(k.getCenter()) <= 128) {
                                    canSend = true;
                                    break;
                                }
                            }
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
