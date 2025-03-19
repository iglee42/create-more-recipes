package fr.iglee42.cmr.jei;

import com.simibubi.create.Create;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.item.ItemHelper;
import fr.iglee42.cmr.recipes.BlockSpoutingRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IIngredientManager;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
public class BlockSpoutCategory extends CreateRecipeCategory<BlockSpoutingRecipe> {

	private final AnimatedBlockSpout spout = new AnimatedBlockSpout();

	public BlockSpoutCategory(Info<BlockSpoutingRecipe> info) {
		super(info);
	}


	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, BlockSpoutingRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 27, 51)
				.setBackground(getRenderedSlot(), -1, -1)
				.addIngredients(recipe.getIngredients()
						.get(0));

		addFluidSlot(builder,27,32,recipe.getRequiredFluid());

		builder
				.addSlot(RecipeIngredientRole.OUTPUT, 132, 51)
				.setBackground(getRenderedSlot(), -1, -1)
				.addItemStack(getResultItem(recipe));
	}


	@Override
	public void draw(BlockSpoutingRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
		AllGuiTextures.JEI_SHADOW.render(graphics, 62, 57);
		AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 126, 29);

		Optional<ItemStack> displayedIngredient = iRecipeSlotsView.getSlotViews()
				.get(0)
				.getDisplayedIngredient(VanillaTypes.ITEM_STACK);
		if (displayedIngredient.isEmpty())
			return;

		Item item = displayedIngredient.get()
				.getItem();
		if (!(item instanceof BlockItem blockItem))
			return;

		BlockState state = blockItem.getBlock()
				.defaultBlockState();

		spout.withState(state).withFluids(recipe.getRequiredFluid()
						.getMatchingFluidStacks())
				.draw(graphics, getBackground().getWidth() / 2 - 13, 22);

	}

}
