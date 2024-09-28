package fr.iglee42.cmr.mixins.jei;

import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.compat.jei.category.MixingCategory;
import com.simibubi.create.compat.jei.category.PackingCategory;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import fr.iglee42.cmr.CreateMoreRecipes;
import fr.iglee42.cmr.cooler.SnowmanCoolerBlock;
import fr.iglee42.cmr.jei.AnimatedSnowmanCooler;
import fr.iglee42.cmr.utils.JEIDrawableAccessor;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {MixingCategory.class, PackingCategory.class, }, remap = false)
public abstract class CommonCategoryMixin extends BasinCategory {
    public CommonCategoryMixin(Info<BasinRecipe> info, boolean needsHeating) {
        super(info, needsHeating);
    }


    @Unique
    private static final AnimatedSnowmanCooler coolerSourceBlock = new AnimatedSnowmanCooler();

    @Inject(
            method = "draw(Lcom/simibubi/create/content/processing/basin/BasinRecipe;Lmezz/jei/api/gui/ingredient/IRecipeSlotsView;Lnet/minecraft/client/gui/GuiGraphics;DD)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/compat/jei/category/animations/AnimatedBlazeBurner;withHeat(Lcom/simibubi/create/content/processing/burner/BlazeBurnerBlock$HeatLevel;)Lcom/simibubi/create/compat/jei/category/animations/AnimatedBlazeBurner;",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void cmr$replaceDrawCallIfCooler(BasinRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY, CallbackInfo ci) {
        HeatCondition requiredHeat = recipe.getRequiredHeat();
        if (CreateMoreRecipes.CUSTOM_HEAT_CONDITIONS.containsKey(requiredHeat)){
            SnowmanCoolerBlock.HeatLevel level = CreateMoreRecipes.CUSTOM_HEAT_CONDITIONS.get(requiredHeat).equals("freeze") ? SnowmanCoolerBlock.HeatLevel.FREEZING : SnowmanCoolerBlock.HeatLevel.COOLING;
            coolerSourceBlock.withHeat(level)
                    .draw(graphics, getBackground().getWidth() / 2 + 3, 55);
            JEIDrawableAccessor drawableAccessor = (JEIDrawableAccessor) this;
            drawableAccessor.cmr$getDrawable().draw(graphics, getBackground().getWidth() / 2 + 3, 34);
            ci.cancel();
        }
    }
}