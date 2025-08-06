package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.datanessence.block.storage.FluidTankBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import static com.cmdpro.datanessence.util.RenderHelper.renderQuad;

public class FluidTankRenderer implements BlockEntityRenderer<FluidTankBlockEntity> {
    @Override
    public void render(FluidTankBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        FluidStack fluid = pBlockEntity.getFluidHandler().getFluidInTank(0);
        int max = pBlockEntity.getFluidHandler().getTankCapacity(0);
        float fill = ((float)fluid.getAmount()/(float)max);
        if (fill > 0) {
            IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluid.getFluid());
            ResourceLocation stillTexture = fluidTypeExtensions.getStillTexture(fluid);
            if (stillTexture != null) {
                TextureAtlasSprite sprite =
                        Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture);
                int tintColor = fluidTypeExtensions.getTintColor(fluid);
                VertexConsumer builder = pBuffer.getBuffer(ItemBlockRenderTypes.getRenderLayer(fluid.getFluid().defaultFluidState()));
                pPoseStack.pushPose();
                pPoseStack.translate(0.5, 0.5, 0.5);
                pPoseStack.scale(0.999f, 0.999f, 0.999f);
                float u0 = sprite.getU0();
                float u1 = sprite.getU1();
                float v0 = sprite.getV0();
                float v1 = sprite.getV1();

                renderQuad(builder, pPoseStack, -0.5f, -0.5f+fill, -0.5f, 0.5f, -0.5f+fill, 0.5f, u0, v0, u1, v1, pPackedLight, tintColor);

                v0 += ((1f-fill)*(v1-v0));

                renderQuad(builder, pPoseStack, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f+fill, 0.5f, u0, v0, u1, v1, pPackedLight, tintColor);
                renderQuad(builder, pPoseStack, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f+fill, -0.5f, u0, v0, u1, v1, pPackedLight, tintColor);

                //Rotate the pose stack because I failed to figure out how to make it work with vertex positioning
                pPoseStack.mulPose(Axis.YP.rotationDegrees(90));
                renderQuad(builder, pPoseStack, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f+fill, 0.5f, u0, v0, u1, v1, pPackedLight, tintColor);
                renderQuad(builder, pPoseStack, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f+fill, -0.5f, u0, v0, u1, v1, pPackedLight, tintColor);

                pPoseStack.popPose();
            }
        }
    }
    EntityRenderDispatcher renderDispatcher;
    public FluidTankRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        renderDispatcher = rendererProvider.getEntityRenderer();
    }
}
