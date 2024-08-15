package com.cmdpro.datanessence.api.util.client;

import com.cmdpro.datanessence.hiddenblocks.HiddenBlock;
import com.cmdpro.datanessence.hiddenblocks.HiddenBlocksManager;
import com.cmdpro.datanessence.moddata.ClientPlayerUnlockedEntries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ClientHiddenBlockUtil {
    public static BlockState getHiddenBlock(Block block) {
        for (HiddenBlock i : HiddenBlocksManager.blocks.values()) {
            if (i.originalBlock == null || i.hiddenAs == null || i.entry == null) {
                continue;
            }
            if (i.originalBlock.equals(block)) {
                if (i.completionRequired) {
                    if (ClientPlayerUnlockedEntries.getIncomplete().contains(i.entry)) {
                        return i.hiddenAs;
                    }
                    if (!ClientPlayerUnlockedEntries.getUnlocked().contains(i.entry)) {
                        return i.hiddenAs;
                    }
                } else {
                    if (!ClientPlayerUnlockedEntries.getIncomplete().contains(i.entry) && !ClientPlayerUnlockedEntries.getUnlocked().contains(i.entry)) {
                        return i.hiddenAs;
                    }
                }
                break;
            }
        }
        return null;
    }
}
