// Made with Blockbench 4.10.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class wings<T extends Player> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("modid", "wings"), "main");
	private final ModelPart root;
	private final ModelPart wings;
	private final ModelPart left_wing;
	private final ModelPart left_wing_upper_arm;
	private final ModelPart left_wing_lower_arm;
	private final ModelPart left_wingtip;
	private final ModelPart right_wing;
	private final ModelPart right_wing_upper_arm;
	private final ModelPart right_wing_lower_arm;
	private final ModelPart right_wingtip;

	public wings(ModelPart root) {
		this.root = root.getChild("root");
		this.wings = root.getChild("wings");
		this.left_wing = root.getChild("left_wing");
		this.left_wing_upper_arm = root.getChild("left_wing_upper_arm");
		this.left_wing_lower_arm = root.getChild("left_wing_lower_arm");
		this.left_wingtip = root.getChild("left_wingtip");
		this.right_wing = root.getChild("right_wing");
		this.right_wing_upper_arm = root.getChild("right_wing_upper_arm");
		this.right_wing_lower_arm = root.getChild("right_wing_lower_arm");
		this.right_wingtip = root.getChild("right_wingtip");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 21.5F, 2.0F));

		PartDefinition wings = root.addOrReplaceChild("wings", CubeListBuilder.create(), PartPose.offset(-1.0F, 1.5F, 1.5F));

		PartDefinition left_wing = wings.addOrReplaceChild("left_wing", CubeListBuilder.create(), PartPose.offset(2.0F, 0.0F, 0.0F));

		PartDefinition left_wing_upper_arm = left_wing.addOrReplaceChild("left_wing_upper_arm", CubeListBuilder.create().texOffs(16, 39).addBox(0.0F, -1.0F, 0.0F, 7.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition left_membrane_start_r1 = left_wing_upper_arm.addOrReplaceChild("left_membrane_start_r1", CubeListBuilder.create().texOffs(36, 18).addBox(-4.0F, -1.0F, 1.0F, 9.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 2.0F, -0.25F, 0.0436F, 0.0F, 0.0F));

		PartDefinition left_wing_lower_arm = left_wing_upper_arm.addOrReplaceChild("left_wing_lower_arm", CubeListBuilder.create().texOffs(36, 25).addBox(-1.0F, -1.0F, 0.0F, 10.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(28, 28).addBox(-6.0F, 1.0F, 0.75F, 14.0F, 11.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(7.0F, 0.0F, 0.0F));

		PartDefinition left_wingtip = left_wing_lower_arm.addOrReplaceChild("left_wingtip", CubeListBuilder.create(), PartPose.offset(9.0F, 0.0F, 0.0F));

		PartDefinition left_wingtip_membrane_r1 = left_wingtip.addOrReplaceChild("left_wingtip_membrane_r1", CubeListBuilder.create().texOffs(0, 14).addBox(-3.0F, -1.0F, 1.0F, 18.0F, 14.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -0.25F, 0.0436F, 0.0F, 0.0F));

		PartDefinition finger_r1 = left_wingtip.addOrReplaceChild("finger_r1", CubeListBuilder.create().texOffs(36, 39).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.25F, 0.0F, 0.0436F, 0.0F, 0.0873F));

		PartDefinition finger_r2 = left_wingtip.addOrReplaceChild("finger_r2", CubeListBuilder.create().texOffs(36, 2).addBox(-0.2936F, -0.2929F, -0.0308F, 17.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, 0.0F, 0.0F, 0.0F, -0.0436F, 0.7854F));

		PartDefinition finger_r3 = left_wingtip.addOrReplaceChild("finger_r3", CubeListBuilder.create().texOffs(36, 6).addBox(-1.0F, -1.0F, 0.0F, 15.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0436F));

		PartDefinition right_wing = wings.addOrReplaceChild("right_wing", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition right_wing_upper_arm = right_wing.addOrReplaceChild("right_wing_upper_arm", CubeListBuilder.create().texOffs(0, 39).addBox(-7.0F, -1.0F, 0.0F, 7.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition right_membrane_start_r1 = right_wing_upper_arm.addOrReplaceChild("right_membrane_start_r1", CubeListBuilder.create().texOffs(36, 11).addBox(-5.0F, -1.0F, 1.0F, 9.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 2.0F, -0.25F, 0.0436F, 0.0F, 0.0F));

		PartDefinition right_wing_lower_arm = right_wing_upper_arm.addOrReplaceChild("right_wing_lower_arm", CubeListBuilder.create().texOffs(0, 28).addBox(-8.0F, 1.0F, 0.75F, 14.0F, 11.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(36, 8).addBox(-9.0F, -1.0F, 0.0F, 10.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, 0.0F, 0.0F));

		PartDefinition right_wingtip = right_wing_lower_arm.addOrReplaceChild("right_wingtip", CubeListBuilder.create(), PartPose.offset(-9.0F, 0.0F, 0.0F));

		PartDefinition finger_r4 = right_wingtip.addOrReplaceChild("finger_r4", CubeListBuilder.create().texOffs(36, 4).addBox(-14.0F, -1.0F, 0.0F, 15.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0436F));

		PartDefinition finger_r5 = right_wingtip.addOrReplaceChild("finger_r5", CubeListBuilder.create().texOffs(36, 0).addBox(-16.7064F, -0.2929F, -0.0308F, 17.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, 0.0F, 0.0F, 0.0F, 0.0436F, -0.7854F));

		PartDefinition finger_r6 = right_wingtip.addOrReplaceChild("finger_r6", CubeListBuilder.create().texOffs(32, 39).addBox(0.0F, 0.0F, 0.0F, 1.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.25F, 0.0F, 0.0436F, 0.0F, -0.0873F));

		PartDefinition right_wingtip_membrane_r1 = right_wingtip.addOrReplaceChild("right_wingtip_membrane_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-15.0F, -1.0F, 1.0F, 18.0F, 14.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -0.25F, 0.0436F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(Player entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}