package com.cmdpro.datanessence.renderers.item;

import com.cmdpro.databank.model.DatabankEntityModel;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.model.item.DatabankItemModel;
import com.cmdpro.databank.model.item.DatabankItemRenderer;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.item.blockitem.ChargerItem;
import com.cmdpro.datanessence.item.blockitem.InfuserItem;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.item.ItemStack;

public class InfuserItemRenderer extends DatabankItemRenderer<InfuserItem> {
    public static final ModelLayerLocation modelLocation = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "infuser_item"), "main");
    public InfuserItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet, new Model(modelSet.bakeLayer(modelLocation)));
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "textures/block/infuser.png");
    }

    public static class Model extends DatabankItemModel<InfuserItem> {
        public static AnimationDefinition hand;
        public AnimationState animState = new AnimationState();
        private final ModelPart root;

        public Model(ModelPart pRoot) {
            this.root = pRoot.getChild("root");
        }
        public static DatabankEntityModel model;
        public static DatabankEntityModel getModel() {
            if (model == null) {
                model = DatabankModels.models.get(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "infuser"));
                hand = model.animations.get("hand").createAnimationDefinition();
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