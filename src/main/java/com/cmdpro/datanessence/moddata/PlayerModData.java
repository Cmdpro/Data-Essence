package com.cmdpro.datanessence.moddata;

import com.cmdpro.datanessence.api.BaseCapabilityPointBlockEntity;
import com.cmdpro.datanessence.api.BaseEssencePointBlockEntity;
import com.cmdpro.datanessence.networking.ModMessages;
import com.cmdpro.datanessence.networking.packet.PlayerDataSyncS2CPacket;
import com.cmdpro.datanessence.networking.packet.PlayerTierSyncS2CPacket;
import com.cmdpro.datanessence.networking.packet.UnlockedEntrySyncS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerModData {
    public PlayerModData() {
        unlocked = new ArrayList<>();
    }
    private List<ResourceLocation> unlocked;
    private int tier;
    public int getTier() {
        return tier;
    }
    public void setTier(int value) {
        tier = value;
    }
    private BlockEntity linkFrom;
    public BlockEntity getLinkFrom() {
        return linkFrom;
    }
    public void setLinkFrom(BlockEntity value) {
        linkFrom = value;
    }
    public List<ResourceLocation> getUnlocked() {
        return unlocked;
    }
    public boolean[] getUnlockedEssences() {
        return unlockedEssences;
    }
    public void setUnlockedEssences(boolean[] value) {
        unlockedEssences = value;
    }
    private boolean[] unlockedEssences = new boolean[] { true, true, true, true };
    public void updateData(ServerPlayer player) {
        Color linkColor = new Color(0, 0, 0, 0);
        BlockPos linkFrom = null;
        if (this.linkFrom != null) {
            linkFrom = this.linkFrom.getBlockPos();
            if (this.linkFrom instanceof BaseEssencePointBlockEntity linkFrom2) {
                linkColor = linkFrom2.linkColor();
            } else if (this.linkFrom instanceof BaseCapabilityPointBlockEntity linkFrom2) {
                linkColor = linkFrom2.linkColor();
            }
        }
        ModMessages.sendToPlayer(new PlayerDataSyncS2CPacket(unlockedEssences, linkFrom, linkColor), (player));
    }
    public void updateData(Player player) {
        updateData((ServerPlayer)player);
    }
    public void updateUnlockedEntries(ServerPlayer player) {
        ModMessages.sendToPlayer(new UnlockedEntrySyncS2CPacket(unlocked), (player));
    }
    public void updateUnlockedEntries(Player player) {
        updateUnlockedEntries((ServerPlayer)player);
    }
    public void sendTier(ServerPlayer player, boolean showIndicator) {
        ModMessages.sendToPlayer(new PlayerTierSyncS2CPacket(tier, showIndicator), player);
    }
    public void sendTier(Player player, boolean showIndicator) {
        sendTier((ServerPlayer)player, showIndicator);
    }
    public void copyFrom(PlayerModData source) {
        this.unlocked = source.unlocked;
        this.unlockedEssences = source.unlockedEssences;
    }
    public void saveNBTData(CompoundTag nbt) {
        if (!unlocked.isEmpty()) {
            ListTag tag = new ListTag();
            for (ResourceLocation o : unlocked) {
                CompoundTag tag2 = new CompoundTag();
                tag2.putString("value", o.toString());
                tag.add(tag2);
            }
            nbt.put("unlocked", tag);
        }
        nbt.putBoolean("essenceUnlocked", unlockedEssences[0]);
        nbt.putBoolean("lunarEssenceUnlocked", unlockedEssences[1]);
        nbt.putBoolean("naturalEssenceUnlocked", unlockedEssences[2]);
        nbt.putBoolean("exoticEssenceUnlocked", unlockedEssences[3]);
        nbt.putInt("tier", tier);
    }
    public void loadNBTData(CompoundTag nbt) {
        unlocked.clear();
        if (nbt.contains("unlocked")) {
            List<ResourceLocation> list = new ArrayList<>();
            for (Tag o : (ListTag)nbt.get("unlocked")) {
                list.add(ResourceLocation.of(((CompoundTag) o).getString("value"), ':'));
            }
            unlocked = list;
        }
        unlockedEssences = new boolean[] { nbt.getBoolean("essenceUnlocked"), nbt.getBoolean("lunarEssenceUnlocked"), nbt.getBoolean("naturalEssenceUnlocked"), nbt.getBoolean("exoticEssenceUnlocked") };
        tier = nbt.getInt("tier");
    }
}
