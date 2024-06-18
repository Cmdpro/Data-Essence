package com.cmdpro.datanessence;

import com.cmdpro.datanessence.api.ClientDataNEssenceUtil;
import com.cmdpro.datanessence.init.ItemInit;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
import com.cmdpro.datanessence.shaders.system.ShaderInstance;
import com.cmdpro.datanessence.shaders.system.ShaderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
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
import org.joml.Quaternionf;
import software.bernie.geckolib.util.RenderUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = DataNEssence.MOD_ID)
public class ClientEvents {
    public static SimpleSoundInstance music;
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES)) {
            if (ClientPlayerData.getLinkPos() != null) {
                Vec3 pos = event.getCamera().getPosition();
                Vec3 pos1 = ClientPlayerData.getLinkPos().getCenter();
                Vec3 pos2 = Minecraft.getInstance().player.getRopeHoldPosition(event.getPartialTick());
                event.getPoseStack().pushPose();
                event.getPoseStack().translate(-pos.x, -pos.y, -pos.z);
                ClientDataNEssenceUtil.renderBeam(event.getPoseStack(), Minecraft.getInstance().renderBuffers().bufferSource(), BeaconRenderer.BEAM_LOCATION, event.getPartialTick(), 1.0f, Minecraft.getInstance().level.getGameTime(), pos1, pos2, ClientPlayerData.getLinkColor(), 0.025f, 0.03f);
                event.getPoseStack().popPose();
            }
        }
        if (event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_LEVEL)) {
            for (ShaderInstance i : ShaderManager.instances) {
                i.process();
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
