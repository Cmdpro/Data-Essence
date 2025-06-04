package com.cmdpro.datanessence.client.renderers.item;

import com.cmdpro.databank.model.DatabankEntityModel;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.model.item.DatabankItemModel;
import com.cmdpro.databank.model.item.DatabankItemRenderer;

import com.cmdpro.datanessence.DataNEssence;

import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public class EssenceDerivationSpikeItemRenderer extends DatabankItemRenderer<BlockItem> {
    public static final ModelLayerLocation modelLocation = new ModelLayerLocation(DataNEssence.locate("essence_derivation_spike_item"), "main");
    public EssenceDerivationSpikeItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet, new Model(modelSet.bakeLayer(modelLocation)));
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return DataNEssence.locate("textures/block/essence_derivation_spike.png");
    }

    public static class Model extends DatabankItemModel<BlockItem> {
        public static AnimationDefinition hand;
        public AnimationState animState = new AnimationState();
        private final ModelPart root;

        public Model(ModelPart pRoot) {
            this.root = pRoot.getChild("root");
        }
        public static DatabankEntityModel model;
        public static DatabankEntityModel getModel() {
            if (model == null) {
                model = DatabankModels.models.get(DataNEssence.locate("essence_derivation_spike"));
                hand = model.animations.get("idle").createAnimationDefinition();
            }
            return model;
        }

        public static LayerDefinition createLayer() {
            return getModel().createLayerDefinition();
        }
        public void setupAnim(ItemStack pStack) {
            animState.startIfStopped((int)getAgeInTicks());
            this.animate(animState, hand, 1.0f);
        }

        @Override
        public ModelPart root() {
            return root;
        }
    }
}