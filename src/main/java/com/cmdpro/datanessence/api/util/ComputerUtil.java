package com.cmdpro.datanessence.api.util;

import com.cmdpro.datanessence.data.computers.ComputerData;
import com.cmdpro.datanessence.networking.ModMessages;
import com.cmdpro.datanessence.networking.packet.s2c.ComputerDataSync;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class ComputerUtil {
    public static void openComputer(ServerPlayer player, ComputerData data) {
        ModMessages.sendToPlayer(new ComputerDataSync(data), (player));
    }

    public static void openComputer(Player player, ComputerData data) {
        ComputerUtil.openComputer((ServerPlayer) player, data);
    }
}
