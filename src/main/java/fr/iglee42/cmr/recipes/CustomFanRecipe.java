package fr.iglee42.cmr.recipes;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import javax.annotation.ParametersAreNonnullByDefault;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import fr.iglee42.cmr.init.CMRRecipeTypes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class CustomFanRecipe extends StandardProcessingRecipe<CustomFanRecipe.CustomFanWrapper> {
    public CustomFanRecipe(ProcessingRecipeParams params) {
        super(CMRRecipeTypes.CUSTOM_FAN, params);
    }

    @Override
    public boolean matches(CustomFanWrapper inv, Level worldIn) {
        if (inv.isEmpty())
            return false;
        return ingredients.get(0)
                .test(inv.getItem(0));
    }

    public boolean matches(CustomFanWrapper inv, Level worldIn,Block block) {
        return inv.isEmpty() ? false : ((Ingredient)this.ingredients.get(0)).test(inv.getItem(0)) && getProcessingBlock().contains(block);
    }

    public List<Block> getProcessingBlock(){
        List<Block> blocks = new ArrayList<>();
        if (ingredients.size() > 1) {
            for (ItemStack item : ingredients.get(1).getItems()) {
                if (!(item.getItem() instanceof BlockItem bi)) continue;
                blocks.add(bi.getBlock());
            }
        }
        for (FluidIngredient fluid : fluidIngredients){
            blocks.addAll(fluid.getMatchingFluidStacks().stream().map(f->f.getFluid().defaultFluidState().createLegacyBlock().getBlock()).toList());
        }
        return blocks;
    }

    protected int getMaxInputCount() {
        return 2;
    }

    protected int getMaxOutputCount() {
        return 12;
    }

    @Override
    protected int getMaxFluidInputCount() {
        return 1;
    }

    public static class CustomFanWrapper extends RecipeWrapper {
        public CustomFanWrapper(ItemStackHandler handler) {
            super(handler);
        }
    }
}
