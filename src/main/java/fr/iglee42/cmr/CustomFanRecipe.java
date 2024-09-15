package fr.iglee42.cmr;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class CustomFanRecipe extends ProcessingRecipe<CustomFanRecipe.CustomFanWrapper> {
    public CustomFanRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
        super(CMRRecipeTypes.CUSTOM_FAN, params);
    }

    public boolean matches(CustomFanWrapper inv, Level worldIn) {
        return inv.isEmpty() ? false : ((Ingredient)this.ingredients.get(1)).test(inv.getItem(0));
    }

    public boolean matches(CustomFanWrapper inv, Level worldIn,Block block) {
        return inv.isEmpty() ? false : ((Ingredient)this.ingredients.get(1)).test(inv.getItem(0)) && getProcessingBlock().contains(block);
    }

    public List<Block> getProcessingBlock(){
        List<Block> blocks = new ArrayList<>();
        for (ItemStack item : ingredients.get(0).getItems()) {
            if (!(item.getItem() instanceof BlockItem bi)) continue;
            blocks.add(bi.getBlock());
        }
        return blocks;
    }

    protected int getMaxInputCount() {
        return 2;
    }

    protected int getMaxOutputCount() {
        return 12;
    }

    public static class CustomFanWrapper extends RecipeWrapper {
        public CustomFanWrapper() {
            super(new ItemStackHandler(1));
        }
    }
}
