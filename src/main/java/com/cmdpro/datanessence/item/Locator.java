package com.cmdpro.datanessence.item;

import com.cmdpro.datanessence.data.pinging.PingableStructureManager;
import com.cmdpro.datanessence.networking.ModMessages;
import com.cmdpro.datanessence.networking.packet.s2c.CreatePingShader;
import com.cmdpro.datanessence.networking.packet.s2c.PingStructures;
import com.cmdpro.datanessence.data.pinging.StructurePing;
import com.cmdpro.datanessence.registry.SoundRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Locator extends Item {
    public Locator(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (!pLevel.isClientSide) {
            pLevel.playSound(null, pPlayer.blockPosition(), SoundRegistry.LOCATOR_PING.value(), SoundSource.PLAYERS);
            List<StructurePing> pings = new ArrayList<>();
            ServerLevel serverLevel = ((ServerLevel)pLevel);
            for (var i : PingableStructureManager.types.entrySet()) {
                if (pPlayer instanceof ServerPlayer serverPlayer) {
                    if (i.getValue().requiredAdvancement.isPresent()) {
                        AdvancementHolder advancement = serverLevel.getServer().getAdvancements().get(i.getValue().requiredAdvancement.get().location());
                        if (advancement != null) {
                            if (!serverPlayer.getAdvancements().getOrStartProgress(advancement).isDone()) {
                                continue;
                            }
                        }
                    }
                }
                Optional<Registry<Structure>> registry = serverLevel.registryAccess().registry(Registries.STRUCTURE);
                if (registry.isPresent()) {
                    Optional<Holder.Reference<Structure>> structure = registry.get().getHolder(i.getValue().structure);
                    if (structure.isPresent()) {
                        var result = serverLevel.getChunkSource().getGenerator().findNearestMapStructure(serverLevel, HolderSet.direct(structure.get()), pPlayer.blockPosition(), 50, false);
                        if (result != null) {
                            boolean known = false;
                            if (pPlayer instanceof ServerPlayer serverPlayer) {
                                AdvancementHolder advancement = serverLevel.getServer().getAdvancements().get(i.getValue().advancement.location());
                                if (advancement != null) {
                                    known = serverPlayer.getAdvancements().getOrStartProgress(advancement).isDone();
                                }
                            }
                            pings.add(new StructurePing(result.getFirst(), i.getKey(), known));
                        }
                    }
                }
            }
            ModMessages.sendToPlayer(new PingStructures(pings), (ServerPlayer)pPlayer);
            ModMessages.sendToPlayersNear(new CreatePingShader(pPlayer.position()), serverLevel, pPlayer.position(), 128);
            pPlayer.getCooldowns().addCooldown(this, 20*10);
        }
        return InteractionResultHolder.sidedSuccess(pPlayer.getItemInHand(pUsedHand), pLevel.isClientSide);
    }
}
