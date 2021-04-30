
package net.mcreator.memealmanac.entity;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.World;
import net.minecraft.world.IWorldReader;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Hand;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ActionResultType;
import net.minecraft.tags.FluidTags;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.network.IPacket;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Item;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.Entity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.MobRenderer;

import net.mcreator.memealmanac.procedures.SpeedRunnerRightClickedOnEntityProcedure;
import net.mcreator.memealmanac.MemealmanacModElements;

import java.util.Map;
import java.util.HashMap;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.matrix.MatrixStack;

@MemealmanacModElements.ModElement.Tag
public class SpeedRunnerEntity extends MemealmanacModElements.ModElement {
	public static EntityType entity = null;
	public SpeedRunnerEntity(MemealmanacModElements instance) {
		super(instance, 1);
		FMLJavaModLoadingContext.get().getModEventBus().register(new ModelRegisterHandler());
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void initElements() {
		entity = (EntityType.Builder.<CustomEntity>create(CustomEntity::new, EntityClassification.AMBIENT).setShouldReceiveVelocityUpdates(true)
				.setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(CustomEntity::new).size(0.9f, 1.4f)).build("speed_runner")
						.setRegistryName("speed_runner");
		elements.entities.add(() -> entity);
		elements.items.add(
				() -> new SpawnEggItem(entity, -1, -16777216, new Item.Properties().group(ItemGroup.MISC)).setRegistryName("speed_runner_spawn_egg"));
	}

	@SubscribeEvent
	public void addFeatureToBiomes(BiomeLoadingEvent event) {
		event.getSpawns().getSpawner(EntityClassification.AMBIENT).add(new MobSpawnInfo.Spawners(entity, 5, 1, 1));
	}

	@Override
	public void init(FMLCommonSetupEvent event) {
		DeferredWorkQueue.runLater(this::setupAttributes);
		EntitySpawnPlacementRegistry.register(entity, EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS,
				Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::canSpawnOn);
		DungeonHooks.addDungeonMob(entity, 180);
	}
	private static class ModelRegisterHandler {
		@SubscribeEvent
		@OnlyIn(Dist.CLIENT)
		public void registerModels(ModelRegistryEvent event) {
			RenderingRegistry.registerEntityRenderingHandler(entity, renderManager -> {
				return new MobRenderer(renderManager, new Modellucky(), 0.7f) {
					@Override
					public ResourceLocation getEntityTexture(Entity entity) {
						return new ResourceLocation("memealmanac:textures/anonimus_speedrunner.png");
					}
				};
			});
		}
	}
	private void setupAttributes() {
		AttributeModifierMap.MutableAttribute ammma = MobEntity.func_233666_p_();
		ammma = ammma.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.5);
		ammma = ammma.createMutableAttribute(Attributes.MAX_HEALTH, 20);
		ammma = ammma.createMutableAttribute(Attributes.ARMOR, 0);
		ammma = ammma.createMutableAttribute(Attributes.ATTACK_DAMAGE, 3);
		ammma = ammma.createMutableAttribute(ForgeMod.SWIM_SPEED.get(), 0.5);
		GlobalEntityTypeAttributes.put(entity, ammma.create());
	}
	public static class CustomEntity extends CreatureEntity {
		public CustomEntity(FMLPlayMessages.SpawnEntity packet, World world) {
			this(entity, world);
		}

		public CustomEntity(EntityType<CustomEntity> type, World world) {
			super(type, world);
			experienceValue = 10;
			setNoAI(false);
			setCustomName(new StringTextComponent("Speedrunner"));
			setCustomNameVisible(true);
			this.moveController = new MovementController(this) {
				@Override
				public void tick() {
					if (CustomEntity.this.areEyesInFluid(FluidTags.WATER))
						CustomEntity.this.setMotion(CustomEntity.this.getMotion().add(0, 0.005, 0));
					if (this.action == MovementController.Action.MOVE_TO && !CustomEntity.this.getNavigator().noPath()) {
						double dx = this.posX - CustomEntity.this.getPosX();
						double dy = this.posY - CustomEntity.this.getPosY();
						double dz = this.posZ - CustomEntity.this.getPosZ();
						dy = dy / (double) MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
						CustomEntity.this.rotationYaw = this.limitAngle(CustomEntity.this.rotationYaw,
								(float) (MathHelper.atan2(dz, dx) * (double) (180 / (float) Math.PI)) - 90, 90);
						CustomEntity.this.renderYawOffset = CustomEntity.this.rotationYaw;
						CustomEntity.this.setAIMoveSpeed(MathHelper.lerp(0.125f, CustomEntity.this.getAIMoveSpeed(),
								(float) (this.speed * CustomEntity.this.getAttributeValue(Attributes.MOVEMENT_SPEED))));
						CustomEntity.this.setMotion(CustomEntity.this.getMotion().add(0, CustomEntity.this.getAIMoveSpeed() * dy * 0.1, 0));
					} else {
						CustomEntity.this.setAIMoveSpeed(0);
					}
				}
			};
			this.navigator = new SwimmerPathNavigator(this, this.world);
		}

		@Override
		public IPacket<?> createSpawnPacket() {
			return NetworkHooks.getEntitySpawningPacket(this);
		}

		@Override
		protected void registerGoals() {
			super.registerGoals();
			this.goalSelector.addGoal(1, new RandomWalkingGoal(this, 1));
			this.targetSelector.addGoal(2, new HurtByTargetGoal(this).setCallsForHelp(this.getClass()));
			this.goalSelector.addGoal(3, new LookRandomlyGoal(this));
			this.goalSelector.addGoal(4, new SwimGoal(this));
		}

		@Override
		public CreatureAttribute getCreatureAttribute() {
			return CreatureAttribute.UNDEFINED;
		}

		@Override
		public net.minecraft.util.SoundEvent getHurtSound(DamageSource ds) {
			return (net.minecraft.util.SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("memealmanac:speedrunner.hurt"));
		}

		@Override
		public net.minecraft.util.SoundEvent getDeathSound() {
			return (net.minecraft.util.SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("memealmanac:speedrunner.death"));
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (source == DamageSource.DRAGON_BREATH)
				return false;
			if (source == DamageSource.WITHER)
				return false;
			if (source.getDamageType().equals("witherSkull"))
				return false;
			return super.attackEntityFrom(source, amount);
		}

		@Override
		public ActionResultType func_230254_b_(PlayerEntity sourceentity, Hand hand) {
			ItemStack itemstack = sourceentity.getHeldItem(hand);
			ActionResultType retval = ActionResultType.func_233537_a_(this.world.isRemote());
			super.func_230254_b_(sourceentity, hand);
			double x = this.getPosX();
			double y = this.getPosY();
			double z = this.getPosZ();
			Entity entity = this;
			{
				Map<String, Object> $_dependencies = new HashMap<>();
				$_dependencies.put("entity", entity);
				$_dependencies.put("world", world);
				SpeedRunnerRightClickedOnEntityProcedure.executeProcedure($_dependencies);
			}
			return retval;
		}

		@Override
		public boolean canBreatheUnderwater() {
			return true;
		}

		@Override
		public boolean isNotColliding(IWorldReader worldreader) {
			return worldreader.checkNoEntityCollision(this, VoxelShapes.create(this.getBoundingBox()));
		}

		@Override
		public boolean isPushedByWater() {
			return false;
		}
	}

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
		public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue,
				float alpha) {
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

		public void setRotationAngles(Entity e, float f, float f1, float f2, float f3, float f4) {
			this.LLeg.rotateAngleX = MathHelper.cos(f * 1.0F) * -1.0F * f1;
			this.Head.rotateAngleY = f3 / (180F / (float) Math.PI);
			this.Head.rotateAngleX = f4 / (180F / (float) Math.PI);
			this.LArm.rotateAngleX = MathHelper.cos(f * 0.6662F) * f1;
			this.RLeg.rotateAngleX = MathHelper.cos(f * 1.0F) * 1.0F * f1;
			this.RArm.rotateAngleX = MathHelper.cos(f * 0.6662F + (float) Math.PI) * f1;
		}
	}
}
