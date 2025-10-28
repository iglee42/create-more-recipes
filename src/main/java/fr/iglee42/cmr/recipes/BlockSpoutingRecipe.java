package fr.iglee42.cmr.recipes;

import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.utility.BlockHelper;
import fr.iglee42.cmr.init.CMRRecipeTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import java.util.List;

public class BlockSpoutingRecipe extends StandardProcessingRecipe<RecipeWrapper> {

	public BlockSpoutingRecipe(ProcessingRecipeParams params) {
		super(CMRRecipeTypes.BLOCK_SPOUTING, params);
	}

	@Override
	public boolean matches(RecipeWrapper inv, Level p_77569_2_) {
		return ingredients.get(0)
			.test(inv.getItem(0));
	}

	public boolean testBlock(BlockState in) {
		return ingredients.get(0)
				.test(new ItemStack(in.getBlock()
						.asItem()));
	}

	public BlockState transformBlock(BlockState in,RandomSource randomSource) {
		ProcessingOutput mainOutput = results.get(0);
		ItemStack output = mainOutput.rollOutput(randomSource);
		if (output.getItem() instanceof BlockItem bi)
			return BlockHelper.copyProperties(in, bi.getBlock()
					.defaultBlockState());
		return Blocks.AIR.defaultBlockState();
	}

	@Override
	protected int getMaxInputCount() {
		return 1;
	}

	@Override
	protected int getMaxOutputCount() {
		return 1;
	}

	@Override
	protected int getMaxFluidInputCount() {
		return 1;
	}

    public SizedFluidIngredient getRequiredFluid() {
        if (fluidIngredients.isEmpty())
            throw new IllegalStateException("Filling Recipe has no fluid ingredient!");
        return fluidIngredients.get(0);
    }

    @Override
    public List<ItemStack> rollResults(RandomSource randomSource) {
        return rollResults(getRollableResultsExceptBlock(),randomSource);
    }

	public List<ProcessingOutput> getRollableResultsExceptBlock() {
		ProcessingOutput mainOutput = results.get(0);
		if (mainOutput.getStack()
				.getItem() instanceof BlockItem)
			return results.subList(1, results.size());
		return results;
	}


}
