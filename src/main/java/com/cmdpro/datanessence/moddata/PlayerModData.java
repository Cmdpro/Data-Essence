package com.cmdpro.datanessence.moddata;

import com.cmdpro.datanessence.networking.ModMessages;
import com.cmdpro.datanessence.networking.packet.PlayerDataSyncS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerModData {
    public PlayerModData() {
        unlocked = new ArrayList<>();
    }
    private List<ResourceLocation> unlocked;
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
        ModMessages.sendToPlayer(new PlayerDataSyncS2CPacket(unlockedEssences), (player));
    }
    public void updateData(Player player) {
        updateData((ServerPlayer)player);
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
    }
    public void loadNBTData(CompoundTag nbt) {
        unlocked.clear();
        if (nbt.contains("unlocked")) {
            List<ResourceLocation> list = new ArrayList<>();
            for (Tag o : (ListTag)nbt.get("unlocked")) {
                list.add(ResourceLocation.of(((CompoundTag) o).getString("value"), ':'));
            }
        }
        unlockedEssences = new boolean[] { nbt.getBoolean("essenceUnlocked"), nbt.getBoolean("lunarEssenceUnlocked"), nbt.getBoolean("naturalEssenceUnlocked"), nbt.getBoolean("exoticEssenceUnlocked") };
    }
}
