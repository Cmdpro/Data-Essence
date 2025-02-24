package com.cmdpro.datanessence.mixin;

import com.cmdpro.datanessence.item.equipment.TraversiteTrudgers;
import com.cmdpro.datanessence.registry.ItemRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    /**
     * The Traversite Trudgers prevent movement slowdowns. This actually carries that out, for blocks that affect one's move speed.
     */
    @Inject(method = "getBlockSpeedFactor", at = @At(value = "RETURN"), remap = false, cancellable = true)
    private void datanessence$applyTraversiteTrudgersBonus(CallbackInfoReturnable<Float> cir) {
        if ( ((Object) this instanceof LivingEntity living) && TraversiteTrudgers.areTrudgersEquipped(living) ) {
            cir.setReturnValue(Math.max(cir.getReturnValue(), 1F));
        }
    }

    /**
     * The Traversite Trudgers prevent movement slowdowns. This actually carries that out, for blocks you can get stuck inside.
     */
    @ModifyVariable(method = "makeStuckInBlock", at = @At(value = "LOAD"), argsOnly = true)
    private Vec3 datanessence$applyTraversiteTrudgersBlockBonus(Vec3 mult) {
        if ( ((Object) this instanceof LivingEntity living) && TraversiteTrudgers.areTrudgersEquipped(living) )
            return Vec3.ZERO;
        return mult;
    }
}
