package com.cmdpro.datanessence;

import com.cmdpro.datanessence.api.ClientDataNEssenceUtil;
import com.cmdpro.datanessence.entity.BlackHole;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
import com.cmdpro.datanessence.shaders.DataNEssenceCoreShaders;
import com.cmdpro.datanessence.shaders.system.ShaderInstance;
import com.cmdpro.datanessence.shaders.system.ShaderManager;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import static com.mojang.blaze3d.platform.GlConst.GL_DRAW_FRAMEBUFFER;

@EventBusSubscriber(value = Dist.CLIENT, modid = DataNEssence.MOD_ID)
public class ClientEvents {
    public static RenderTarget tempRenderTarget;
    public static SimpleSoundInstance music;
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (tempRenderTarget == null) {
            tempRenderTarget = new TextureTarget(Minecraft.getInstance().getMainRenderTarget().width, Minecraft.getInstance().getMainRenderTarget().height, true, Minecraft.ON_OSX);
        }
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
        if (event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_PARTICLES)) {
            copyBuffers();
            DataNEssenceCoreShaders.WARPINGPOINT.instance.setSampler("DepthBuffer", tempRenderTarget.getDepthTextureId());
            DataNEssenceCoreShaders.WARPINGPOINT.instance.setSampler("ColorBuffer", tempRenderTarget.getColorTextureId());
            event.getPoseStack().pushPose();
            event.getPoseStack().translate(-event.getCamera().getPosition().x, -event.getCamera().getPosition().y, -event.getCamera().getPosition().z);
            for (Entity i : Minecraft.getInstance().level.entitiesForRendering()) {
                if (i instanceof BlackHole hole) {
                    Vec3 pos = i.getBoundingBox().getCenter();
                    ClientDataNEssenceUtil.renderBlackHole(event.getPoseStack(), pos, Minecraft.getInstance().renderBuffers().bufferSource(), hole.getEntityData().get(BlackHole.SIZE), 16, 16);
                    ClientDataNEssenceUtil.renderBlackHole(event.getPoseStack(), pos, Minecraft.getInstance().renderBuffers().bufferSource(), -hole.getEntityData().get(BlackHole.SIZE), 16, 16);
                }
            }
            event.getPoseStack().popPose();
        }
        if (event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_LEVEL)) {
            for (ShaderInstance i : ShaderManager.instances) {
                i.process();
            }
        }
    }
    public static void copyBuffers() {
        if (tempRenderTarget == null) return;
        RenderTarget mainRenderTarget = Minecraft.getInstance().getMainRenderTarget();
        tempRenderTarget.copyDepthFrom(mainRenderTarget);
        copyColorFrom(tempRenderTarget, mainRenderTarget);

        GlStateManager._glBindFramebuffer(GL_DRAW_FRAMEBUFFER, mainRenderTarget.frameBufferId);
    }
    public static void copyColorFrom(RenderTarget tempRenderTarget, RenderTarget renderTarget) {
        RenderSystem.assertOnRenderThreadOrInit();
        GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, renderTarget.frameBufferId);
        GlStateManager._glBindFramebuffer(GL_DRAW_FRAMEBUFFER, tempRenderTarget.frameBufferId);
        GlStateManager._glBlitFrameBuffer(0, 0, renderTarget.width, renderTarget.height, 0, 0, tempRenderTarget.width, tempRenderTarget.height, GlConst.GL_COLOR_BUFFER_BIT, GlConst.GL_NEAREST);
        GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, 0);
    }
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent event)
    {
        for (ShaderInstance i : ShaderManager.instances) {
            i.tick();
        }/*
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
