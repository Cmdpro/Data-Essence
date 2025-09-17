package com.cmdpro.datanessence.client;

import com.cmdpro.databank.ClientDatabankUtils;
import com.cmdpro.databank.Databank;
import com.cmdpro.databank.misc.RenderingUtil;
import com.cmdpro.databank.multiblock.Multiblock;
import com.cmdpro.databank.rendering.ColorUtil;
import com.cmdpro.databank.rendering.RenderHandler;
import com.cmdpro.databank.rendering.RenderProjectionUtil;
import com.cmdpro.databank.rendering.ShaderHelper;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.essence.EssenceBarBackgroundTypes;
import com.cmdpro.datanessence.api.essence.EssenceBlockEntity;
import com.cmdpro.datanessence.api.essence.EssenceType;
import com.cmdpro.datanessence.api.item.ItemEssenceContainer;
import com.cmdpro.datanessence.api.pearlnetwork.PearlNetworkBlockEntity;
import com.cmdpro.datanessence.api.util.client.ClientEssenceBarUtil;
import com.cmdpro.datanessence.api.util.client.ClientRenderingUtil;
import com.cmdpro.datanessence.block.auxiliary.EssenceReaderBlockEntity;
import com.cmdpro.datanessence.client.gui.PingsGuiLayer;
import com.cmdpro.datanessence.client.particle.CircleParticleOptions;
import com.cmdpro.datanessence.client.particle.MoteParticleOptions;
import com.cmdpro.datanessence.config.DataNEssenceClientConfig;
import com.cmdpro.datanessence.data.pinging.PingableStructure;
import com.cmdpro.datanessence.entity.BlackHole;
import com.cmdpro.datanessence.item.equipment.EssenceMeter;
import com.cmdpro.datanessence.item.equipment.AntiGravityPack;
import com.cmdpro.datanessence.item.equipment.GrapplingHook;
import com.cmdpro.datanessence.moddata.ClientPlayerData;
import com.cmdpro.datanessence.data.pinging.StructurePing;
import com.cmdpro.datanessence.networking.ModMessages;
import com.cmdpro.datanessence.networking.packet.c2s.RequestMachineEssenceValue;
import com.cmdpro.datanessence.networking.packet.s2c.AddScannedOre;
import com.cmdpro.datanessence.networking.packet.s2c.MachineEssenceValueSync;
import com.cmdpro.datanessence.registry.*;
import com.cmdpro.datanessence.client.shaders.DataNEssenceCoreShaders;
import com.cmdpro.datanessence.client.shaders.DataNEssenceRenderTypes;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.apache.commons.lang3.RandomUtils;
import org.joml.Math;
import org.joml.Vector3d;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

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
    public static float time;
    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (tempRenderTarget == null) {
            tempRenderTarget = new TextureTarget(mc.getMainRenderTarget().width, mc.getMainRenderTarget().height, true, Minecraft.ON_OSX);
        }
        float shaderTime = time + (Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true) / 20f);
        DataNEssenceCoreShaders.ESSENCE_BRIDGE.safeGetUniform("Time").set(shaderTime);
        if (event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_WEATHER)) {
            MultiBufferSource.BufferSource bufferSource = RenderHandler.createBufferSource();
            if (ClientPlayerData.getLinkPos() != null) {
                BlockEntity blockEntity = mc.level.getBlockEntity(ClientPlayerData.getLinkPos());
                Vec3 pos = event.getCamera().getPosition();
                Vec3 pos1 = ClientPlayerData.getLinkPos().getCenter();
                Vec3 pos2 = mc.player.getRopeHoldPosition(event.getPartialTick().getGameTimeDeltaPartialTick(true));
                Color color = ClientPlayerData.getLinkColor();
                event.getPoseStack().pushPose();
                event.getPoseStack().translate(-pos.x, -pos.y, -pos.z);
                if (blockEntity instanceof PearlNetworkBlockEntity ent) {
                    Vec3 currentPos = pos1;
                    Vec3 target = pos2;
                    VertexConsumer vertexConsumer = RenderHandler.createBufferSource().getBuffer(DataNEssenceRenderTypes.WIRES);
                    Vec3 normal = currentPos.subtract(target).normalize();
                    vertexConsumer.addVertex(event.getPoseStack().last(), (float) currentPos.x, (float) currentPos.y, (float) currentPos.z).setColor(color.getRGB()).setNormal(event.getPoseStack().last(), (float) normal.x, (float) normal.y, (float) normal.z);
                    currentPos = target;
                    vertexConsumer.addVertex(event.getPoseStack().last(), (float) currentPos.x, (float) currentPos.y, (float) currentPos.z).setColor(color.getRGB()).setNormal(event.getPoseStack().last(), (float) normal.x, (float) normal.y, (float) normal.z);
                } else {
                    ClientRenderingUtil.renderLine(bufferSource.getBuffer(DataNEssenceRenderTypes.WIRES), event.getPoseStack(), pos1, pos2, color);
                }
                event.getPoseStack().popPose();
            }
            for (Player i : mc.level.players()) {
                GrapplingHook.GrapplingHookData grapplingHookData = i.getData(AttachmentTypeRegistry.GRAPPLING_HOOK_DATA).orElse(null);
                if (grapplingHookData != null) {
                    Vec3 pos = event.getCamera().getPosition();
                    Vec3 pos1 = grapplingHookData.pos;
                    Vec3 pos2 = i.getRopeHoldPosition(event.getPartialTick().getGameTimeDeltaPartialTick(true));
                    Color color = new Color(255, 255, 255);
                    float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(true);
                    if (i.getMainHandItem().getItem() instanceof GrapplingHook hook) {
                        color = hook.getHookColor(i, i.getMainHandItem(), partialTick);
                    } else if (i.getOffhandItem().getItem() instanceof GrapplingHook hook) {
                        color = hook.getHookColor(i, i.getOffhandItem(), partialTick);
                    }
                    event.getPoseStack().pushPose();
                    event.getPoseStack().translate(-pos.x, -pos.y, -pos.z);
                    ClientRenderingUtil.renderLine(bufferSource.getBuffer(DataNEssenceRenderTypes.WIRES), event.getPoseStack(), pos1, pos2, color, 0.05d);
                    event.getPoseStack().popPose();
                }
            }
            if (mc.player != null) {
                if (mc.player.isHolding((stack) -> stack.getItem() instanceof GrapplingHook && ItemEssenceContainer.getEssence(stack, GrapplingHook.FUEL_ESSENCE_TYPE) >= 5) && mc.player.getData(AttachmentTypeRegistry.GRAPPLING_HOOK_DATA).isEmpty()) {
                    HitResult hit = mc.player.pick(35, 0, false);
                    if (hit.getType() != HitResult.Type.MISS) {
                        long gameTime = mc.level.getGameTime();
                        float fade = (Math.sin((gameTime+Minecraft.getInstance().getTimer().getGameTimeDeltaTicks())/15f)+1f)/2f;
                        Vec3 pos = event.getCamera().getPosition();
                        Vec3 pos1 = hit.getLocation();
                        Vec3 pos2 = mc.player.getRopeHoldPosition(event.getPartialTick().getGameTimeDeltaPartialTick(true));
                        event.getPoseStack().pushPose();
                        event.getPoseStack().translate(-pos.x, -pos.y, -pos.z);
                        Color color = Color.WHITE;
                        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(true);
                        if (mc.player.getMainHandItem().getItem() instanceof GrapplingHook hook) {
                            color = hook.getHookColor(mc.player, mc.player.getMainHandItem(), partialTick);
                        } else if (mc.player.getOffhandItem().getItem() instanceof GrapplingHook hook) {
                            color = hook.getHookColor(mc.player, mc.player.getOffhandItem(), partialTick);
                        }
                        color = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)((0.5f-(fade/4))*255));
                        ClientRenderingUtil.renderLine(bufferSource.getBuffer(DataNEssenceRenderTypes.WIRES), event.getPoseStack(), pos1, pos2, color, 0.05d);
                        event.getPoseStack().popPose();
                    }
                }
                if (mc.hitResult instanceof BlockHitResult blockHitResult) {

                    // Render the Essence Reader's selected essence above it
                    if (mc.level.getBlockEntity(blockHitResult.getBlockPos()) instanceof EssenceReaderBlockEntity reader) {
                        Optional<EssenceType> type;
                        if (reader.selectedEssences != null) {
                            type = reader.selectedEssences.stream().findFirst();
                        } else {
                            type = Optional.empty();
                        }

                        int width = 10;
                        int height = 10;
                        float div = 24f;
                        float worldWidth = width / div;
                        float worldHeight = height / div;

                        if (type.isPresent()) {
                            RenderProjectionUtil.project((graphics) -> {
                                ClientEssenceBarUtil.drawEssenceIcon(graphics, 0, 0, type.get());
                            }, (poseStack) -> {
                                Vec3 center = blockHitResult.getBlockPos().getCenter();
                                Vec2 angle = calculateRotationVector(center, Minecraft.getInstance().player.getEyePosition());

                                poseStack.pushPose();
                                poseStack.translate(center.x, center.y+0.1d, center.z);
                                poseStack.pushPose();
                                poseStack.mulPose(Axis.YP.rotationDegrees(-angle.y + 180));
                            }, (poseStack) -> {
                                poseStack.popPose();
                                poseStack.popPose();
                            }, bufferSource, new Vec3(-worldWidth / 2f, worldHeight / 2f, 0), new Vec3(worldWidth / 2f, worldHeight / 2f, 0), new Vec3(worldWidth / 2f, -worldHeight / 2f, 0), new Vec3(-worldWidth / 2f, -worldHeight / 2f, 0), width, height);
                            ;
                        }
                    }

                    // Essence Meter rendering
                    if (mc.player.isHolding((item) -> item.getItem() instanceof EssenceMeter)) {
                        if (EssenceMeter.currentMachineEssenceValue != null && EssenceMeter.currentMachineEssenceValue.pos.equals(blockHitResult.getBlockPos())) {
                            if (mc.level.getBlockEntity(blockHitResult.getBlockPos()) instanceof EssenceBlockEntity essenceBlockEntity) {
                                List<EssenceType> supported = EssenceMeter.currentMachineEssenceValue.values.keySet().stream().sorted(Comparator.comparing((i) -> i.tier)).toList();
                                Map<EssenceType, Float> values = EssenceMeter.currentMachineEssenceValue.values;
                                float max = EssenceMeter.currentMachineEssenceValue.maxValue;
                                float shift = -(supported.size() - 1f) / 2f;
                                int width = 13 * supported.size();
                                int height = 54 + 11;
                                float worldWidth = width / 96f;
                                float worldHeight = height / 96f;
                                RenderProjectionUtil.project((graphics) -> {
                                    float shift2 = shift;
                                    for (EssenceType i : supported) {
                                        ClientEssenceBarUtil.drawEssenceIcon(graphics, (int) (((width / 2f) - 4f) + (shift2 * 13)), 0, i, essenceBlockEntity.getMeterBackgroundType(), false, ClientPlayerData.getUnlockedEssences().getOrDefault(DataNEssenceRegistries.ESSENCE_TYPE_REGISTRY.getKey(i), false));
                                        ClientEssenceBarUtil.drawEssenceBar(graphics, (int) (((width / 2f) - 4f) + (shift2 * 13)) + 1, 12, i, values.get(i), max, essenceBlockEntity.getMeterBackgroundType());
                                        shift2 += 1;
                                    }
                                }, (poseStack) -> {
                                    Vec3 center = blockHitResult.getBlockPos().getCenter();
                                    Vec2 angle = calculateRotationVector(center, Minecraft.getInstance().player.getEyePosition());
                                    boolean horizontal = true;
                                    if (mc.player.getEyePosition().y >= center.y + essenceBlockEntity.getMeterSideLength(Direction.UP)) {
                                        if (mc.level.getBlockState(blockHitResult.getBlockPos().above()).isAir()) {
                                            center = center.add(0, (essenceBlockEntity.getMeterSideLength(Direction.UP)+essenceBlockEntity.getMeterRenderOffset(Direction.UP)), 0);
                                            horizontal = false;
                                        }
                                    } else if (mc.player.getEyePosition().y <= center.y - essenceBlockEntity.getMeterSideLength(Direction.DOWN)) {
                                        if (mc.level.getBlockState(blockHitResult.getBlockPos().below()).isAir()) {
                                            center = center.add(0, -(essenceBlockEntity.getMeterSideLength(Direction.DOWN) + essenceBlockEntity.getMeterRenderOffset(Direction.DOWN)), 0);
                                            horizontal = false;
                                        }
                                    }
                                    if (horizontal) {
                                        Direction direction = Direction.fromYRot(angle.y);
                                        float offset = (essenceBlockEntity.getMeterSideLength(direction)+essenceBlockEntity.getMeterRenderOffset(direction));
                                        center = center.add(direction.getStepX()*offset, 0, direction.getStepZ()*offset);
                                    }
                                    poseStack.pushPose();
                                    poseStack.translate(center.x, center.y, center.z);
                                    poseStack.pushPose();
                                    poseStack.mulPose(Axis.YP.rotationDegrees(-angle.y + 180));
                                }, (poseStack) -> {
                                    poseStack.popPose();
                                    poseStack.popPose();
                                }, bufferSource, new Vec3(-worldWidth / 2f, worldHeight / 2f, 0), new Vec3(worldWidth / 2f, worldHeight / 2f, 0), new Vec3(worldWidth / 2f, -worldHeight / 2f, 0), new Vec3(-worldWidth / 2f, -worldHeight / 2f, 0), width, height);
                            }
                        }
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
    private static Vec2 calculateRotationVector(Vec3 pVec, Vec3 pTarget) {
        double d0 = pTarget.x - pVec.x;
        double d1 = pTarget.y - pVec.y;
        double d2 = pTarget.z - pVec.z;
        double d3 = java.lang.Math.sqrt(d0 * d0 + d2 * d2);
        return new Vec2(
                Mth.wrapDegrees((float)(-(Mth.atan2(d1, d3) * (double)(180F / (float) java.lang.Math.PI)))),
                Mth.wrapDegrees((float)(Mth.atan2(d2, d0) * (double)(180F / (float) java.lang.Math.PI)) - 90.0F)
        );
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
            RenderingUtil.renderAdvancedBeaconBeam(poseStack, bufferSource, BeaconRenderer.BEAM_LOCATION, event.getPartialTick().getGameTimeDeltaPartialTick(true), 1f, gameTime, posLow, posHigh, color, 0.25f, 0.3f);
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
        time += 1/20f;
        Minecraft mc = Minecraft.getInstance();
        var player = mc.player;
        if (mc.level != null)
        {
            for (Map.Entry<StructurePing, Integer> i : pings.entrySet().stream().toList()) {
                if (i.getValue()-1 <= 0) {
                    pings.remove(i.getKey());
                } else {
                    pings.put(i.getKey(), i.getValue()-1);
                }
            }
            if (mc.player != null) {
                boolean playedASound = false;
                for (Map.Entry<BlockPos, Integer> i : AddScannedOre.scanned.entrySet().stream().toList()) {
                    int value = i.getValue() - 1;
                    if (i.getValue() > 0 && value <= 0) {
                        if (!playedASound) {
                            mc.level.playSound(mc.player, i.getKey(), SoundRegistry.SCAN_ORE.value(), SoundSource.PLAYERS, 1f, 1f);
                            playedASound = true;
                        }
                    }
                    if (value <= -100) {
                        AddScannedOre.scanned.remove(i.getKey());
                    } else {
                        AddScannedOre.scanned.put(i.getKey(), value);
                    }
                }
            }
            boolean playMusic = false;
            SoundEvent mus = null;
            if (mc.player != null) {
                if (mc.hitResult instanceof BlockHitResult blockHitResult) {
                    if (mc.player.isHolding((item) -> item.getItem() instanceof EssenceMeter)) {
                        if (mc.level.getBlockEntity(blockHitResult.getBlockPos()) instanceof EssenceBlockEntity) {
                            ModMessages.sendToServer(new RequestMachineEssenceValue(blockHitResult.getBlockPos()));
                        }
                    }
                }
                GrapplingHook.GrapplingHookData grapplingHookData = player.getData(AttachmentTypeRegistry.GRAPPLING_HOOK_DATA).orElse(null);
                if (grapplingHookData != null) {
                    if (mc.player.position().distanceTo(grapplingHookData.pos) > 0) {
                        Vector3d direction = new Vector3d(grapplingHookData.pos.subtract(mc.player.position()).toVector3f()).normalize();
                        double theta = direction.angle(new Vector3d(0, 1, 0));
                        double angle = Math.toDegrees(theta);
                        double distanceToUp = Math.abs(180-(angle > 180 ? 180-(angle-180) : angle));
                        double intensity = 1d;
                        if (distanceToUp <= 90) {
                            intensity = distanceToUp/90d;
                        }
                        double centripetalAcceleration = mc.player.getDeltaMovement().lengthSqr() / (1d + grapplingHookData.distance);
                        double mass = 1;
                        double gravity = mc.player.getGravity();
                        Vector3d tension = new Vector3d(direction).mul(mass * (centripetalAcceleration + gravity * Math.cos(theta)));
                        Vector3d force = new Vector3d(tension).div(mass);
                        mc.player.setDeltaMovement(mc.player.getDeltaMovement().add(new Vec3(force.x, force.y > 0 ? force.y*intensity : force.y, force.z)));
                    }
                }
                for (int index = 0; index < player.getInventory().getContainerSize(); index++) {
                    ItemStack slot = player.getInventory().getItem(index);
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
                if (AntiGravityPack.shouldRemoveGravity(player)) {
                    player.setDeltaMovement(player.getDeltaMovement().multiply(1, 0, 1));

                    var random = mc.level.getRandom();
                    int lifetime = RandomUtils.nextInt(10, 60);

                    for (int i = 0; i < 5; i++) {
                        float hOff = random.nextFloat() * 0.1f;
                        hOff = random.nextInt() % 2 == 0 ? hOff : -hOff;
                        float xVel = random.nextFloat() * 0.08f;
                        xVel = random.nextInt() % 2 == 0 ? xVel : -xVel;
                        float zVel = random.nextFloat() * 0.08f;
                        zVel = random.nextInt() % 2 == 0 ? zVel : -zVel;

                        var fx = new CircleParticleOptions().setFriction(1f).setLifetime(lifetime).setAdditive(true).setColor(new Color(EssenceTypeRegistry.LUNAR_ESSENCE.get().getColor()));
                        mc.particleEngine.createParticle(fx, player.position().x+hOff, player.position().y+hOff, player.position().z+hOff, xVel, -1, zVel);
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
            AddScannedOre.scanned.clear();
        }
    }
}
