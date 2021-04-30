package net.mcreator.memealmanac.procedures;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.World;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity;

import net.mcreator.memealmanac.MemealmanacModElements;
import net.mcreator.memealmanac.MemealmanacMod;

import java.util.Map;

@MemealmanacModElements.ModElement.Tag
public class SpeedRunnerRightClickedOnEntityProcedure extends MemealmanacModElements.ModElement {
	public SpeedRunnerRightClickedOnEntityProcedure(MemealmanacModElements instance) {
		super(instance, 2);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			if (!dependencies.containsKey("entity"))
				MemealmanacMod.LOGGER.warn("Failed to load dependency entity for procedure SpeedRunnerRightClickedOnEntity!");
			return;
		}
		if (dependencies.get("sourceentity") == null) {
			if (!dependencies.containsKey("sourceentity"))
				MemealmanacMod.LOGGER.warn("Failed to load dependency sourceentity for procedure SpeedRunnerRightClickedOnEntity!");
			return;
		}
		if (dependencies.get("world") == null) {
			if (!dependencies.containsKey("world"))
				MemealmanacMod.LOGGER.warn("Failed to load dependency world for procedure SpeedRunnerRightClickedOnEntity!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		Entity sourceentity = (Entity) dependencies.get("sourceentity");
		IWorld world = (IWorld) dependencies.get("world");
		if (((((sourceentity instanceof LivingEntity) ? ((LivingEntity) sourceentity).getHeldItemMainhand() : ItemStack.EMPTY)
				.getItem() == new ItemStack(Items.DIAMOND, (int) (1)).getItem())
				|| (((sourceentity instanceof LivingEntity) ? ((LivingEntity) sourceentity).getHeldItemOffhand() : ItemStack.EMPTY)
						.getItem() == new ItemStack(Items.DIAMOND, (int) (1)).getItem()))) {
			if (world instanceof World && !world.isRemote()) {
				((World) world).playSound(null, new BlockPos((int) (entity.getPosX()), (int) (entity.getPosY()), (int) (entity.getPosZ())),
						(net.minecraft.util.SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("memealmanac:speedrunner.lucky")),
						SoundCategory.NEUTRAL, (float) 0.5, (float) 1);
			} else {
				((World) world).playSound((entity.getPosX()), (entity.getPosY()), (entity.getPosZ()),
						(net.minecraft.util.SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("memealmanac:speedrunner.lucky")),
						SoundCategory.NEUTRAL, (float) 0.5, (float) 1, false);
			}
		}
	}
}
