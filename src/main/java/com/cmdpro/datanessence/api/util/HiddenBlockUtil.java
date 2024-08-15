package com.cmdpro.datanessence.api.util;

import com.cmdpro.datanessence.hiddenblocks.HiddenBlock;
import com.cmdpro.datanessence.hiddenblocks.HiddenBlocksManager;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class HiddenBlockUtil {
    public static BlockState getHiddenBlock(Block block, Player player) {
        List<ResourceLocation> unlocked = player.getData(AttachmentTypeRegistry.UNLOCKED);
        List<ResourceLocation> incomplete = player.getData(AttachmentTypeRegistry.INCOMPLETE);
        for (HiddenBlock i : HiddenBlocksManager.blocks.values()) {
            if (i.originalBlock == null || i.hiddenAs == null || i.entry == null) {
                continue;
            }
            if (i.originalBlock.equals(block)) {
                if (i.completionRequired) {
                    if (incomplete.contains(i.entry)) {
                        return i.hiddenAs;
                    }
                    if (!unlocked.contains(i.entry)) {
                        return i.hiddenAs;
                    }
                } else {
                    if (!incomplete.contains(i.entry) && !unlocked.contains(i.entry)) {
                        return i.hiddenAs;
                    }
                }
                break;
            }
        }
        return null;
    }

    public static BlockState getHiddenBlock(Block block) {
        for (HiddenBlock i : HiddenBlocksManager.blocks.values()) {
            if (i.originalBlock == null || i.hiddenAs == null || i.entry == null) {
                continue;
            }
            if (i.originalBlock.equals(block)) {
                return i.hiddenAs;
            }
        }
        return null;
    }
}
