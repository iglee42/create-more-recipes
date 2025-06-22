package fr.iglee42.cmr.jei;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.Create;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.ItemIcon;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.ProcessingViaFanCategory;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CRecipes;
import fr.iglee42.cmr.CreateMoreRecipes;
import fr.iglee42.cmr.init.CMRRecipeTypes;
import fr.iglee42.cmr.init.CMRRegistries;
import fr.iglee42.cmr.recipes.BlockSpoutingRecipe;
import fr.iglee42.cmr.recipes.CustomFanRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import net.createmod.catnip.config.ConfigBase;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static mezz.jei.api.recipe.RecipeType.createRecipeHolderType;

@JeiPlugin
@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
public class CMRJEI implements IModPlugin {

	private static final ResourceLocation ID = CreateMoreRecipes.asResource("jei_plugin");

	private final List<CreateRecipeCategory<?>> allCategories = new ArrayList<>();
	private IIngredientManager ingredientManager;

	private void loadCategories() {
		allCategories.clear();

		CreateRecipeCategory<?>

		customFan = builder(CustomFanRecipe.class)
				.addTypedRecipes(CMRRecipeTypes.CUSTOM_FAN)
				.catalystStack(ProcessingViaFanCategory.getFan("fan_custom"))
				.doubleItemIcon(AllItems.PROPELLER.get(), Items.DIAMOND)
				.emptyBackground(178, 72)
				.build("fan_custom", FanCustomCategory::new),
		blockSpout = builder(BlockSpoutingRecipe.class)
				.addTypedRecipes(CMRRecipeTypes.BLOCK_SPOUTING)
				.catalystStack(CMRRegistries.BLOCK_SPOUT::asStack)
				.itemIcon(CMRRegistries.BLOCK_SPOUT.asItem())
				.emptyBackground(178, 72)
				.build("block_spout", BlockSpoutCategory::new);

	}

	private <T extends Recipe<?>> CategoryBuilder<T> builder(Class<? extends T> recipeClass) {
		return new CategoryBuilder<>(recipeClass);
	}

	@Override
	@Nonnull
	public ResourceLocation getPluginUid() {
		return ID;
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		loadCategories();
		registration.addRecipeCategories(allCategories.toArray(IRecipeCategory[]::new));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		ingredientManager = registration.getIngredientManager();

		allCategories.forEach(c -> c.registerRecipes(registration));

	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		allCategories.forEach(c -> c.registerCatalysts(registration));
		registration.addRecipeCatalyst(CMRRegistries.SMITHING_PRESS, RecipeTypes.SMITHING);
	}



	private class CategoryBuilder<T extends Recipe<?>> extends CreateRecipeCategory.Builder<T>{

		public CategoryBuilder(Class<? extends T> recipeClass) {
			super(recipeClass);
		}

		@Override
		public CreateRecipeCategory<T> build(String name, CreateRecipeCategory.Factory<T> factory) {
			return build(CreateMoreRecipes.asResource(name), factory);
		}

		@Override
		public CreateRecipeCategory<T> build(ResourceLocation id, CreateRecipeCategory.Factory<T> factory) {
			CreateRecipeCategory<T> category =  super.build(id, factory);
			allCategories.add(category);
			return category;
		}
	}


	public static void consumeAllRecipes(Consumer<? super RecipeHolder<?>> consumer) {
		Minecraft.getInstance()
				.getConnection()
				.getRecipeManager()
				.getRecipes()
				.forEach(consumer);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static <T extends Recipe<?>> void consumeTypedRecipes(Consumer<RecipeHolder<?>> consumer, RecipeType<?> type) {
		List<? extends RecipeHolder<?>> map = Minecraft.getInstance()
				.getConnection()
				.getRecipeManager().getAllRecipesFor((RecipeType) type);
		if (!map.isEmpty())
			map.forEach(consumer);
	}

	public static List<RecipeHolder<?>> getTypedRecipes(RecipeType<?> type) {
		List<RecipeHolder<?>> recipes = new ArrayList<>();
		consumeTypedRecipes(recipes::add, type);
		return recipes;
	}

	public static List<RecipeHolder<?>> getTypedRecipesExcluding(RecipeType<?> type, Predicate<RecipeHolder<?>> exclusionPred) {
		List<RecipeHolder<?>> recipes = getTypedRecipes(type);
		recipes.removeIf(exclusionPred);
		return recipes;
	}

	public static boolean doInputsMatch(Recipe<?> recipe1, Recipe<?> recipe2) {
		if (recipe1.getIngredients()
				.isEmpty()
				|| recipe2.getIngredients()
				.isEmpty()) {
			return false;
		}
		ItemStack[] matchingStacks = recipe1.getIngredients()
				.getFirst()
				.getItems();
		if (matchingStacks.length == 0) {
			return false;
		}
		return recipe2.getIngredients()
				.getFirst()
				.test(matchingStacks[0]);
	}

	public static boolean doOutputsMatch(Recipe<?> recipe1, Recipe<?> recipe2) {
		RegistryAccess registryAccess = Minecraft.getInstance().level.registryAccess();
		return ItemHelper.sameItem(recipe1.getResultItem(registryAccess), recipe2.getResultItem(registryAccess));
	}

}
