package com.cmdpro.datanessence;

import com.cmdpro.databank.music.MusicController;
import com.cmdpro.databank.music.MusicManager;
import com.cmdpro.datanessence.api.util.client.ClientRenderingUtil;
import com.cmdpro.datanessence.api.util.item.EssenceChargeableItemUtil;
import com.cmdpro.datanessence.entity.BlackHole;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
import com.cmdpro.datanessence.registry.DataComponentRegistry;
import com.cmdpro.datanessence.registry.ItemRegistry;
import com.cmdpro.datanessence.registry.MobEffectRegistry;
import com.cmdpro.datanessence.shaders.DataNEssenceCoreShaders;
import com.cmdpro.datanessence.shaders.DataNEssenceRenderTypes;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.text.DecimalFormat;
import java.util.List;

import static com.mojang.blaze3d.platform.GlConst.GL_DRAW_FRAMEBUFFER;

@EventBusSubscriber(value = Dist.CLIENT, modid = DataNEssence.MOD_ID)
public class ClientEvents {
    public static RenderTarget tempRenderTarget;
    private static String format(float number) {
        DecimalFormat format = new DecimalFormat("#.0");
        String num = format.format(number);
        if (num.charAt(0) == '.') {
            num = "0" + num;
        }
        return num;
    }
    @SubscribeEvent
    public static void itemTooltip(ItemTooltipEvent event) {
        float maxExoticEssence = EssenceChargeableItemUtil.getMaxExoticEssence(event.getItemStack());
        if (maxExoticEssence > 0) {
            event.getToolTip().add(1, Component.translatable("gui.essence_bar.exotic_essence_with_max", format(EssenceChargeableItemUtil.getExoticEssence(event.getItemStack())), format(maxExoticEssence)));
        }
        float maxNaturalEssence = EssenceChargeableItemUtil.getMaxNaturalEssence(event.getItemStack());
        if (maxNaturalEssence > 0) {
            event.getToolTip().add(1, Component.translatable("gui.essence_bar.natural_essence_with_max", format(EssenceChargeableItemUtil.getNaturalEssence(event.getItemStack())), format(maxNaturalEssence)));
        }
        float maxLunarEssence = EssenceChargeableItemUtil.getMaxLunarEssence(event.getItemStack());
        if (maxLunarEssence > 0) {
            event.getToolTip().add(1, Component.translatable("gui.essence_bar.lunar_essence_with_max", format(EssenceChargeableItemUtil.getLunarEssence(event.getItemStack())), format(maxLunarEssence)));
        }
        float maxEssence = EssenceChargeableItemUtil.getMaxEssence(event.getItemStack());
        if (maxEssence > 0) {
            event.getToolTip().add(1, Component.translatable("gui.essence_bar.essence_with_max", format(EssenceChargeableItemUtil.getEssence(event.getItemStack())), format(maxEssence)));
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
                ClientRenderingUtil.renderLine(Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(DataNEssenceRenderTypes.WIRES), event.getPoseStack(), pos1, pos2, ClientPlayerData.getLinkColor());
                event.getPoseStack().popPose();
            }
        }
        if (event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_PARTICLES)) {
            MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            copyBuffers();
            DataNEssenceCoreShaders.WARPING_POINT.setSampler("DepthBuffer", tempRenderTarget.getDepthTextureId());
            DataNEssenceCoreShaders.WARPING_POINT.setSampler("ColorBuffer", tempRenderTarget.getColorTextureId());
            event.getPoseStack().pushPose();
            event.getPoseStack().translate(-event.getCamera().getPosition().x, -event.getCamera().getPosition().y, -event.getCamera().getPosition().z);
            for (Entity i : Minecraft.getInstance().level.entitiesForRendering()) {
                if (i instanceof BlackHole hole) {
                    Vec3 pos = i.getBoundingBox().getCenter();
                    ClientRenderingUtil.renderBlackHole(event.getPoseStack(), pos, bufferSource, hole.getEntityData().get(BlackHole.SIZE), 16, 16);
                    ClientRenderingUtil.renderBlackHole(event.getPoseStack(), pos, bufferSource, -hole.getEntityData().get(BlackHole.SIZE), 16, 16);
                }
            }
            event.getPoseStack().popPose();
        }
        if (event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_LEVEL)) {
            if (Minecraft.getInstance().player != null) {
                ClientModEvents.genderEuphoriaShader.setActive(Minecraft.getInstance().player.hasEffect(MobEffectRegistry.GENDER_EUPHORIA));
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

    public static SimpleSoundInstance musicDiscPlayerMusic;
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event)
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null)
        {
            boolean playMusic = false;
            SoundEvent mus = null;
            if (Minecraft.getInstance().player != null) {
                for (int index = 0; index < Minecraft.getInstance().player.getInventory().getContainerSize(); index++) {
                    ItemStack slot = Minecraft.getInstance().player.getInventory().getItem(index);
                    if (slot.is(ItemRegistry.MUSIC_DISC_PLAYER.get())) {
                        ResourceKey<SoundEvent> key = slot.get(DataComponentRegistry.PLAYING_MUSIC);
                        if (key != null) {
                            SoundEvent sound = BuiltInRegistries.SOUND_EVENT.get(key);
                            if (sound != null) {
                                mus = sound;
                                playMusic = true;
                                break;
                            }
                        }
                    }
                }
            }
            SoundManager manager = mc.getSoundManager();
            if (manager.isActive(musicDiscPlayerMusic))
            {
                if (!playMusic)
                {
                    manager.stop(musicDiscPlayerMusic);
                } else {
                    if (!musicDiscPlayerMusic.getLocation().equals(mus.getLocation())) {
                        manager.stop(musicDiscPlayerMusic);
                    }
                }
            }
            if (!manager.isActive(musicDiscPlayerMusic))
            {
                if (!manager.isActive(musicDiscPlayerMusic) && playMusic)
                {
                    musicDiscPlayerMusic = new SimpleSoundInstance(
                        mus.getLocation(),
                        SoundSource.RECORDS,
                        1.0F,
                        1.0F,
                        SoundInstance.createUnseededRandom(),
                        false,
                        0,
                        SoundInstance.Attenuation.NONE,
                        0.0,
                        0.0,
                        0.0,
                        true
                    );
                    manager.play(musicDiscPlayerMusic);
                }
            }
        }
    }
}
