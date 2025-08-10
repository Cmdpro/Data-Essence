package com.cmdpro.datanessence.client.renderers.block;

import com.cmdpro.datanessence.block.processing.DryingTableBlockEntity;
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
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import static com.cmdpro.datanessence.util.RenderHelper.renderQuad;

public class DryingTableRenderer implements BlockEntityRenderer<DryingTableBlockEntity> {
    EntityRenderDispatcher renderDispatcher;

    public DryingTableRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        renderDispatcher = rendererProvider.getEntityRenderer();
    }

    @Override
    public void render(DryingTableBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        FluidStack fluid = blockEntity.getFluidHandler().getFluidInTank(0);
        int max = blockEntity.getFluidHandler().getTankCapacity(0);
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
                poseStack.scale(0.8f, 0.8f, 0.8f);

                float u0 = sprite.getU0();
                float u1 = sprite.getU1();
                float v0 = sprite.getV0();
                float v1 = sprite.getV1();

                renderQuad(builder, poseStack, -0.5f, -0.5f+fill, -0.5f, 0.5f, -0.5f+fill, 0.5f, u0, v0, u1, v1, packedLight, tintColor);

                poseStack.popPose();
            }
        }

        ItemStack item = blockEntity.getItemHandler().getStackInSlot(0);

        if (item != ItemStack.EMPTY) {
            poseStack.pushPose();
            poseStack.translate(0.5d, 0.85d, 0.5d);
            //poseStack.mulPose(Axis.YP.rotationDegrees(blockEntity.getLevel().getLevelData().getGameTime() % 360));
            poseStack.scale(0.5f, 0.5f, 0.5f);
            Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, blockEntity.getLevel(), 0);
            poseStack.popPose();
        }
    }
}
