package com.cmdpro.datanessence.renderers.block;

import com.cmdpro.databank.model.DatabankEntityModel;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityModel;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityRenderer;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.block.generation.IndustrialPlantSiphon;
import com.cmdpro.datanessence.block.generation.IndustrialPlantSiphonBlockEntity;
import com.cmdpro.datanessence.registry.EssenceTypeRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class IndustrialPlantSiphonRenderer extends DatabankBlockEntityRenderer<IndustrialPlantSiphonBlockEntity> {
    public static final ModelLayerLocation modelLocation = new ModelLayerLocation(DataNEssence.locate("industrial_plant_siphon"), "main");

    public IndustrialPlantSiphonRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new Model(rendererProvider.getModelSet().bakeLayer(modelLocation)));
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return DataNEssence.locate("textures/block/industrial_plant_siphon.png");
    }

    @Override
    public void render(IndustrialPlantSiphonBlockEntity tile, float pPartialTick, PoseStack poseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        Direction facing = tile.getBlockState().getValue(IndustrialPlantSiphon.FACING);
        Vec3 rotateAround = new Vec3(0.5, 0.5, 0.5);

        if (facing.equals(Direction.NORTH))
            poseStack.rotateAround(Axis.YP.rotationDegrees(180), (float) rotateAround.x, (float) rotateAround.y, (float) rotateAround.z);
        if (facing.equals(Direction.SOUTH))
            poseStack.rotateAround(Axis.YP.rotationDegrees(0), (float) rotateAround.x, (float) rotateAround.y, (float) rotateAround.z);
        if (facing.equals(Direction.EAST))
            poseStack.rotateAround(Axis.YP.rotationDegrees(90), (float) rotateAround.x, (float) rotateAround.y, (float) rotateAround.z);
        if (facing.equals(Direction.WEST))
            poseStack.rotateAround(Axis.YP.rotationDegrees(-90), (float) rotateAround.x, (float) rotateAround.y, (float) rotateAround.z);

        super.render(tile, pPartialTick, poseStack, pBufferSource, pPackedLight, pPackedOverlay);
    }

    public static class Model extends DatabankBlockEntityModel<IndustrialPlantSiphonBlockEntity> {
        public static DatabankEntityModel model;
        public static AnimationDefinition idle;
        public static AnimationDefinition working;
        private final ModelPart root;

        public Model(ModelPart root) {
            this.root = root.getChild("root");
        }

        public static DatabankEntityModel getModel() {
            if (model == null) {
                model = DatabankModels.models.get(DataNEssence.locate("industrial_plant_siphon"));
                idle = model.animations.get("idle").createAnimationDefinition();
                working = model.animations.get("working").createAnimationDefinition();
            }
            return model;
        }

        public static LayerDefinition createLayer() {
            return getModel().createLayerDefinition();
        }

        public void setupAnim(IndustrialPlantSiphonBlockEntity pEntity) {
            if (pEntity.essenceGenerationTicks > 0 && !(pEntity.storage.getEssence(EssenceTypeRegistry.ESSENCE.get()) + IndustrialPlantSiphonBlockEntity.essenceProduced > pEntity.storage.getMaxEssence())) {
                this.animate(pEntity.animState, working, 1.0f);
            } else {
                this.animate(pEntity.animState, idle, 1.0f);
            }
            pEntity.animState.startIfStopped((int)getAgeInTicks());
        }

        @Override
        public ModelPart root() {
            return root;
        }
    }
}
