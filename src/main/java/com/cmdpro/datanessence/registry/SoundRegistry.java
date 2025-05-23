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
            () -> SoundEvent.createVariableRangeEvent(DataNEssence.locate("critical_data_unlocked")));
    public static final Supplier<SoundEvent> TIER_ADVANCED = SOUND_EVENTS.register("tier_advanced",
            () -> SoundEvent.createVariableRangeEvent(DataNEssence.locate("tier_advanced")));
    public static final Supplier<SoundEvent> ENTROPIC_PROCESSOR_WORKING = SOUND_EVENTS.register("entropic_processor_working",
            () -> SoundEvent.createVariableRangeEvent(DataNEssence.locate("entropic_processor_working")));
    public static final Supplier<SoundEvent> LOCATOR_PING = SOUND_EVENTS.register("locator_ping",
            () -> SoundEvent.createVariableRangeEvent(DataNEssence.locate("locator_ping")));
    public static final Supplier<SoundEvent> UI_CLICK = SOUND_EVENTS.register("ui_click",
            () -> SoundEvent.createVariableRangeEvent(DataNEssence.locate("ui_click")));
    public static final Supplier<SoundEvent> UNDER_THE_SKY = SOUND_EVENTS.register("under_the_sky",
            () -> SoundEvent.createVariableRangeEvent(DataNEssence.locate("under_the_sky")));
}
