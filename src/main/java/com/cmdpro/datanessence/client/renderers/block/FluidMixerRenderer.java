package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.databank.model.DatabankModel;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityModel;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityRenderer;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.processing.DryingTableBlockEntity;
import com.cmdpro.datanessence.block.processing.FluidMixerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import static com.cmdpro.datanessence.util.RenderHelper.renderQuad;

public class FluidMixerRenderer extends DatabankBlockEntityRenderer<FluidMixerBlockEntity> {
    float itemAngle;


    public FluidMixerRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new FluidMixerRenderer.Model());
    }

    @Override
    public void render(FluidMixerBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        super.render(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);

        FluidStack fluid = blockEntity.getOutputHandler().getFluidInTank(0);
        int max = blockEntity.getOutputHandler().getTankCapacity(0);
        float fill = ((float)fluid.getAmount()/(float)max);

        if (fill > 0) {
            IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluid.getFluid());
            ResourceLocation stillTexture = fluidTypeExtensions.getStillTexture(fluid);

            if (stillTexture != null) {
                TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture);
                int tintColor = fluidTypeExtensions.getTintColor(fluid);
                VertexConsumer builder = bufferSource.getBuffer(ItemBlockRenderTypes.getRenderLayer(fluid.getFluid().defaultFluidState()));

                poseStack.pushPose();
                poseStack.translate(0.5f, 0.5f, 0.5f);
                poseStack.scale(0.87f, 0.8f, 0.87f);

                float u0 = sprite.getU0();
                float u1 = sprite.getU1();
                float v0 = sprite.getV0();
                float v1 = sprite.getV1();

                renderQuad(builder, poseStack, -0.5f, -0.5f+fill, -0.5f, 0.5f, -0.5f+fill, 0.5f, u0, v0, u1, v1, packedLight, tintColor);

                v0 += ((1f-fill)*(v1-v0));

                renderQuad(builder, poseStack, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f+fill, 0.5f, u0, v0, u1, v1, packedLight, tintColor);
                renderQuad(builder, poseStack, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f+fill, -0.5f, u0, v0, u1, v1, packedLight, tintColor);
                poseStack.mulPose(Axis.YP.rotationDegrees(90));
                renderQuad(builder, poseStack, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f+fill, 0.5f, u0, v0, u1, v1, packedLight, tintColor);
                renderQuad(builder, poseStack, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f+fill, -0.5f, u0, v0, u1, v1, packedLight, tintColor);

                poseStack.popPose();
            }
        }

        ItemStack item = blockEntity.getItemHandler().getStackInSlot(0);

        if (item != ItemStack.EMPTY) {
            poseStack.pushPose();
            poseStack.translate(0.5d, 0.5f, 0.5d);
            poseStack.scale(0.65f, 0.65f, 0.65f);
            if (blockEntity.animState.isCurrentAnim("working"))
                itemAngle = -((blockEntity.age + partialTick) / 20f / (float) (Math.PI * 2) * 360f);
            poseStack.mulPose(Axis.YP.rotationDegrees(itemAngle));

            Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, blockEntity.getLevel(), 0);
            poseStack.popPose();
        }
    }

    public static class Model extends DatabankBlockEntityModel<FluidMixerBlockEntity> {
        @Override
        public ResourceLocation getTextureLocation() {
            return DataNEssence.locate("textures/block/fluid_mixer.png");
        }

        @Override
        public void setupModelPose(FluidMixerBlockEntity pEntity, float partialTick) {
            pEntity.animState.updateAnimDefinitions(getModel());
            if (pEntity.workTime >= 0) {
                pEntity.animState.setAnim("working");
            } else {
                pEntity.animState.setAnim("idle");
            }
            animate(pEntity.animState);
        }

        public DatabankModel model;
        public DatabankModel getModel() {
            if (model == null) {
                model = DatabankModels.models.get(DataNEssence.locate("fluid_mixer"));
            }
            return model;
        }
    }
}
