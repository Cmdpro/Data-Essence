package com.cmdpro.datanessence.item;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.CommonVariables;
import com.cmdpro.datanessence.api.util.DataTabletUtil;
import com.cmdpro.datanessence.data.pinging.PingableStructureManager;
import com.cmdpro.datanessence.networking.ModMessages;
import com.cmdpro.datanessence.networking.packet.s2c.PingStructures;
import com.cmdpro.datanessence.registry.SoundRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.commands.LocateCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.apache.commons.lang3.RandomUtils;
import org.joml.Math;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class Locator extends Item {
    public Locator(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (!pLevel.isClientSide) {
            pLevel.playSound(null, pPlayer.blockPosition(), SoundRegistry.LOCATOR_PING.get(), SoundSource.PLAYERS);
            List<PingStructures.StructurePing> pings = new ArrayList<>();
            ServerLevel serverLevel = ((ServerLevel)pLevel);
            for (var i : PingableStructureManager.types.entrySet()) {
                Optional<Registry<Structure>> registry = serverLevel.registryAccess().registry(Registries.STRUCTURE);
                if (registry.isPresent()) {
                    Optional<Holder.Reference<Structure>> structure = registry.get().getHolder(i.getValue().structure);
                    if (structure.isPresent()) {
                        var result = serverLevel.getChunkSource().getGenerator().findNearestMapStructure(serverLevel, HolderSet.direct(structure.get()), pPlayer.blockPosition(), 50, false);
                        pings.add(new PingStructures.StructurePing(result.getFirst(), i.getKey()));
                    }
                }
            }
            ModMessages.sendToPlayer(new PingStructures(pings), (ServerPlayer)pPlayer);
        }
        return InteractionResultHolder.sidedSuccess(pPlayer.getItemInHand(pUsedHand), pLevel.isClientSide);
    }
}
