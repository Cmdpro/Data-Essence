package com.cmdpro.datanessence.api.block;

import net.minecraft.world.item.context.UseOnContext;

public interface RedirectorInteractable {
    /**
     * This is called whenever a player right-clicks a block with an Essence Redirector.
     * @return whether the use succeeded
     */
    abstract boolean onRedirectorUse(UseOnContext context);
}
