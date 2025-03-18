package fr.iglee42.cmr.mixins;

import com.mojang.datafixers.kinds.App;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import net.minecraft.util.StringRepresentable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ProcessingRecipeSerializer.class)
public class ProcessingRecipeSerializerMixin {

    @ModifyArg(method = "lambda$codec$7",at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder$Instance;group(Lcom/mojang/datafixers/kinds/App;Lcom/mojang/datafixers/kinds/App;Lcom/mojang/datafixers/kinds/App;Lcom/mojang/datafixers/kinds/App;)Lcom/mojang/datafixers/Products$P4;"),index=3)
    private static <T extends ProcessingRecipe<?>> App cmr$modifyHeatCodec(App par1){
        return StringRepresentable.fromEnum(HeatCondition::values).optionalFieldOf("heat_requirement",HeatCondition.NONE).forGetter(T::getRequiredHeat);
    }
}
