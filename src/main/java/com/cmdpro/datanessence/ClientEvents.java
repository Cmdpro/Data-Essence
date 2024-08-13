package com.cmdpro.datanessence;

import com.cmdpro.datanessence.api.util.client.ClientRenderingUtil;
import com.cmdpro.datanessence.api.util.EssenceUtil;
import com.cmdpro.datanessence.entity.BlackHole;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
import com.cmdpro.datanessence.renderers.other.MultiblockRenderer;
import com.cmdpro.datanessence.shaders.DataNEssenceCoreShaders;
import com.cmdpro.datanessence.shaders.system.ShaderInstance;
import com.cmdpro.datanessence.shaders.system.ShaderManager;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import static com.mojang.blaze3d.platform.GlConst.GL_DRAW_FRAMEBUFFER;

@EventBusSubscriber(value = Dist.CLIENT, modid = DataNEssence.MOD_ID)
public class ClientEvents {
    public static RenderTarget tempRenderTarget;
    public static SimpleSoundInstance music;
    @SubscribeEvent
    public static void itemTooltip(ItemTooltipEvent event) {
        float maxExoticEssence = EssenceUtil.ItemUtil.EssenceChargeableItemUtil.getMaxExoticEssence(event.getItemStack());
        if (maxExoticEssence > 0) {
            event.getToolTip().add(1, Component.translatable("gui.essence_bar.exotic_essence_with_max", EssenceUtil.ItemUtil.EssenceChargeableItemUtil.getExoticEssence(event.getItemStack()), maxExoticEssence));
        }
        float maxNaturalEssence = EssenceUtil.ItemUtil.EssenceChargeableItemUtil.getMaxNaturalEssence(event.getItemStack());
        if (maxNaturalEssence > 0) {
            event.getToolTip().add(1, Component.translatable("gui.essence_bar.natural_essence_with_max", EssenceUtil.ItemUtil.EssenceChargeableItemUtil.getNaturalEssence(event.getItemStack()), maxNaturalEssence));
        }
        float maxLunarEssence = EssenceUtil.ItemUtil.EssenceChargeableItemUtil.getMaxLunarEssence(event.getItemStack());
        if (maxLunarEssence > 0) {
            event.getToolTip().add(1, Component.translatable("gui.essence_bar.lunar_essence_with_max", EssenceUtil.ItemUtil.EssenceChargeableItemUtil.getLunarEssence(event.getItemStack()), maxLunarEssence));
        }
        float maxEssence = EssenceUtil.ItemUtil.EssenceChargeableItemUtil.getMaxEssence(event.getItemStack());
        if (maxEssence > 0) {
            event.getToolTip().add(1, Component.translatable("gui.essence_bar.essence_with_max", EssenceUtil.ItemUtil.EssenceChargeableItemUtil.getEssence(event.getItemStack()), maxEssence));
        }
    }
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (tempRenderTarget == null) {
            tempRenderTarget = new TextureTarget(Minecraft.getInstance().getMainRenderTarget().width, Minecraft.getInstance().getMainRenderTarget().height, true, Minecraft.ON_OSX);
        }
        if (event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES)) {
            if (ClientPlayerData.getLinkPos() != null) {
                Vec3 pos = event.getCamera().getPosition();
                Vec3 pos1 = ClientPlayerData.getLinkPos().getCenter();
                Vec3 pos2 = Minecraft.getInstance().player.getRopeHoldPosition(event.getPartialTick().getGameTimeDeltaPartialTick(true));
                event.getPoseStack().pushPose();
                event.getPoseStack().translate(-pos.x, -pos.y, -pos.z);
                ClientRenderingUtil.renderBeam(event.getPoseStack(), Minecraft.getInstance().renderBuffers().bufferSource(), BeaconRenderer.BEAM_LOCATION, event.getPartialTick().getGameTimeDeltaPartialTick(true), 1.0f, Minecraft.getInstance().level.getGameTime(), pos1, pos2, ClientPlayerData.getLinkColor(), 0.025f, 0.03f);
                event.getPoseStack().popPose();
            }
        }
        if (event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_PARTICLES)) {
            MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            copyBuffers();
            DataNEssenceCoreShaders.WARPINGPOINT.instance.setSampler("DepthBuffer", tempRenderTarget.getDepthTextureId());
            DataNEssenceCoreShaders.WARPINGPOINT.instance.setSampler("ColorBuffer", tempRenderTarget.getColorTextureId());
            event.getPoseStack().pushPose();
            event.getPoseStack().translate(-event.getCamera().getPosition().x, -event.getCamera().getPosition().y, -event.getCamera().getPosition().z);
            for (Entity i : Minecraft.getInstance().level.entitiesForRendering()) {
                if (i instanceof BlackHole hole) {
                    Vec3 pos = i.getBoundingBox().getCenter();
                    ClientRenderingUtil.renderBlackHole(event.getPoseStack(), pos, bufferSource, hole.getEntityData().get(BlackHole.SIZE), 16, 16);
                    ClientRenderingUtil.renderBlackHole(event.getPoseStack(), pos, bufferSource, -hole.getEntityData().get(BlackHole.SIZE), 16, 16);
                }
            }
            MultiblockRenderer.renderCurrentMultiblock(event.getPoseStack(), event.getPartialTick());
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
    public static void onClientTickPre(ClientTickEvent.Pre event)
    {
        for (ShaderInstance i : ShaderManager.instances) {
            i.tick();
        }
    }
    @SubscribeEvent
    public static void onClientTickPost(ClientTickEvent.Post event)
    {
        Minecraft mc = Minecraft.getInstance();
        if (MultiblockRenderer.multiblock != null) {
            if (MultiblockRenderer.multiblockPos != null) {
                if (MultiblockRenderer.multiblock.checkMultiblock(mc.level, MultiblockRenderer.multiblockPos)) {
                    MultiblockRenderer.multiblock = null;
                    MultiblockRenderer.multiblockPos = null;
                }
            }
        }
        /*
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
