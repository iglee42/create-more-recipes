package fr.iglee42.cmr.mixins.jei;


import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.compat.jei.category.PackingCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedPress;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import fr.iglee42.cmr.utils.JEIDrawableAccessor;
import mezz.jei.api.gui.drawable.IDrawable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = PackingCategory.class, remap = false)
public abstract class PackingCategoryMixin extends BasinCategory implements JEIDrawableAccessor {
    @Shadow @Final private AnimatedPress press;

    public PackingCategoryMixin(Info<BasinRecipe> info, boolean needsHeating) {
        super(info, needsHeating);
    }

    @Override
    public IDrawable cmr$getDrawable() {
        return press;
    }
}