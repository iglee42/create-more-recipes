package fr.iglee42.cmr.mixins;

import com.simibubi.create.compat.jei.ConversionRecipe;
import com.simibubi.create.compat.jei.category.MysteriousItemConversionCategory;
import fr.iglee42.cmr.CMRRegistries;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(MysteriousItemConversionCategory.class)
public class MysteriousItemConversionCategoryMixin {

    @Shadow @Final public static List<ConversionRecipe> RECIPES;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void inject(CallbackInfo ci){
        RECIPES.add(ConversionRecipe.create(CMRRegistries.EMPTY_SNOWMAN_COOLER.asStack(),CMRRegistries.SNOWMAN_COOLER.asStack()));
    }

}
