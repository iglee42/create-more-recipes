package fr.iglee42.cmr.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.ProcessingViaFanCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.element.GuiGameElement;
import fr.iglee42.cmr.CustomFanRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class FanCustomCategory extends ProcessingViaFanCategory.MultiOutput<CustomFanRecipe> {
    public FanCustomCategory(CreateRecipeCategory.Info<CustomFanRecipe> info) {
        super(info);
    }

    protected AllGuiTextures getBlockShadow() {
        return AllGuiTextures.JEI_LIGHT;
    }

    protected void renderAttachedBlock(GuiGraphics graphics) {
        //GuiGameElement.of(Blocks.SOUL_FIRE.defaultBlockState()).scale(24.0).atLocal(0.0, 0.0, 2.0).lighting(AnimatedKinetics.DEFAULT_LIGHTING).render(graphics);
    }

    public void setRecipe(IRecipeLayoutBuilder builder, CustomFanRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 21, 48).setBackground(getRenderedSlot(), -1, -1).addIngredients(recipe.getIngredients().get(1));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 141, 48).setBackground(getRenderedSlot(), -1, -1).addItemStack(getResultItem(recipe));
        builder.addInvisibleIngredients(RecipeIngredientRole.CATALYST).addIngredients(VanillaTypes.ITEM_STACK,recipe.getProcessingBlock().stream().map(ItemStack::new).toList());
    }

    @Override
    public void draw(CustomFanRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        this.renderWidgets(graphics, recipe, mouseX, mouseY);
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        this.translateFan(matrixStack);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-12.5F));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5F));
        AnimatedKinetics.defaultBlockElement(AllPartialModels.ENCASED_FAN_INNER).rotateBlock(180.0, 0.0, (double)(AnimatedKinetics.getCurrentAngle() * 16.0F)).scale(24.0).render(graphics);
        AnimatedKinetics.defaultBlockElement(AllBlocks.ENCASED_FAN.getDefaultState()).rotateBlock(0.0, 180.0, 0.0).atLocal(0.0, 0.0, 0.0).scale(24.0).render(graphics);
        GuiGameElement.of(recipe.getProcessingBlock().get(0).defaultBlockState()).scale(24.0).atLocal(0.0, 0.0, 2.0).lighting(AnimatedKinetics.DEFAULT_LIGHTING).render(graphics);
        matrixStack.popPose();
    }
}
