package fr.iglee42.cmr.cooler;

import com.simibubi.create.AllSoundEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import fr.iglee42.cmr.cooler.SnowmanCoolerBlockEntity.FuelType;

@Mod.EventBusSubscriber
public class SnowmanCoolerHandler {

	@SubscribeEvent
	public static void onThrowableImpact(ProjectileImpactEvent event) {
		thrownEggsGetEatenByBurner(event);
	}

	public static void thrownEggsGetEatenByBurner(ProjectileImpactEvent event) {
		Projectile projectile = event.getProjectile();
		if (!(projectile instanceof Snowball))
			return;

		if (event.getRayTraceResult()
			.getType() != HitResult.Type.BLOCK)
			return;

		BlockEntity blockEntity = projectile.level()
			.getBlockEntity(BlockPos.containing(event.getRayTraceResult()
				.getLocation()));
		if (!(blockEntity instanceof SnowmanCoolerBlockEntity)) {
			return;
		}

		event.setCanceled(true);
		projectile.setDeltaMovement(Vec3.ZERO);
		projectile.discard();

		Level world = projectile.level();
		if (world.isClientSide)
			return;

		SnowmanCoolerBlockEntity heater = (SnowmanCoolerBlockEntity) blockEntity;
		if (!heater.isCreative()) {
			if (heater.activeFuel != FuelType.SPECIAL) {
				heater.activeFuel = FuelType.NORMAL;
				heater.remainingBurnTime =
					Mth.clamp(heater.remainingBurnTime + 80, 0, SnowmanCoolerBlockEntity.MAX_HEAT_CAPACITY);
				heater.updateBlockState();
				heater.notifyUpdate();
			}
		}

		AllSoundEvents.BLAZE_MUNCH.playOnServer(world, heater.getBlockPos());
	}



}
