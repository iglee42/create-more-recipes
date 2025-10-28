package fr.iglee42.cmr.blockspout;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.api.behaviour.spouting.BlockSpoutingBehaviour;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import fr.iglee42.cmr.init.CMRRecipeTypes;
import fr.iglee42.cmr.init.CMRRegistries;
import fr.iglee42.cmr.recipes.BlockSpoutingRecipe;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;
import java.util.Optional;

public class BlockSpoutBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {

    public static final int TIME = 100;
    public int processingTicks;
    public boolean sendSplash;
    SmartFluidTankBehaviour tank;
    public BlockSpoutingRecipe recipe;


    public BlockSpoutBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        processingTicks = -1;
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                CMRRegistries.BLOCK_SPOUT_BE.get(),
                (be, context) -> {
                    if (context != Direction.DOWN)
                        return be.tank.getCapability();
                    return null;
                }
        );
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        tank = SmartFluidTankBehaviour.single(this, 1000);
        behaviours.add(tank);
    }

    public void tick() {
        super.tick();
        if (level.isClientSide) return;
        if (processingTicks >= 0) {
            processingTicks--;
            notifyUpdate();
        }
        if (processingTicks > 7 && processingTicks < 93){
            sendSplash = true;
            notifyUpdate();
        }
        if (processingTicks == 7 && recipe != null){
            level.destroyBlock(worldPosition.below(2), false);

            BlockState transformedBlock = recipe.transformBlock(level.getBlockState(worldPosition.below(2)),level.random);
            level.setBlock(worldPosition.below(2), transformedBlock, 3);
            recipe.rollResults(level.random)
                    .forEach(stack -> Block.popResource(level, worldPosition.below(2), stack));
            FluidStack stack = getCurrentFluidInTank().copy();
            stack.shrink(recipe.getRequiredFluid().amount());
            tank.getPrimaryHandler().setFluid(stack);
            notifyUpdate();
            recipe = null;

        }

        if (recipe == null){
            Optional<BlockSpoutingRecipe> foundRecipe = level.getRecipeManager()
                    .getAllRecipesFor(CMRRecipeTypes.BLOCK_SPOUTING.getType())
                    .stream()
                    .filter(r -> {
                        BlockSpoutingRecipe bsr =(BlockSpoutingRecipe) ((StandardProcessingRecipe<?>) r.value());
                        return bsr.testBlock(level.getBlockState(worldPosition.below(2))) && bsr.getRequiredFluid().test(getCurrentFluidInTank());
                    })
                    .map(r->(BlockSpoutingRecipe) ((StandardProcessingRecipe<?>) r.value()))
                    .findFirst();

            if (foundRecipe.isEmpty()){
                recipe = null;
                return;
            }
            recipe = foundRecipe.get();
            processingTicks = TIME;
        }
    }

    

    protected void spawnParticles() {
        if (isVirtual())
            return;
        Vec3 vec = VecHelper.getCenterOf(worldPosition);
        vec = vec.subtract(0, 2, 0);
        ParticleOptions particle = new BlockParticleOption(ParticleTypes.BLOCK,getCurrentFluidInTank().getFluid().defaultFluidState().createLegacyBlock());
        for (int i = 0; i < 2; i++) {
            Vec3 m = VecHelper.offsetRandomly(Vec3.ZERO, level.random, 1.5f);
            m = new Vec3(m.x, Math.abs(m.y), m.z);
            level.addAlwaysVisibleParticle(particle, vec.x, vec.y, vec.z, m.x, m.y, m.z);
        }
    }

    private FluidStack getCurrentFluidInTank() {
        return tank.getPrimaryHandler()
                .getFluid();
    }


    @Override
    protected void write(CompoundTag compoundTag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compoundTag, registries, clientPacket);
        compoundTag.putInt("ProcessingTicks", processingTicks);
        if (sendSplash && clientPacket) {
            compoundTag.putBoolean("Splash", true);
            sendSplash = false;
        }
    }

    @Override
    protected void read(CompoundTag compoundTag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compoundTag, registries, clientPacket);
        processingTicks = compoundTag.getInt("ProcessingTicks");
        if (!clientPacket)
            return;
        if (compoundTag.contains("Splash"))
            spawnParticles();
    }
    

    @Override
    protected AABB createRenderBoundingBox() {
        return super.createRenderBoundingBox().expandTowards(0, -2, 0);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return containedFluidTooltip(tooltip, isPlayerSneaking,
                level.getCapability(Capabilities.FluidHandler.BLOCK, worldPosition, null));
    }

}
