package com.cmdpro.datanessence;

import com.cmdpro.datanessence.api.ClientDataNEssenceUtil;
import com.cmdpro.datanessence.init.ItemInit;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import software.bernie.geckolib.util.RenderUtils;
import team.lodestar.lodestone.helpers.RenderHelper;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypeRegistry;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;

import java.awt.*;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = DataNEssence.MOD_ID)
public class ClientEvents {
    public static SimpleSoundInstance music;
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_WEATHER)) {
            if (ClientPlayerData.getLinkPos() != null) {
                VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld();
                Vec3 pos = event.getCamera().getPosition();
                Vec3 pos1 = ClientPlayerData.getLinkPos().getCenter();
                Vec3 pos2 = Minecraft.getInstance().player.getRopeHoldPosition(event.getPartialTick());
                event.getPoseStack().translate(-pos.x, -pos.y, -pos.z);
                builder.setColor(ClientPlayerData.getLinkColor())
                        .setRenderType(LodestoneRenderTypeRegistry.TRANSPARENT_TEXTURE.applyAndCache(new ResourceLocation(DataNEssence.MOD_ID, "textures/vfx/beam.png")))
                        .renderBeam(event.getPoseStack().last().pose(), pos1, pos2, 0.025f);
                event.getPoseStack().translate(pos.x, pos.y, pos.z);
            }
        }
    }
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event)
    {/*
        Minecraft mc = Minecraft.getInstance();
        if (event.phase == TickEvent.Phase.END && mc.level != null)
        {
            boolean playMusic = false;
            SoundEvent mus = SoundInit.RUNICOVERSEER.get();
            for (Entity i : mc.level.entitiesForRendering()) {
                if (i instanceof RunicOverseer overseer) {
                    playMusic = true;
                    mus = SoundInit.RUNICOVERSEER.get();
                    if (overseer.getEntityData().get(RunicOverseer.INTRO)) {
                        mus = SoundInit.RUNICOVERSEERINTRO.get();
                    }
                }
            }
            SoundManager manager = mc.getSoundManager();
            if (manager.isActive(music))
            {
                mc.getMusicManager().stopPlaying();
                if (!playMusic)
                {
                    manager.stop(music);
                }
                if (!music.getLocation().equals(mus.getLocation())) {
                    manager.stop(music);
                }
            }
            if (!manager.isActive(music) && playMusic)
            {
                music = SimpleSoundInstance.forMusic(mus);
                manager.play(music);
            }
        }*/
    }
}
