package com.cmdpro.datanessence.screen;

import com.cmdpro.datanessence.block.logic.SignalEmitterMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SignalEmitterScreen extends AbstractSignalScreen<SignalEmitterMenu> {
    public SignalEmitterScreen(SignalEmitterMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }
}
