package com.cmdpro.datanessence.item.equipment;

import com.cmdpro.datanessence.api.block.RedirectorInteractable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class EssenceRedirector extends Item {

    public EssenceRedirector(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockEntity tile = world.getBlockEntity(context.getClickedPos());

        if ( tile instanceof RedirectorInteractable ) {
            if ( ((RedirectorInteractable) tile).onRedirectorUse(context) ) {
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }
}
