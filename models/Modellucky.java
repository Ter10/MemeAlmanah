// Made with Blockbench 3.8.3
// Exported for Minecraft version 1.15 - 1.16
// Paste this class into your mod and generate all required imports

public static class Modellucky extends EntityModel<Entity> {
	private final ModelRenderer Head;
	private final ModelRenderer Body;
	private final ModelRenderer LArm;
	private final ModelRenderer RArm;
	private final ModelRenderer LLeg;
	private final ModelRenderer RLeg;

	public Modellucky() {
		textureWidth = 64;
		textureHeight = 64;

		Head = new ModelRenderer(this);
		Head.setRotationPoint(0.0F, 8.0F, 0.0F);
		Head.setTextureOffset(0, 0).addBox(-4.0F, -8.0F, -3.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);
		Head.setTextureOffset(32, 0).addBox(-4.0F, -8.1F, -3.1F, 8.0F, 8.0F, 8.0F, 0.0F, false);

		Body = new ModelRenderer(this);
		Body.setRotationPoint(0.0F, 13.0F, 0.0F);
		Body.setTextureOffset(10, 14).addBox(-8.0F, -5.0F, -2.0F, 16.0F, 9.0F, 6.0F, 0.0F, false);
		Body.setTextureOffset(0, 0).addBox(-7.0F, -4.0F, -2.2F, 4.0F, 7.0F, 1.0F, 0.0F, false);

		LArm = new ModelRenderer(this);
		LArm.setRotationPoint(8.0F, 8.0F, 0.0F);
		LArm.setTextureOffset(22, 49).addBox(0.0F, 0.0F, -2.0F, 6.0F, 9.0F, 6.0F, 0.0F, true);

		RArm = new ModelRenderer(this);
		RArm.setRotationPoint(-8.0F, 8.0F, 0.0F);
		RArm.setTextureOffset(22, 49).addBox(-6.0F, 0.0F, -2.0F, 6.0F, 9.0F, 6.0F, 0.0F, false);

		LLeg = new ModelRenderer(this);
		LLeg.setRotationPoint(6.0F, 18.0F, 2.0F);
		LLeg.setTextureOffset(18, 51).addBox(-6.0F, -1.0F, -4.0F, 8.0F, 7.0F, 6.0F, 0.0F, true);

		RLeg = new ModelRenderer(this);
		RLeg.setRotationPoint(-5.0F, 18.0F, 2.0F);
		RLeg.setTextureOffset(18, 51).addBox(-3.0F, -1.0F, -4.0F, 8.0F, 7.0F, 6.0F, 0.0F, false);
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red,
			float green, float blue, float alpha) {
		Head.render(matrixStack, buffer, packedLight, packedOverlay);
		Body.render(matrixStack, buffer, packedLight, packedOverlay);
		LArm.render(matrixStack, buffer, packedLight, packedOverlay);
		RArm.render(matrixStack, buffer, packedLight, packedOverlay);
		LLeg.render(matrixStack, buffer, packedLight, packedOverlay);
		RLeg.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
		this.LLeg.rotateAngleX = MathHelper.cos(f * 1.0F) * -1.0F * f1;
		this.Head.rotateAngleY = f3 / (180F / (float) Math.PI);
		this.Head.rotateAngleX = f4 / (180F / (float) Math.PI);
		this.LArm.rotateAngleX = MathHelper.cos(f * 0.6662F) * f1;
		this.RLeg.rotateAngleX = MathHelper.cos(f * 1.0F) * 1.0F * f1;
		this.RArm.rotateAngleX = MathHelper.cos(f * 0.6662F + (float) Math.PI) * f1;
	}
}