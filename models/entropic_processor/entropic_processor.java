// Made with Blockbench 4.11.1
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class entropic_processor<T extends EntropicProcessor> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "entropic_processor"), "main");
	private final ModelPart casing;
	private final ModelPart grinder_left;
	private final ModelPart grinder_right;
	private final ModelPart grinder_bottom;

	public entropic_processor(ModelPart root) {
		this.casing = root.getChild("casing");
		this.grinder_left = root.getChild("grinder_left");
		this.grinder_right = root.getChild("grinder_right");
		this.grinder_bottom = root.getChild("grinder_bottom");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition casing = partdefinition.addOrReplaceChild("casing", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -10.0F, -8.0F, 16.0F, 10.0F, 16.0F, new CubeDeformation(0.0F))
		.texOffs(0, 60).addBox(-8.0F, -16.0F, 6.0F, 14.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 26).addBox(6.0F, -16.0F, -6.0F, 2.0F, 6.0F, 14.0F, new CubeDeformation(0.0F))
		.texOffs(32, 60).addBox(-6.0F, -16.0F, -8.0F, 14.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(32, 26).addBox(-8.0F, -16.0F, -8.0F, 2.0F, 6.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition grinder_left = partdefinition.addOrReplaceChild("grinder_left", CubeListBuilder.create(), PartPose.offset(3.0576F, 10.5F, -0.0025F));

		PartDefinition wheel_left_7_r1 = grinder_left.addOrReplaceChild("wheel_left_7_r1", CubeListBuilder.create().texOffs(64, 35).addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5576F, 0.0F, -5.2475F, 0.0F, 0.0F, -0.829F));

		PartDefinition wheel_left_6_r1 = grinder_left.addOrReplaceChild("wheel_left_6_r1", CubeListBuilder.create().texOffs(64, 30).addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3076F, 0.0F, -3.7475F, 0.0F, 0.0F, -0.6981F));

		PartDefinition wheel_left_5_r1 = grinder_left.addOrReplaceChild("wheel_left_5_r1", CubeListBuilder.create().texOffs(64, 25).addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3076F, 0.0F, -2.2475F, 0.0F, 0.0F, -0.5672F));

		PartDefinition wheel_left_4_r1 = grinder_left.addOrReplaceChild("wheel_left_4_r1", CubeListBuilder.create().texOffs(64, 20).addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.0576F, 0.0F, -0.7475F, 0.0F, 0.0F, -0.4363F));

		PartDefinition wheel_left_3_r1 = grinder_left.addOrReplaceChild("wheel_left_3_r1", CubeListBuilder.create().texOffs(64, 15).addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1924F, 0.0F, 0.7525F, 0.0F, 0.0F, -0.3054F));

		PartDefinition wheel_left_2_r1 = grinder_left.addOrReplaceChild("wheel_left_2_r1", CubeListBuilder.create().texOffs(64, 10).addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1924F, 0.0F, 2.2525F, 0.0F, 0.0F, -0.1745F));

		PartDefinition wheel_left_1_r1 = grinder_left.addOrReplaceChild("wheel_left_1_r1", CubeListBuilder.create().texOffs(64, 5).addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4424F, 0.0F, 3.7525F, 0.0F, 0.0F, -0.0436F));

		PartDefinition wheel_left_0_r1 = grinder_left.addOrReplaceChild("wheel_left_0_r1", CubeListBuilder.create().texOffs(64, 0).addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4424F, 0.0F, 5.2525F, 0.0F, 0.0F, 0.0873F));

		PartDefinition axle_left_r1 = grinder_left.addOrReplaceChild("axle_left_r1", CubeListBuilder.create().texOffs(0, 46).addBox(0.0F, -2.0F, -12.0F, 1.0F, 1.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.0576F, 1.5F, 5.5025F, 0.0F, 0.0873F, 0.0F));

		PartDefinition grinder_right = partdefinition.addOrReplaceChild("grinder_right", CubeListBuilder.create(), PartPose.offset(-3.0576F, 10.5F, -0.0025F));

		PartDefinition wheel_right_7_r1 = grinder_right.addOrReplaceChild("wheel_right_7_r1", CubeListBuilder.create().texOffs(40, 68).addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5576F, 0.0F, -5.2475F, 0.0F, 0.0F, 0.829F));

		PartDefinition wheel_right_6_r1 = grinder_right.addOrReplaceChild("wheel_right_6_r1", CubeListBuilder.create().texOffs(30, 68).addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3076F, 0.0F, -3.7475F, 0.0F, 0.0F, 0.6981F));

		PartDefinition wheel_right_5_r1 = grinder_right.addOrReplaceChild("wheel_right_5_r1", CubeListBuilder.create().texOffs(20, 68).addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3076F, 0.0F, -2.2475F, 0.0F, 0.0F, 0.5672F));

		PartDefinition wheel_right_4_r1 = grinder_right.addOrReplaceChild("wheel_right_4_r1", CubeListBuilder.create().texOffs(10, 68).addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0576F, 0.0F, -0.7475F, 0.0F, 0.0F, 0.4363F));

		PartDefinition wheel_right_3_r1 = grinder_right.addOrReplaceChild("wheel_right_3_r1", CubeListBuilder.create().texOffs(0, 68).addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1924F, 0.0F, 0.7525F, 0.0F, 0.0F, 0.3054F));

		PartDefinition wheel_right_2_r1 = grinder_right.addOrReplaceChild("wheel_right_2_r1", CubeListBuilder.create().texOffs(64, 65).addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1924F, 0.0F, 2.2525F, 0.0F, 0.0F, 0.1745F));

		PartDefinition wheel_right_1_r1 = grinder_right.addOrReplaceChild("wheel_right_1_r1", CubeListBuilder.create().texOffs(64, 60).addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4424F, 0.0F, 3.7525F, 0.0F, 0.0F, 0.0436F));

		PartDefinition wheel_right_0_r1 = grinder_right.addOrReplaceChild("wheel_right_0_r1", CubeListBuilder.create().texOffs(64, 40).addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4424F, 0.0F, 5.2525F, 0.0F, 0.0F, -0.0873F));

		PartDefinition axle_right_r1 = grinder_right.addOrReplaceChild("axle_right_r1", CubeListBuilder.create().texOffs(28, 46).addBox(-1.0F, -2.0F, -12.0F, 1.0F, 1.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0576F, 1.5F, 5.5025F, 0.0F, -0.0873F, 0.0F));

		PartDefinition grinder_bottom = partdefinition.addOrReplaceChild("grinder_bottom", CubeListBuilder.create().texOffs(56, 46).addBox(-0.5F, -0.5F, -6.9444F, 1.0F, 1.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.25F, 12.0F, 0.1944F));

		PartDefinition wheel_bottom_7_r1 = grinder_bottom.addOrReplaceChild("wheel_bottom_7_r1", CubeListBuilder.create().texOffs(24, 73).addBox(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -5.1944F, 0.0F, 0.0F, -1.3963F));

		PartDefinition wheel_bottom_6_r1 = grinder_bottom.addOrReplaceChild("wheel_bottom_6_r1", CubeListBuilder.create().texOffs(16, 73).addBox(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -3.6944F, 0.0F, 0.0F, -1.1781F));

		PartDefinition wheel_bottom_5_r1 = grinder_bottom.addOrReplaceChild("wheel_bottom_5_r1", CubeListBuilder.create().texOffs(8, 73).addBox(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -2.1944F, 0.0F, 0.0F, -0.9163F));

		PartDefinition wheel_bottom_4_r1 = grinder_bottom.addOrReplaceChild("wheel_bottom_4_r1", CubeListBuilder.create().texOffs(0, 73).addBox(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -0.6944F, 0.0F, 0.0F, -0.7854F));

		PartDefinition wheel_bottom_3_r1 = grinder_bottom.addOrReplaceChild("wheel_bottom_3_r1", CubeListBuilder.create().texOffs(50, 72).addBox(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.8056F, 0.0F, 0.0F, -0.5672F));

		PartDefinition wheel_bottom_2_r1 = grinder_bottom.addOrReplaceChild("wheel_bottom_2_r1", CubeListBuilder.create().texOffs(66, 70).addBox(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 2.3056F, 0.0F, 0.0F, -0.3927F));

		PartDefinition wheel_bottom_1_r1 = grinder_bottom.addOrReplaceChild("wheel_bottom_1_r1", CubeListBuilder.create().texOffs(58, 70).addBox(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 3.8056F, 0.0F, 0.0F, -0.2618F));

		PartDefinition wheel_bottom_0_r1 = grinder_bottom.addOrReplaceChild("wheel_bottom_0_r1", CubeListBuilder.create().texOffs(50, 68).addBox(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 5.3056F, 0.0F, 0.0F, -0.1309F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(EntropicProcessor entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		casing.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		grinder_left.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		grinder_right.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		grinder_bottom.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}