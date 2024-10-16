package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SoundRegistry {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, DataNEssence.MOD_ID);

    public static final Supplier<SoundEvent> CRITICAL_DATA_UNLOCKED = SOUND_EVENTS.register("critical_data_unlocked",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "critical_data_unlocked")));
}
