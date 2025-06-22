package fr.iglee42.cmr.mixins;

import com.mojang.datafixers.kinds.App;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import net.minecraft.util.StringRepresentable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ProcessingRecipeParams.class)
public abstract class ProcessingRecipeParamsMixin {


    @ModifyArg(method = "lambda$codec$3",at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder$Instance;group(Lcom/mojang/datafixers/kinds/App;Lcom/mojang/datafixers/kinds/App;Lcom/mojang/datafixers/kinds/App;Lcom/mojang/datafixers/kinds/App;)Lcom/mojang/datafixers/Products$P4;"),index=3)
    private static <T extends ProcessingRecipeParams> App cmr$modifyHeatCodec(App par1){
        return StringRepresentable.fromEnum(HeatCondition::values).optionalFieldOf("heat_requirement", HeatCondition.NONE)
                .forGetter(r->((ProcessingRecipeParamsAccessor)r).getRequiredHeat());
    }
}
