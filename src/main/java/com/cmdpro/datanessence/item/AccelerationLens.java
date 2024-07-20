package com.cmdpro.datanessence.item;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.ILaserEmitterModule;
import com.cmdpro.datanessence.block.LaserEmitter;
import com.cmdpro.datanessence.block.entity.LaserEmitterBlockEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.joml.Vector3f;

public class AccelerationLens extends Item implements ILaserEmitterModule {
    public AccelerationLens(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void applyToMob(LaserEmitterBlockEntity ent, LivingEntity entity) {
        if (entity instanceof Player player) {
            player.hurtMarked = true;
        }
        Vector3f vec = ent.getBlockState().getValue(LaserEmitter.FACING).step();
        entity.push(vec.x, vec.y, vec.z);
    }
}
