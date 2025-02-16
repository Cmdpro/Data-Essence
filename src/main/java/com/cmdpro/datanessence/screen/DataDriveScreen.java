package com.cmdpro.datanessence.screen;

import com.cmdpro.datanessence.moddata.ClientPlayerUnlockedEntries;
import com.cmdpro.datanessence.networking.ModMessages;
import com.cmdpro.datanessence.networking.packet.PlayerChangeDriveDataC2SPacket;
import com.cmdpro.datanessence.datatablet.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

public class DataDriveScreen extends DataTabletScreen {
    public DataDriveScreen(Component pTitle, boolean offhand) {
        super(pTitle);
        this.offhand = offhand;
    }
    public boolean offhand;
    @Override
    public boolean clickEntry(Entry entry) {
        ModMessages.sendToServer(new PlayerChangeDriveDataC2SPacket(entry.id, !ClientPlayerUnlockedEntries.getUnlocked().contains(entry.id) && ClientPlayerUnlockedEntries.getIncomplete().contains(entry.id), offhand));
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        onClose();
        return true;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}
