package fr.iglee42.cmr.mixins.jei;

import com.simibubi.create.AllItems;
import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import fr.iglee42.cmr.CMRRegistries;
import fr.iglee42.cmr.CreateMoreRecipes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(value = BasinCategory.class,remap = false)
public class BasinCategoryMixin {

    @Inject(method = "setRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lcom/simibubi/create/content/processing/basin/BasinRecipe;Lmezz/jei/api/recipe/IFocusGroup;)V",at = @At("TAIL"),locals = LocalCapture.CAPTURE_FAILSOFT)
    private void cmr$setRecipe(IRecipeLayoutBuilder builder, BasinRecipe recipe, IFocusGroup focuses, CallbackInfo ci, List condensedIngredients, int size, int xOffset, int i, HeatCondition requiredHeat){
        if (CreateMoreRecipes.CUSTOM_HEAT_CONDITIONS.containsKey(requiredHeat)){
            builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 134, 81)
                    .addItemStack(CMRRegistries.SNOWMAN_COOLER.asStack());
        }
        if (requiredHeat.serialize().equals(CreateMoreRecipes.freezeId)){
            builder.addSlot(RecipeIngredientRole.CATALYST, 153, 81).addItemStack(CMRRegistries.FROZEN_CAKE.asStack());
        }
    }
}
