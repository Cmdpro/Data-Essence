package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.client.FactorySong;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SoundRegistry {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, DataNEssence.MOD_ID);

    //Misc
    public static final Holder<SoundEvent> CRITICAL_DATA_UNLOCKED = createBasicSound("critical_data_unlocked");
    public static final Holder<SoundEvent> TIER_ADVANCED = createBasicSound("tier_advanced");

    //UI
    public static final Holder<SoundEvent> UI_CLICK = createBasicSound("ui_click");
    public static final Holder<SoundEvent> COLOR_MIXING_MIX = createBasicSound("minigame.color_mixing.mix");
    public static final Holder<SoundEvent> MINESWEEPER_PLACE_FLAG = createBasicSound("minigame.minesweeper.place_flag");
    public static final Holder<SoundEvent> MINESWEEPER_REMOVE_FLAG = createBasicSound("minigame.minesweeper.remove_flag");
    public static final Holder<SoundEvent> MINESWEEPER_BREAK = createBasicSound("minigame.minesweeper.break");
    public static final Holder<SoundEvent> MINESWEEPER_EXPLODE = createBasicSound("minigame.minesweeper.explode");

    //Items
    public static final Holder<SoundEvent> LOCATOR_PING = createBasicSound("item.signal_tracker.ping");
    public static final Holder<SoundEvent> ORE_SCANNER_PING = createBasicSound("item.ore_scanner.ping");
    public static final Holder<SoundEvent> SCAN_ORE = createBasicSound("item.ore_scanner.scan_ore");
    public static final Holder<SoundEvent> WARP_CAPSULE_USE = createBasicSound("item.warp_capsule.use");
    public static final Holder<SoundEvent> WARP_CAPSULE_FAIL = createBasicSound("item.warp_capsule.fail");
    public static final Holder<SoundEvent> EQUIP_TRAVERSITE_TRUDGERS = createBasicSound("item.traversite_trudgers.equip");
    public static final Holder<SoundEvent> ESSENCE_BOMB_THROW = createBasicSound("item.essence_bomb.throw");
    public static final Holder<SoundEvent> LUNAR_ESSENCE_BOMB_THROW = createBasicSound("item.lunar_essence_bomb.throw");
    public static final Holder<SoundEvent> NATURAL_ESSENCE_BOMB_THROW = createBasicSound("item.natural_essence_bomb.throw");
    public static final Holder<SoundEvent> EXOTIC_ESSENCE_BOMB_THROW = createBasicSound("item.exotic_essence_bomb.throw");
    public static final Holder<SoundEvent> HAMMER_AND_CHISEL_MINE = createBasicSound("item.hammer_and_chisel.mine");
    public static final Holder<SoundEvent> COGNIZANT_CUBE_SHAKE = createBasicSound("item.cognizant_cube.shake");
    public static final Holder<SoundEvent> COGNIZANT_CUBE_MESSAGE = createBasicSound("item.cognizant_cube.message");

    //Blocks
    public static final Holder<SoundEvent> ENTROPIC_PROCESSOR_WORKING = createBasicSound("block.entropic_processor.working");
    public static final Holder<SoundEvent> NODE_LINK_FROM = createBasicSound("block.generic_node.link_from");
    public static final Holder<SoundEvent> NODE_LINK_TO = createBasicSound("block.generic_node.link_to");
    public static final Holder<SoundEvent> PEARL_NETWORK_LINK_FROM = createBasicSound("block.generic_pearl_network.link_from");
    public static final Holder<SoundEvent> PEARL_NETWORK_LINK_TO = createBasicSound("block.generic_pearl_network.link_to");
    public static final Holder<SoundEvent> VACUUM_PICKUP = createBasicSound("block.vacuum.pickup");
    public static final Holder<SoundEvent> AUTO_FABRICATOR_CRAFT = createBasicSound("block.auto_fabricator.craft");
    public static final Holder<SoundEvent> FABRICATOR_START = createBasicSound("block.fabricator.start");
    public static final Holder<SoundEvent> FABRICATOR_CRAFT = createBasicSound("block.fabricator.craft");
    public static final Holder<SoundEvent> INFUSER_CRAFT = createBasicSound("block.infuser.craft");
    public static final Holder<SoundEvent> MINERAL_PURIFICATION_CHAMBER_CRAFT = createBasicSound("block.mineral_purification_chamber.craft");
    public static final Holder<SoundEvent> FLUID_COLLECTOR_TICK = createBasicSound("block.fluid_collector.tick");
    public static final Holder<SoundEvent> FLUID_COLLECTOR_FILL = createBasicSound("block.fluid_collector.fill");
    public static final Holder<SoundEvent> FLUID_SPILLER_EMPTY = createBasicSound("block.fluid_spiller.empty");
    public static final Holder<SoundEvent> METAL_SHAPER_CRAFTING = createBasicSound("block.metal_shaper.crafting");
    public static final Holder<SoundEvent> ENDER_PEARL_ABSORB = createBasicSound("block.ender_pearl_capture.absorb");
    public static final Holder<SoundEvent> STRUCTURE_PROTECTOR_DEACTIVATE = createBasicSound("block.structure_protector.deactivate");
    public static final Holder<SoundEvent> STRUCTURE_PROTECTOR_REFRESH = createBasicSound("block.structure_protector.refresh");

    //Fluids
    public static final Holder<SoundEvent> GENDERFLUID_TRANSITION = createBasicSound("fluid.genderfluid.transition");

    //Mobs
    public static final Holder<SoundEvent> ANCIENT_SENTINEL_WALK = createBasicSound("entity.ancient_sentinel.walk");
    public static final Holder<SoundEvent> ANCIENT_SENTINEL_HURT = createBasicSound("entity.ancient_sentinel.hurt");
    public static final Holder<SoundEvent> ANCIENT_SENTINEL_DEATH = createBasicSound("entity.ancient_sentinel.death");
    public static final Holder<SoundEvent> ANCIENT_SENTINEL_SHOOT = createBasicSound("entity.ancient_sentinel.shoot");

    //Music
    public static final Holder<SoundEvent> UNDER_THE_SKY = createBasicSound("music.under_the_sky");

    // Factory Loops
    public static final Holder<SoundEvent> FABRICATOR_LOOP = createBasicFactoryLoopSound("ost.factory_loops.fabricator");
    public static final Holder<SoundEvent> LUNARIUM_LOOP = createBasicFactoryLoopSound("ost.factory_loops.lunarium");
    public static final Holder<SoundEvent> AGRICORE_LOOP = createBasicFactoryLoopSound("ost.factory_loops.agricore");
    public static final Holder<SoundEvent> STARFORGE_LOOP = createBasicFactoryLoopSound("ost.factory_loops.starforge");
    public static final Holder<SoundEvent> LEECH_LOOP = createBasicFactoryLoopSound("ost.factory_loops.essence_leech");
    public static final Holder<SoundEvent> DERIVATION_SPIKE_LOOP = createBasicFactoryLoopSound("ost.factory_loops.essence_derivation_spike");
    public static final Holder<SoundEvent> METAL_SHAPER_LOOP = createBasicFactoryLoopSound("ost.factory_loops.metal_shaper");

    public static Holder<SoundEvent> createBasicSound(String name) {
        return SOUND_EVENTS.register(name,
                () -> SoundEvent.createVariableRangeEvent(DataNEssence.locate(name)));
    }

    public static Holder<SoundEvent> createBasicFactoryLoopSound(String name) {
        var identifier = DataNEssence.locate(name);
        var sound = SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(identifier));
        if (Dist.CLIENT.isClient()) {
            FactorySong.addFactorySongSound(identifier);
        }
        return sound;
    }
}
