package com.cmdpro.datanessence.api.pearlnetwork;

import com.cmdpro.datanessence.api.misc.BlockPosNetworks;
import com.cmdpro.datanessence.api.node.block.BaseCapabilityPoint;
import com.cmdpro.datanessence.api.node.block.BaseCapabilityPointBlockEntity;
import com.cmdpro.datanessence.api.node.item.INodeUpgrade;
import com.cmdpro.datanessence.api.util.PlayerDataUtil;
import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.cmdpro.datanessence.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jgrapht.graph.DefaultEdge;

import java.awt.*;
import java.util.Optional;
import java.util.Set;

public class PearlNetworkBlock extends Block {
    public PearlNetworkBlock(Properties properties) {
        super(properties);
    }
    public static Color getColor() {
        return new Color(12, 87, 38);
    }
    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        Item requiredItem = ItemRegistry.ESSENCE_REDIRECTOR.get();
        boolean success = pPlayer.getItemInHand(pHand).is(requiredItem);
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if (entity instanceof PearlNetworkBlockEntity ent) {
                if (pPlayer.getItemInHand(pHand).is(requiredItem)) {
                    BlockPosNetworks networks = pLevel.getData(AttachmentTypeRegistry.ENDER_PEARL_NETWORKS);
                    Optional<BlockEntity> linkFrom = pPlayer.getData(AttachmentTypeRegistry.LINK_FROM);
                    if (!linkFrom.isPresent()) {
                        if (canConnectFrom()) {
                            pPlayer.setData(AttachmentTypeRegistry.LINK_FROM, Optional.of(ent));
                            PlayerDataUtil.updateData((ServerPlayer) pPlayer);
                            pLevel.playSound(null, pPos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1f, 1.1f);
                        }
                    } else {
                        if (canConnectTo(linkFrom.get())) {
                            if (linkFrom.get().getBlockState().getBlock() instanceof PearlNetworkBlock other) {
                                if (ent != linkFrom.get() && (ent.link.isEmpty() || !ent.link.contains(linkFrom.get().getBlockPos()))) {
                                    if (linkFrom.get() instanceof PearlNetworkBlockEntity linkFrom2) {
                                        networks.graph.addEdge(linkFrom2.getBlockPos(), pPos);
                                        linkFrom2.updateBlock();
                                        pPlayer.setData(AttachmentTypeRegistry.LINK_FROM, Optional.empty());
                                        PlayerDataUtil.updateData((ServerPlayer) pPlayer);
                                        pLevel.playSound(null, pPos, SoundEvents.CROSSBOW_LOADING_END.value(), SoundSource.BLOCKS, 1f, 1.1f);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (success) {
            return ItemInteractionResult.sidedSuccess(pLevel.isClientSide());
        }
        return super.useItemOn(pStack, pState, pLevel, pPos, pPlayer, pHand, pHitResult);
    }
    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pState.getBlock() != pNewState.getBlock()) {
            if (pLevel.getBlockEntity(pPos) instanceof BaseCapabilityPointBlockEntity) {
                BlockPosNetworks networks = pLevel.getData(AttachmentTypeRegistry.ENDER_PEARL_NETWORKS);
                Set<DefaultEdge> edges = networks.graph.edgesOf(pPos);
                for (DefaultEdge i : edges) {
                    BlockPos pos = networks.graph.getEdgeSource(i);
                    networks.graph.removeEdge(i);
                    if (!pos.equals(pPos)) {
                        if (pLevel.getBlockEntity(pos) instanceof BaseCapabilityPointBlockEntity ent) {
                            ent.updateBlock();
                        }
                    }
                }
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }
    public boolean canConnectTo(BlockEntity from) {
        return true;
    }
    public boolean canConnectFrom() {
        return true;
    }
}
