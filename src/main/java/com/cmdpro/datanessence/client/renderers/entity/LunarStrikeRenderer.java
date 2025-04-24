package com.cmdpro.datanessence.client.renderers.entity;

import com.cmdpro.databank.ClientDatabankUtils;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.entity.EssenceSlashProjectile;
import com.cmdpro.datanessence.entity.LunarStrike;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.awt.*;

public class LunarStrikeRenderer extends EntityRenderer<LunarStrike> {

    public LunarStrikeRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(LunarStrike pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
        ClientDatabankUtils.renderAdvancedBeaconBeam(pPoseStack, pBuffer, BeaconRenderer.BEAM_LOCATION, pPartialTick, 1.0f, pEntity.level().getGameTime(), Vec3.ZERO, new Vec3(0, ((float)pEntity.level().getMaxBuildHeight())-pEntity.position().y, 0), Color.YELLOW, 0.25f, 0.3f);
    }
    public ResourceLocation getTextureLocation(LunarStrike pEntity) {
        return null;
    }
}