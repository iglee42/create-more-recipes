package fr.iglee42.cmr.init;

import java.util.Collections;

import com.simibubi.create.foundation.utility.Lang;

import fr.iglee42.cmr.CreateMoreRecipes;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import static fr.iglee42.cmr.init.CMRTags.NameSpace.MOD;

public class CMRTags {
	public static <T> TagKey<T> optionalTag(Registry<T> registry,
											ResourceLocation id) {
		return TagKey.create(registry.key(), id);
	}

	public static <T> TagKey<T> commonTag(Registry<T> registry, String path) {
		return optionalTag(registry, ResourceLocation.fromNamespaceAndPath("c", path));
	}

	public static TagKey<Block> commonBlockTag(String path) {
		return commonTag(BuiltInRegistries.BLOCK, path);
	}

	public static TagKey<Item> commonItemTag(String path) {
		return commonTag(BuiltInRegistries.ITEM, path);
	}

	public static TagKey<Fluid> commonFluidTag(String path) {
		return commonTag(BuiltInRegistries.FLUID, path);
	}

	public enum NameSpace {

		MOD(CreateMoreRecipes.MODID, false, true),
		COMMON("c"),
		CREATE("create")

		;

		public final String id;
		public final boolean optionalDefault;
		public final boolean alwaysDatagenDefault;

		NameSpace(String id) {
			this(id, true, false);
		}

		NameSpace(String id, boolean optionalDefault, boolean alwaysDatagenDefault) {
			this.id = id;
			this.optionalDefault = optionalDefault;
			this.alwaysDatagenDefault = alwaysDatagenDefault;
		}
	}

	public enum CMRBlockTags {

		;

		public final TagKey<Block> tag;
		public final boolean alwaysDatagen;

		CMRBlockTags() {
			this(MOD);
		}

		CMRBlockTags(NameSpace namespace) {
			this(namespace, namespace.optionalDefault, namespace.alwaysDatagenDefault);
		}

		CMRBlockTags(NameSpace namespace, String path) {
			this(namespace, path, namespace.optionalDefault, namespace.alwaysDatagenDefault);
		}

		CMRBlockTags(NameSpace namespace, boolean optional, boolean alwaysDatagen) {
			this(namespace, null, optional, alwaysDatagen);
		}

		CMRBlockTags(NameSpace namespace, String path, boolean optional, boolean alwaysDatagen) {
			ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace.id, path == null ? Lang.asId(name()) : path);
			if (optional) {
				tag = optionalTag(BuiltInRegistries.BLOCK, id);
			} else {
				tag = BlockTags.create(id);
			}
			this.alwaysDatagen = alwaysDatagen;
		}

		@SuppressWarnings("deprecation")
		public boolean matches(Block block) {
			return block.builtInRegistryHolder()
				.is(tag);
		}

		public boolean matches(ItemStack stack) {
			return stack != null && stack.getItem() instanceof BlockItem blockItem && matches(blockItem.getBlock());
		}

		public boolean matches(BlockState state) {
			return state.is(tag);
		}

		private static void init() {}

	}

	public enum CMRItemTags {

		SNOWMAN_COOLER_FUEL_REGULAR(MOD, "snowman_cooler_fuel/regular"),
		SNOWMAN_COOLER_FUEL_SPECIAL(MOD, "snowman_cooler_fuel/special"),
		;

		public final TagKey<Item> tag;
		public final boolean alwaysDatagen;

		CMRItemTags() {
			this(MOD);
		}

		CMRItemTags(NameSpace namespace) {
			this(namespace, namespace.optionalDefault, namespace.alwaysDatagenDefault);
		}

		CMRItemTags(NameSpace namespace, String path) {
			this(namespace, path, namespace.optionalDefault, namespace.alwaysDatagenDefault);
		}

		CMRItemTags(NameSpace namespace, boolean optional, boolean alwaysDatagen) {
			this(namespace, null, optional, alwaysDatagen);
		}

		CMRItemTags(NameSpace namespace, String path, boolean optional, boolean alwaysDatagen) {
			ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace.id, path == null ? Lang.asId(name()) : path);
			if (optional) {
				tag = optionalTag(BuiltInRegistries.ITEM, id);
			} else {
				tag = ItemTags.create(id);
			}
			this.alwaysDatagen = alwaysDatagen;
		}

		@SuppressWarnings("deprecation")
		public boolean matches(Item item) {
			return item.builtInRegistryHolder()
				.is(tag);
		}

		public boolean matches(ItemStack stack) {
			return stack.is(tag);
		}

		private static void init() {}

	}

	public enum CMRFluidTags {
		;

		public final TagKey<Fluid> tag;
		public final boolean alwaysDatagen;

		CMRFluidTags() {
			this(MOD);
		}

		CMRFluidTags(NameSpace namespace) {
			this(namespace, namespace.optionalDefault, namespace.alwaysDatagenDefault);
		}

		CMRFluidTags(NameSpace namespace, String path) {
			this(namespace, path, namespace.optionalDefault, namespace.alwaysDatagenDefault);
		}

		CMRFluidTags(NameSpace namespace, boolean optional, boolean alwaysDatagen) {
			this(namespace, null, optional, alwaysDatagen);
		}

		CMRFluidTags(NameSpace namespace, String path, boolean optional, boolean alwaysDatagen) {
			ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace.id, path == null ? Lang.asId(name()) : path);
			if (optional) {
				tag = optionalTag(BuiltInRegistries.FLUID, id);
			} else {
				tag = FluidTags.create(id);
			}
			this.alwaysDatagen = alwaysDatagen;
		}

		@SuppressWarnings("deprecation")
		public boolean matches(Fluid fluid) {
			return fluid.is(tag);
		}

		public boolean matches(FluidState state) {
			return state.is(tag);
		}

		private static void init() {}

	}

	public enum CMREntityTags {

		SNOWMAN_COOLER_CAPTURABLE,
		;

		public final TagKey<EntityType<?>> tag;
		public final boolean alwaysDatagen;

		CMREntityTags() {
			this(MOD);
		}

		CMREntityTags(NameSpace namespace) {
			this(namespace, namespace.optionalDefault, namespace.alwaysDatagenDefault);
		}

		CMREntityTags(NameSpace namespace, String path) {
			this(namespace, path, namespace.optionalDefault, namespace.alwaysDatagenDefault);
		}

		CMREntityTags(NameSpace namespace, boolean optional, boolean alwaysDatagen) {
			this(namespace, null, optional, alwaysDatagen);
		}

		CMREntityTags(NameSpace namespace, String path, boolean optional, boolean alwaysDatagen) {
			ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace.id, path == null ? Lang.asId(name()) : path);
			if (optional) {
				tag = optionalTag(BuiltInRegistries.ENTITY_TYPE, id);
			} else {
				tag = TagKey.create(Registries.ENTITY_TYPE, id);
			}
			this.alwaysDatagen = alwaysDatagen;
		}

		public boolean matches(EntityType<?> type) {
			return type.is(tag);
		}

		public boolean matches(Entity entity) {
			return matches(entity.getType());
		}

		private static void init() {}

	}

	public enum CMRRecipeSerializerTags {

		;

		public final TagKey<RecipeSerializer<?>> tag;
		public final boolean alwaysDatagen;

		CMRRecipeSerializerTags() {
			this(MOD);
		}

		CMRRecipeSerializerTags(NameSpace namespace) {
			this(namespace, namespace.optionalDefault, namespace.alwaysDatagenDefault);
		}

		CMRRecipeSerializerTags(NameSpace namespace, String path) {
			this(namespace, path, namespace.optionalDefault, namespace.alwaysDatagenDefault);
		}

		CMRRecipeSerializerTags(NameSpace namespace, boolean optional, boolean alwaysDatagen) {
			this(namespace, null, optional, alwaysDatagen);
		}

		CMRRecipeSerializerTags(NameSpace namespace, String path, boolean optional, boolean alwaysDatagen) {
			ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace.id, path == null ? Lang.asId(name()) : path);
			if (optional) {
				tag = optionalTag(BuiltInRegistries.RECIPE_SERIALIZER, id);
			} else {
				tag = TagKey.create(Registries.RECIPE_SERIALIZER, id);
			}
			this.alwaysDatagen = alwaysDatagen;
		}

		public boolean matches(RecipeSerializer<?> recipeSerializer) {
			ResourceKey<RecipeSerializer<?>> key = BuiltInRegistries.RECIPE_SERIALIZER.getResourceKey(recipeSerializer).orElseThrow();
			return BuiltInRegistries.RECIPE_SERIALIZER.getHolder(key).orElseThrow().is(tag);
		}

		private static void init() {}
	}

	public static void init() {
		CMRBlockTags.init();
		CMRItemTags.init();
		CMRFluidTags.init();
		CMREntityTags.init();
		CMRRecipeSerializerTags.init();
	}
}