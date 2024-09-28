package fr.iglee42.cmr.cooler;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags.AllEntityTags;
import com.simibubi.create.foundation.utility.RegisteredObjects;
import com.simibubi.create.foundation.utility.VecHelper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;

import fr.iglee42.cmr.CMRRegistries;
import fr.iglee42.cmr.CMRTags;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.phys.Vec3;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class EmptyCoolerBlockItem extends BlockItem {


    public EmptyCoolerBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
    }


    public InteractionResult useOn(UseOnContext context) {

            Level world = context.getLevel();
            BlockPos pos = context.getClickedPos();
            BlockEntity be = world.getBlockEntity(pos);
            Player player = context.getPlayer();
            if (!(be instanceof SpawnerBlockEntity)) {
                return super.useOn(context);
            } else {
                BaseSpawner spawner = ((SpawnerBlockEntity) be).getSpawner();
                List<SpawnData> possibleSpawns = spawner.spawnPotentials.unwrap().stream().map(WeightedEntry.Wrapper::getData).toList();
                if (((List) possibleSpawns).isEmpty()) {
                    possibleSpawns = new ArrayList();
                    ((List) possibleSpawns).add(spawner.nextSpawnData);
                }

                Iterator var8 = ((List) possibleSpawns).iterator();

                Optional optionalEntity;
                do {
                    if (!var8.hasNext()) {
                        return super.useOn(context);
                    }

                    SpawnData e = (SpawnData) var8.next();
                    optionalEntity = EntityType.by(e.entityToSpawn());
                } while (optionalEntity.isEmpty() || !CMRTags.CMREntityTags.SNOWMAN_COOLER_CAPTURABLE.matches((EntityType) optionalEntity.get()));

                this.spawnCaptureEffects(world, VecHelper.getCenterOf(pos));
                if (!world.isClientSide && player != null) {
                    this.giveBurnerItemTo(player, context.getItemInHand(), context.getHand());
                    return InteractionResult.SUCCESS;
                } else {
                    return InteractionResult.SUCCESS;
                }
            }
    }

    public InteractionResult interactLivingEntity(ItemStack heldItem, Player player, LivingEntity entity, InteractionHand hand) {
        if (!CMRTags.CMREntityTags.SNOWMAN_COOLER_CAPTURABLE.matches(entity)) {
            return InteractionResult.PASS;
        } else {
            Level world = player.level();
            this.spawnCaptureEffects(world, entity.position());
            if (world.isClientSide) {
                return InteractionResult.FAIL;
            } else {
                this.giveBurnerItemTo(player, heldItem, hand);
                entity.discard();
                return InteractionResult.FAIL;
            }
        }
    }

    protected void giveBurnerItemTo(Player player, ItemStack heldItem, InteractionHand hand) {
        ItemStack filled = CMRRegistries.SNOWMAN_COOLER.asStack();
        if (!player.isCreative()) {
            heldItem.shrink(1);
        }

        if (heldItem.isEmpty()) {
            player.setItemInHand(hand, filled);
        } else {
            player.getInventory().placeItemBackInInventory(filled);
        }
    }

    public static void spawnCaptureEffects(Level world, Vec3 vec) {
        if (!world.isClientSide) {
            BlockPos soundPos = BlockPos.containing(vec);
            world.playSound((Player) null, soundPos, SoundEvents.SNOW_GOLEM_HURT, SoundSource.HOSTILE, 0.25F, 0.75F);
            world.playSound((Player) null, soundPos, SoundEvents.BUCKET_FILL_POWDER_SNOW, SoundSource.HOSTILE, 0.5F, 0.75F);
        } else {
            for (int i = 0; i < 40; ++i) {
                Vec3 motion = VecHelper.offsetRandomly(Vec3.ZERO, world.random, 0.125F);
                world.addParticle(ParticleTypes.ITEM_SNOWBALL, vec.x, vec.y, vec.z, motion.x, motion.y, motion.z);
                world.addParticle(ParticleTypes.SNOWFLAKE, vec.x, vec.y, vec.z, motion.x, motion.y, motion.z);
                Vec3 circle = motion.multiply(1.0, 0.0, 1.0).normalize().scale(0.5);
                world.addParticle(ParticleTypes.SMOKE, circle.x, vec.y, circle.z, 0.0, -0.125, 0.0);
            }

        }
    }

}