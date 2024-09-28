package fr.iglee42.cmr.mixins.jei;

import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.compat.jei.category.MixingCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedMixer;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import fr.iglee42.cmr.utils.JEIDrawableAccessor;
import mezz.jei.api.gui.drawable.IDrawable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = MixingCategory.class, remap = false)
public abstract class MixingCategoryMixin extends BasinCategory  implements JEIDrawableAccessor {
    @Shadow @Final private AnimatedMixer mixer;

    public MixingCategoryMixin(Info<BasinRecipe> info, boolean needsHeating) {
        super(info, needsHeating);
    }

    public IDrawable cmr$getDrawable() {
        return mixer;
    }
}