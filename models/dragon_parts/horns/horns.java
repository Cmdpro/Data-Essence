// Made with Blockbench 4.11.0
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class horns<T extends Player> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "horns"), "main");
	private final ModelPart root;
	private final ModelPart horns;

	public horns(ModelPart root) {
		this.root = root.getChild("root");
		this.horns = root.getChild("horns");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition horns = root.addOrReplaceChild("horns", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 1.0F));

		PartDefinition cube_r1 = horns.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 17).addBox(0.0F, -1.85F, 0.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -8.5F, 0.75F, -0.4702F, -0.1001F, -0.1942F));

		PartDefinition cube_r2 = horns.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(4, 17).addBox(-1.0F, -1.85F, 0.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -8.5F, 0.75F, -0.4702F, 0.1001F, 0.1942F));

		PartDefinition cube_r3 = horns.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(12, 0).addBox(-0.85F, -2.1F, -1.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.75F, -6.5F, 1.0F, 0.2535F, 0.1095F, -0.0848F));

		PartDefinition cube_r4 = horns.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 12).addBox(-1.15F, -2.1F, -1.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.75F, -6.5F, 1.0F, 0.2535F, -0.1095F, 0.0848F));

		PartDefinition cube_r5 = horns.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(0, 6).addBox(-1.0F, -2.05F, -1.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.25F, -7.0F, -2.5F, -0.2421F, -0.2399F, -0.0868F));

		PartDefinition cube_r6 = horns.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.05F, -1.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.25F, -7.0F, -2.5F, -0.2421F, 0.2399F, 0.0868F));

		PartDefinition cube_r7 = horns.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(10, 12).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -7.5F, -4.0F, -0.215F, -0.0376F, -0.1705F));

		PartDefinition cube_r8 = horns.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(12, 5).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -7.5F, -4.0F, -0.215F, 0.0376F, 0.1705F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(Player entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}