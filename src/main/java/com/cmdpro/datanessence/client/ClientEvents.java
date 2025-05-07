package com.cmdpro.datanessence.client;

import com.cmdpro.databank.ClientDatabankUtils;
import com.cmdpro.databank.rendering.ColorUtil;
import com.cmdpro.databank.rendering.RenderHandler;
import com.cmdpro.databank.rendering.ShaderHelper;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.item.ItemEssenceContainer;
import com.cmdpro.datanessence.api.util.client.ClientRenderingUtil;
import com.cmdpro.datanessence.client.gui.PingsGuiLayer;
import com.cmdpro.datanessence.config.DataNEssenceClientConfig;
import com.cmdpro.datanessence.data.pinging.PingableStructure;
import com.cmdpro.datanessence.entity.BlackHole;
import com.cmdpro.datanessence.item.equipment.GrapplingHook;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
import com.cmdpro.datanessence.data.pinging.StructurePing;
import com.cmdpro.datanessence.registry.*;
import com.cmdpro.datanessence.client.shaders.DataNEssenceCoreShaders;
import com.cmdpro.datanessence.client.shaders.DataNEssenceRenderTypes;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.joml.Math;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Map;

import static com.cmdpro.datanessence.client.gui.PingsGuiLayer.pings;
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
        float maxEssence = ItemEssenceContainer.getMaxEssence(event.getItemStack());
        for (ResourceLocation i : ItemEssenceContainer.getSupportedEssenceTypes(event.getItemStack())) {
            event.getToolTip().add(1, Component.translatable(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.get(i).tooltipKeyWithMax, format(ItemEssenceContainer.getEssence(event.getItemStack(), i)), format(maxEssence)));
        }
    }
    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (tempRenderTarget == null) {
            tempRenderTarget = new TextureTarget(mc.getMainRenderTarget().width, mc.getMainRenderTarget().height, true, Minecraft.ON_OSX);
        }
        if (event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_WEATHER)) {
            MultiBufferSource.BufferSource bufferSource = RenderHandler.createBufferSource();
            if (ClientPlayerData.getLinkPos() != null) {
                Vec3 pos = event.getCamera().getPosition();
                Vec3 pos1 = ClientPlayerData.getLinkPos().getCenter();
                Vec3 pos2 = mc.player.getRopeHoldPosition(event.getPartialTick().getGameTimeDeltaPartialTick(true));
                event.getPoseStack().pushPose();
                event.getPoseStack().translate(-pos.x, -pos.y, -pos.z);
                ClientRenderingUtil.renderLine(bufferSource.getBuffer(DataNEssenceRenderTypes.WIRES), event.getPoseStack(), pos1, pos2, ClientPlayerData.getLinkColor());
                event.getPoseStack().popPose();
            }
            for (Player i : mc.level.players()) {
                GrapplingHook.GrapplingHookData grapplingHookData = i.getData(AttachmentTypeRegistry.GRAPPLING_HOOK_DATA).orElse(null);
                if (grapplingHookData != null) {
                    Vec3 pos = event.getCamera().getPosition();
                    Vec3 pos1 = grapplingHookData.pos;
                    Vec3 pos2 = i.getRopeHoldPosition(event.getPartialTick().getGameTimeDeltaPartialTick(true));
                    event.getPoseStack().pushPose();
                    event.getPoseStack().translate(-pos.x, -pos.y, -pos.z);
                    ClientRenderingUtil.renderLine(bufferSource.getBuffer(DataNEssenceRenderTypes.WIRES), event.getPoseStack(), pos1, pos2, new Color(EssenceTypeRegistry.ESSENCE.get().color), 0.05d);
                    event.getPoseStack().popPose();
                }
            }
            if (mc.player != null) {
                if (mc.player.isHolding((stack) -> stack.getItem() instanceof GrapplingHook && ItemEssenceContainer.getEssence(stack, GrapplingHook.FUEL_ESSENCE_TYPE) >= 5) && mc.player.getData(AttachmentTypeRegistry.GRAPPLING_HOOK_DATA).isEmpty()) {
                    HitResult hit = mc.player.pick(35, 0, false);
                    if (hit.getType() != HitResult.Type.MISS) {
                        Vec3 pos = event.getCamera().getPosition();
                        Vec3 pos1 = hit.getLocation();
                        Vec3 pos2 = mc.player.getRopeHoldPosition(event.getPartialTick().getGameTimeDeltaPartialTick(true));
                        event.getPoseStack().pushPose();
                        event.getPoseStack().translate(-pos.x, -pos.y, -pos.z);
                        Color color = new Color(EssenceTypeRegistry.ESSENCE.get().color);
                        color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 150);
                        ClientRenderingUtil.renderLine(bufferSource.getBuffer(DataNEssenceRenderTypes.WIRES), event.getPoseStack(), pos1, pos2, color, 0.05d);
                        event.getPoseStack().popPose();
                    }
                }
            }
            if (!ShaderHelper.shouldUseAlternateRendering()) {
                renderObjects(event);
            }
        }
        if (event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_LEVEL)) {
            if (ShaderHelper.shouldUseAlternateRendering()) {
                RenderSystem.getModelViewStack().pushMatrix().set(RenderHandler.matrix4f);
                RenderSystem.applyModelViewMatrix();
                RenderSystem.setShaderFogStart(RenderHandler.fogStart);
                renderObjects(event);
                FogRenderer.setupNoFog();
                RenderSystem.getModelViewStack().popMatrix();
                RenderSystem.applyModelViewMatrix();
            }
            if (mc.player != null) {
                ClientModEvents.genderEuphoriaShader.setActive(DataNEssenceClientConfig.genderEuphoriaShader && mc.player.hasEffect(MobEffectRegistry.GENDER_EUPHORIA));
            }
        }
    }
    private static void renderObjects(RenderLevelStageEvent event) {
        copyBuffers();
        DataNEssenceCoreShaders.WARPING_POINT.setSampler("DepthBuffer", tempRenderTarget.getDepthTextureId());
        DataNEssenceCoreShaders.WARPING_POINT.setSampler("ColorBuffer", tempRenderTarget.getColorTextureId());
        MultiBufferSource.BufferSource bufferSource = RenderHandler.createBufferSource();
        var poseStack = event.getPoseStack();

        poseStack.pushPose();
        poseStack.translate(-event.getCamera().getPosition().x, -event.getCamera().getPosition().y, -event.getCamera().getPosition().z);
        for (Entity i : Minecraft.getInstance().level.entitiesForRendering()) {
            if (i instanceof BlackHole hole) {
                Vec3 pos = i.getBoundingBox().getCenter();
                ClientRenderingUtil.renderBlackHole(poseStack, pos, bufferSource, hole.getEntityData().get(BlackHole.SIZE), 16, 16);
                ClientRenderingUtil.renderBlackHole(poseStack, pos, bufferSource, -hole.getEntityData().get(BlackHole.SIZE), 16, 16);
            }
        }
        poseStack.popPose();

        for (var i : PingsGuiLayer.pings.entrySet()) {
            StructurePing ping = i.getKey();
            Level world = Minecraft.getInstance().level;
            Vec3 pos = ping.pos.getCenter();
            Vec3 posLow = new Vec3(pos.x, world.getMinBuildHeight(), pos.z);
            Vec3 posHigh = new Vec3(pos.x, world.getMaxBuildHeight(), pos.z);
            PingableStructure structure = ping.getPingableStructure();
            Color color1 = (ping.known) ? structure.color1 : new Color(0xB100B1);
            Color color2 = (ping.known) ? structure.color2 : new Color(0xFF00FF);
            long gameTime = 0;
            if (Minecraft.getInstance().level != null)
                gameTime = Minecraft.getInstance().level.getGameTime();
            Color color = ColorUtil.blendColors(color1, color2, (Math.sin((gameTime+Minecraft.getInstance().getTimer().getGameTimeDeltaTicks())/15f)+1f)/2f);

            poseStack.pushPose();
            poseStack.translate(-event.getCamera().getPosition().x, -event.getCamera().getPosition().y, -event.getCamera().getPosition().z);
            ClientDatabankUtils.renderAdvancedBeaconBeam(poseStack, bufferSource, BeaconRenderer.BEAM_LOCATION, event.getPartialTick().getGameTimeDeltaPartialTick(true), 1f, gameTime, posLow, posHigh, color, 0.25f, 0.3f);
            poseStack.popPose();
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
            for (Map.Entry<StructurePing, Integer> i : pings.entrySet().stream().toList()) {
                if (i.getValue()-1 <= 0) {
                    pings.remove(i.getKey());
                } else {
                    pings.put(i.getKey(), i.getValue()-1);
                }
            }
            boolean playMusic = false;
            SoundEvent mus = null;
            if (mc.player != null) {
                GrapplingHook.GrapplingHookData grapplingHookData = mc.player.getData(AttachmentTypeRegistry.GRAPPLING_HOOK_DATA).orElse(null);
                if (grapplingHookData != null) {
                    Vector3d direction = new Vector3d(grapplingHookData.pos.subtract(mc.player.position()).toVector3f()).normalize();
                    double theta = direction.angle(new Vector3d(0, 1, 0));
                    double centripetalAcceleration = mc.player.getDeltaMovement().lengthSqr()/grapplingHookData.distance;
                    double mass = 1;
                    double gravity = mc.player.getGravity();
                    Vector3d tension = new Vector3d(direction).mul(mass * (centripetalAcceleration + gravity * Math.cos(theta)));
                    Vector3d force = new Vector3d(tension).div(mass);
                    mc.player.setDeltaMovement(mc.player.getDeltaMovement().add(force.x, force.y, force.z));
                }
                for (int index = 0; index < mc.player.getInventory().getContainerSize(); index++) {
                    ItemStack slot = mc.player.getInventory().getItem(index);
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
        } else {
            pings.clear();
        }
    }
}
