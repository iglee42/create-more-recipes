package fr.iglee42.cmr.mixins;

import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ProcessingRecipeParams.class)
public interface ProcessingRecipeParamsAccessor {
    @Accessor
    HeatCondition getRequiredHeat();

}
