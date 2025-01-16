package com.cmdpro.datanessence.item.equipment;

import com.cmdpro.datanessence.api.block.RedirectorInteractable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class EssenceRedirector extends Item {

    public EssenceRedirector(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockEntity tile = world.getBlockEntity(context.getClickedPos());
        BlockState block = world.getBlockState(context.getClickedPos());

        if ( block.getBlock() instanceof RedirectorInteractable ri ) {
            if ( ri.onRedirectorUse(context) ) {
                return InteractionResult.SUCCESS;
            }
        }

        // as we do not currently have any suitable TEs i don't know if this one works.
        if ( tile instanceof RedirectorInteractable ri ) {
            if ( ri.onRedirectorUse(context) ) {
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }
}
