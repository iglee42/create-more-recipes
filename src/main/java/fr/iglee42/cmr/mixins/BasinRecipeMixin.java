package fr.iglee42.cmr.mixins;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import fr.iglee42.cmr.CreateMoreRecipes;
import fr.iglee42.cmr.cooler.SnowmanCoolerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = BasinRecipe.class,remap = false)
public class BasinRecipeMixin {

    @Inject(method = "apply(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;Z)Z", at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;<init>()V", shift = At.Shift.AFTER,ordinal = 0),locals = LocalCapture.CAPTURE_FAILSOFT,cancellable = true)
    private static void cmr$checkForCooler(BasinBlockEntity basin, Recipe<?> recipe, boolean test, CallbackInfoReturnable<Boolean> cir, boolean isBasinRecipe, IItemHandler availableItems, IFluidHandler availableFluids){
        Level level = basin.getLevel();
        if (level != null) {
            BlockPos blockPos = basin.getBlockPos().below(1);
            BlockState blockState = level.getBlockState(blockPos);

            if (isBasinRecipe && CreateMoreRecipes.CUSTOM_HEAT_CONDITIONS.containsKey(((BasinRecipe)recipe).getRequiredHeat())) {
                HeatCondition condition = ((BasinRecipe)recipe).getRequiredHeat();
                if (!blockState.hasProperty(SnowmanCoolerBlock.HEAT_LEVEL)) {
                    cir.setReturnValue(false);
                    return;
                }
                SnowmanCoolerBlock.HeatLevel coolHeat = blockState.getValue(SnowmanCoolerBlock.HEAT_LEVEL);
                if (condition.serialize().equals(CreateMoreRecipes.coldId) && !coolHeat.isAtLeast(SnowmanCoolerBlock.HeatLevel.COOLING)){
                    cir.setReturnValue(false);
                    return;
                }
                if (condition.serialize().equals(CreateMoreRecipes.freezeId) && !coolHeat.isAtLeast(SnowmanCoolerBlock.HeatLevel.FREEZING)){
                    cir.setReturnValue(false);
                }
            }
        }
    }
}
