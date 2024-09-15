package fr.iglee42.cmr;

import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingTypeRegistry;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import com.simibubi.create.infrastructure.config.AllConfigs;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CMRFanProcessingTypes extends AllFanProcessingTypes {

    public static final CustomizableFanType CUSTOM = register("custom",new CustomizableFanType());

    private static final Map<String, FanProcessingType> LEGACY_NAME_MAP;

    private static <T extends FanProcessingType> T register(String id, T type) {
        FanProcessingTypeRegistry.register(CreateMoreRecipes.asResource(id), type);
        return type;
    }


    public static @Nullable FanProcessingType ofLegacyName(String name) {
        return LEGACY_NAME_MAP.get(name);
    }

    public static void register() {
    }

    public static FanProcessingType parseLegacy(String str) {
        FanProcessingType type = ofLegacyName(str);
        return type != null ? type : FanProcessingType.parse(str);
    }

    static {
        Object2ReferenceOpenHashMap<String, FanProcessingType> map = new Object2ReferenceOpenHashMap();
        map.put("CUSTOM",CUSTOM);
        map.trim();
        LEGACY_NAME_MAP = map;
    }


    public static class CustomizableFanType implements FanProcessingType{

        private FanProcessingType.AirFlowParticleAccess access;

        private static final CustomFanRecipe.CustomFanWrapper RECIPE_WRAPPER = new CustomFanRecipe.CustomFanWrapper();
        @Override
        public boolean isValidAt(Level level, BlockPos blockPos) {
            Block block = level.getBlockState(blockPos).getBlock();
            return level.getRecipeManager().getAllRecipesFor(CMRRecipeTypes.CUSTOM_FAN.getType()).stream().anyMatch(r->{
                ProcessingRecipe<?> r1 = (ProcessingRecipe<?>) r;
                CustomFanRecipe r2 = (CustomFanRecipe) r1;
                return r2.getProcessingBlock().contains(block);
            });
        }

        @Override
        public int getPriority() {
            return 2000;
        }

        @Override
        public boolean canProcess(ItemStack itemStack, Level level) {
            return false;
        }

        @Override
        public @Nullable List<ItemStack> process(ItemStack itemStack, Level level) {
            return null;
        }

        @Override
        public void spawnProcessingParticles(Level level, Vec3 pos) {
            if (level.random.nextInt(8) != 0)
                return;
            level.addParticle(ParticleTypes.POOF, pos.x + (level.random.nextFloat() - .5f) * .5f, pos.y + .5f,
                    pos.z + (level.random.nextFloat() - .5f) * .5f, 0, 1 / 8f, 0);
        }

        @Override
        public void morphAirFlow(AirFlowParticleAccess airFlowParticleAccess, RandomSource randomSource) {
            //airFlowParticleAccess.setAlpha(0);
            access = airFlowParticleAccess;
        }

        @Override
        public void affectEntity(Entity entity, Level level) {

        }

        public AirFlowParticleAccess getAccess() {
            return access;
        }

        private boolean canProcess(ItemStack itemStack,Level level,Direction fanDir,BlockPos fanPos,double entityDistance){
            RECIPE_WRAPPER.setItem(0,itemStack);

            if (isValidAt(level,CreateMoreRecipes.getPosOfCatalyst(fanPos,level,fanDir, (int) entityDistance))){
                Block block = level.getBlockState(CreateMoreRecipes.getPosOfCatalyst(fanPos,level,fanDir, (int) entityDistance)).getBlock();
                return level.getRecipeManager().getAllRecipesFor(CMRRecipeTypes.CUSTOM_FAN.getType()).stream().anyMatch(r->{
                    ProcessingRecipe<?> r1 = (ProcessingRecipe<?>) r;
                    CustomFanRecipe r2 = (CustomFanRecipe) r1;
                    return r2.matches(RECIPE_WRAPPER,level,block);
                });
            }
            return false;
        }

        public boolean canCustomProcess(ItemEntity entity, FanProcessingType type, Level level, Direction dir, BlockPos fanPos, double entityDistance) {

            if (entity.getPersistentData()
                    .contains("CreateData")) {
                CompoundTag compound = entity.getPersistentData()
                        .getCompound("CreateData");
                if (compound.contains("Processing")) {
                    CompoundTag processing = compound.getCompound("Processing");

                    if (AllFanProcessingTypes.parseLegacy(processing.getString("Type")) != type)
                        return canProcess(entity.getItem(), entity.level(),dir,fanPos,entityDistance);
                    else if (processing.getInt("Time") >= 0)
                        return true;
                    else if (processing.getInt("Time") == -1)
                        return false;
                }
            }
            return canProcess(entity.getItem(), entity.level(),dir,fanPos,entityDistance);
        }

        public @Nullable List<ItemStack> process(ItemStack itemStack, Level level,Direction dir,BlockPos fanPos, double entityDistance) {
            RECIPE_WRAPPER.setItem(0, itemStack);
            if (isValidAt(level,CreateMoreRecipes.getPosOfCatalyst(fanPos,level,dir, (int) entityDistance))) {
                Block block = level.getBlockState(CreateMoreRecipes.getPosOfCatalyst(fanPos, level, dir, (int) entityDistance)).getBlock();
                Optional<Recipe<Container>> recipe = level.getRecipeManager().getAllRecipesFor(CMRRecipeTypes.CUSTOM_FAN.getType()).stream().filter(r -> {
                    ProcessingRecipe<?> r1 = (ProcessingRecipe<?>) r;
                    CustomFanRecipe r2 = (CustomFanRecipe) r1;
                    return r2.matches(RECIPE_WRAPPER,level,block);
                }).findFirst();
                return recipe.isPresent() ? RecipeApplier.applyRecipeOn(level, itemStack, recipe.get()) : null;
            }
            return null;
        }

        public boolean applyProcessingEntity(ItemEntity entity, FanProcessingType type, Direction dir, BlockPos fanPos, double entityDistance) {
            if (decrementProcessingTime(entity, type) != 0)
                return false;
            List<ItemStack> stacks = process(entity.getItem(), entity.level(),dir,fanPos,entityDistance);
            if (stacks == null)
                return false;
            if (stacks.isEmpty()) {
                entity.discard();
                return false;
            }
            entity.setItem(stacks.remove(0));
            for (ItemStack additional : stacks) {
                ItemEntity entityIn = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), additional);
                entityIn.setDeltaMovement(entity.getDeltaMovement());
                entity.level().addFreshEntity(entityIn);
            }
            return true;
        }


        public TransportedItemStackHandlerBehaviour.TransportedResult applyProcessingHandlers(TransportedItemStack transported, Level world, FanProcessingType type, Direction dir, BlockPos fanPos, int blockDistance) {
            TransportedItemStackHandlerBehaviour.TransportedResult ignore = TransportedItemStackHandlerBehaviour.TransportedResult.doNothing();
            if (transported.processedBy != type) {
                transported.processedBy = type;
                int timeModifierForStackSize = ((transported.stack.getCount() - 1) / 16) + 1;
                int processingTime =
                        (int) (AllConfigs.server().kinetics.fanProcessingTime.get() * timeModifierForStackSize) + 1;
                transported.processingTime = processingTime;
                if (!canProcess(transported.stack, world,dir,fanPos,blockDistance))
                    transported.processingTime = -1;
                return ignore;
            }
            if (transported.processingTime == -1)
                return ignore;
            if (transported.processingTime-- > 0)
                return ignore;

            List<ItemStack> stacks = process(transported.stack, world,dir,fanPos,blockDistance);
            if (stacks == null)
                return ignore;

            List<TransportedItemStack> transportedStacks = new ArrayList<>();
            for (ItemStack additional : stacks) {
                TransportedItemStack newTransported = transported.getSimilar();
                newTransported.stack = additional.copy();
                transportedStacks.add(newTransported);
            }
            return TransportedItemStackHandlerBehaviour.TransportedResult.convertTo(transportedStacks);
        }


        private static int decrementProcessingTime(ItemEntity entity, FanProcessingType type) {
            CompoundTag nbt = entity.getPersistentData();

            if (!nbt.contains("CreateData"))
                nbt.put("CreateData", new CompoundTag());
            CompoundTag createData = nbt.getCompound("CreateData");

            if (!createData.contains("Processing"))
                createData.put("Processing", new CompoundTag());
            CompoundTag processing = createData.getCompound("Processing");

            if (!processing.contains("Type") || AllFanProcessingTypes.parseLegacy(processing.getString("Type")) != type) {
                processing.putString("Type", FanProcessingTypeRegistry.getIdOrThrow(type).toString());
                int timeModifierForStackSize = ((entity.getItem()
                        .getCount() - 1) / 16) + 1;
                int processingTime =
                        (int) (AllConfigs.server().kinetics.fanProcessingTime.get() * timeModifierForStackSize) + 1;
                processing.putInt("Time", processingTime);
            }

            int value = processing.getInt("Time") - 1;
            processing.putInt("Time", value);
            return value;
        }
    }

}
