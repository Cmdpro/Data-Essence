package com.cmdpro.datanessence.block.transmission;

import com.cmdpro.datanessence.api.misc.BlockPosNetworks;
import com.cmdpro.datanessence.api.node.block.BaseEssencePoint;
import com.cmdpro.datanessence.api.node.block.BaseEssencePointBlockEntity;
import com.cmdpro.datanessence.api.node.item.INodeUpgrade;
import com.cmdpro.datanessence.api.util.PlayerDataUtil;
import com.cmdpro.datanessence.config.DataNEssenceConfig;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.cmdpro.datanessence.registry.ItemRegistry;
import com.cmdpro.datanessence.registry.SoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.jgrapht.graph.DefaultEdge;

import java.util.Optional;
import java.util.Set;

public class StructuralPoint extends BaseEssencePoint {

    public StructuralPoint(Properties properties) {
        super(properties);
    }

    @Override
    public Item getRequiredWire() {
        return ItemRegistry.STRUCTURAL_WIRE_RED.get();
    }

    private boolean isStructuralWire(ItemStack stack) {
        return stack.is(ItemRegistry.STRUCTURAL_WIRE_RED.get())
                || stack.is(ItemRegistry.STRUCTURAL_WIRE_GREEN.get());
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
                                           BlockPos pos, Player player, InteractionHand hand,
                                           BlockHitResult hit) {
        boolean isWire = isStructuralWire(stack);
        if (!isWire) {
            return super.useItemOn(stack, state, level, pos, player, hand, hit);
        }

        if (!level.isClientSide()) {//ill fix this someday, whoever is looking at this commit STOP... DROP.... AND ROLL
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof StructuralPointBlockEntity ent) {
                BlockPosNetworks networks = level.getData(AttachmentTypeRegistry.ESSENCE_NODE_NETWORKS);
                Set<DefaultEdge> edges = networks.graph.edgesOf(pos);
                Optional<BlockEntity> linkFromOpt = player.getData(AttachmentTypeRegistry.LINK_FROM);

                if (linkFromOpt.isEmpty()) {
                    // start linking from this node
                    long outgoing = edges.stream()
                            .filter((edge) -> networks.graph.getEdgeSource(edge).equals(pos))
                            .count();
                    if (outgoing < DataNEssenceConfig.maxNodeWires) {
                        player.setData(AttachmentTypeRegistry.LINK_FROM, Optional.of(ent));
                        PlayerDataUtil.updateData((ServerPlayer) player);
                        level.playSound(null, pos,
                                SoundRegistry.NODE_LINK_FROM.value(),
                                SoundSource.BLOCKS, 1f, 1f);
                    }
                } else if (linkFromOpt.get() instanceof StructuralPointBlockEntity linkFrom && linkFrom != ent) {
                    // finish linking to this node
                    if (ent.link == null || !ent.link.contains(linkFrom.getBlockPos())) {
                        if (linkFrom.getBlockPos().closerThan(ent.getBlockPos(), DataNEssenceConfig.wireDistanceLimit)) {
                            networks.graph.addEdge(linkFrom.getBlockPos(), pos);
                            linkFrom.updateBlock();
                            ent.updateBlock();

                            player.setData(AttachmentTypeRegistry.LINK_FROM, Optional.empty());
                            PlayerDataUtil.updateData((ServerPlayer) player);

                            if (!player.isCreative()) {
                                stack.shrink(1); // consume whichever color wire was used
                            }

                            level.playSound(null, pos,
                                    SoundRegistry.NODE_LINK_TO.value(),
                                    SoundSource.BLOCKS, 1f, 1f);
                        }
                    }
                }
            }
        }

        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StructuralPointBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        return (lvl, pos, st, be) -> {
            if (be instanceof StructuralPointBlockEntity ent) {
                StructuralPointBlockEntity.tick(lvl, pos, st, ent);
            }
        };
    }
}
