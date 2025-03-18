package fr.iglee42.cmr.init;


import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.logistics.box.PackageStyles;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.TagDependentIngredientItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import fr.iglee42.cmr.CreateMoreRecipes;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CMRCreativeModeTabs {

	private static final DeferredRegister<CreativeModeTab> TAB_REGISTER =
		DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateMoreRecipes.MODID);

	public static final RegistryObject<CreativeModeTab> MAIN_TAB = TAB_REGISTER.register("tab",
		() -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.cmr"))
			.withTabsBefore(AllCreativeModeTabs.BASE_CREATIVE_TAB.getKey())
			.icon(CMRRegistries.SNOWMAN_COOLER::asStack)
				.displayItems(new RegistrateDisplayItemsGenerator(true,CMRCreativeModeTabs.MAIN_TAB))
			.build());

	
	public static void register(IEventBus modEventBus) {
		TAB_REGISTER.register(modEventBus);
	}

	public static CreativeModeTab getBaseTab() {
		return MAIN_TAB.get();
	}

	private static class RegistrateDisplayItemsGenerator implements CreativeModeTab.DisplayItemsGenerator {
		private static final Predicate<Item> IS_ITEM_3D_PREDICATE;

		static {
			MutableObject<Predicate<Item>> isItem3d = new MutableObject<>(item -> false);
			if (CatnipServices.PLATFORM.getEnv().isClient())
				isItem3d.setValue(makeClient3dItemPredicate());
			IS_ITEM_3D_PREDICATE = isItem3d.getValue();
		}

		@OnlyIn(Dist.CLIENT)
		private static Predicate<Item> makeClient3dItemPredicate() {
			return item -> {
				ItemRenderer itemRenderer = Minecraft.getInstance()
						.getItemRenderer();
				BakedModel model = itemRenderer.getModel(new ItemStack(item), null, null, 0);
				return model.isGui3d();
			};
		}

		private final boolean addItems;
		private final DeferredHolder<CreativeModeTab, CreativeModeTab> tabFilter;

		public RegistrateDisplayItemsGenerator(boolean addItems, DeferredHolder<CreativeModeTab, CreativeModeTab> tabFilter) {
			this.addItems = addItems;
			this.tabFilter = tabFilter;
		}

		private static Predicate<Item> makeExclusionPredicate() {
			Set<Item> exclusions = new ReferenceOpenHashSet<>();

			List<ItemProviderEntry<?, ?>> simpleExclusions = List.of(
			);

			List<ItemEntry<TagDependentIngredientItem>> tagDependentExclusions = List.of(
			);

			for (ItemProviderEntry<?, ?> entry : simpleExclusions) {
				exclusions.add(entry.asItem());
			}

			for (ItemEntry<TagDependentIngredientItem> entry : tagDependentExclusions) {
				TagDependentIngredientItem item = entry.get();
				if (item.shouldHide()) {
					exclusions.add(entry.asItem());
				}
			}

			return exclusions::contains;
		}

		private static List<ItemOrdering> makeOrderings() {
			List<ItemOrdering> orderings = new ReferenceArrayList<>();

			Map<ItemProviderEntry<?, ?>, ItemProviderEntry<?, ?>> simpleBeforeOrderings = Map.of(
			);

			Map<ItemProviderEntry<?, ?>, ItemProviderEntry<?, ?>> simpleAfterOrderings = Map.of(
			);

			simpleBeforeOrderings.forEach((entry, otherEntry) -> {
				orderings.add(ItemOrdering.before(entry.asItem(), otherEntry.asItem()));
			});

			simpleAfterOrderings.forEach((entry, otherEntry) -> {
				orderings.add(ItemOrdering.after(entry.asItem(), otherEntry.asItem()));
			});

			PackageStyles.STANDARD_BOXES.forEach(item -> {
				orderings.add(ItemOrdering.after(item, AllBlocks.PACKAGER.asItem()));
			});

			return orderings;
		}

		private static Function<Item, ItemStack> makeStackFunc() {
			Map<Item, Function<Item, ItemStack>> factories = new Reference2ReferenceOpenHashMap<>();

			Map<ItemProviderEntry<?>, Function<Item, ItemStack>> simpleFactories = Map.of(
			);

			simpleFactories.forEach((entry, factory) -> {
				factories.put(entry.asItem(), factory);
			});

			return item -> {
				Function<Item, ItemStack> factory = factories.get(item);
				if (factory != null) {
					return factory.apply(item);
				}
				return new ItemStack(item);
			};
		}

		private static Function<Item, CreativeModeTab.TabVisibility> makeVisibilityFunc() {
			Map<Item, CreativeModeTab.TabVisibility> visibilities = new Reference2ObjectOpenHashMap<>();

			Map<ItemProviderEntry<?>, CreativeModeTab.TabVisibility> simpleVisibilities = Map.of(
			);

			simpleVisibilities.forEach((entry, factory) -> {
				visibilities.put(entry.asItem(), factory);
			});

			return item -> {
				CreativeModeTab.TabVisibility visibility = visibilities.get(item);
				if (visibility != null) {
					return visibility;
				}
				return CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS;
			};
		}

		@Override
		public void accept(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
			Predicate<Item> exclusionPredicate = makeExclusionPredicate();
			List<ItemOrdering> orderings = makeOrderings();
			Function<Item, ItemStack> stackFunc = makeStackFunc();
			Function<Item, CreativeModeTab.TabVisibility> visibilityFunc = makeVisibilityFunc();

			List<Item> items = new LinkedList<>();
			if (addItems) {
				items.addAll(collectItems(exclusionPredicate.or(IS_ITEM_3D_PREDICATE.negate())));
			}
			items.addAll(collectBlocks(exclusionPredicate));
			if (addItems) {
				items.addAll(collectItems(exclusionPredicate.or(IS_ITEM_3D_PREDICATE)));
			}

			applyOrderings(items, orderings);
			outputAll(output, items, stackFunc, visibilityFunc);
		}

		private List<Item> collectBlocks(Predicate<Item> exclusionPredicate) {
			List<Item> items = new ReferenceArrayList<>();
			for (RegistryEntry<Block, Block> entry : CreateMoreRecipes.REGISTRATE.getAll(Registries.BLOCK)) {
				if (!CreateRegistrate.isInCreativeTab(entry, tabFilter))
					continue;
				Item item = entry.get()
						.asItem();
				if (item == Items.AIR)
					continue;
				if (!exclusionPredicate.test(item))
					items.add(item);
			}
			items = new ReferenceArrayList<>(new ReferenceLinkedOpenHashSet<>(items));
			return items;
		}

		private List<Item> collectItems(Predicate<Item> exclusionPredicate) {
			List<Item> items = new ReferenceArrayList<>();
			for (RegistryEntry<Item, Item> entry : CreateMoreRecipes.REGISTRATE.getAll(Registries.ITEM)) {
				if (!CreateRegistrate.isInCreativeTab(entry, tabFilter))
					continue;
				Item item = entry.get();
				if (item instanceof BlockItem)
					continue;
				if (!exclusionPredicate.test(item))
					items.add(item);
			}
			return items;
		}

		private static void applyOrderings(List<Item> items, List<ItemOrdering> orderings) {
			for (ItemOrdering ordering : orderings) {
				int anchorIndex = items.indexOf(ordering.anchor());
				if (anchorIndex != -1) {
					Item item = ordering.item();
					int itemIndex = items.indexOf(item);
					if (itemIndex != -1) {
						items.remove(itemIndex);
						if (itemIndex < anchorIndex) {
							anchorIndex--;
						}
					}
					if (ordering.type() == ItemOrdering.Type.AFTER) {
						items.add(anchorIndex + 1, item);
					} else {
						items.add(anchorIndex, item);
					}
				}
			}
		}

		private static void outputAll(CreativeModeTab.Output output, List<Item> items, Function<Item, ItemStack> stackFunc, Function<Item, CreativeModeTab.TabVisibility> visibilityFunc) {
			for (Item item : items) {
				output.accept(stackFunc.apply(item), visibilityFunc.apply(item));
			}
		}

		private record ItemOrdering(Item item, Item anchor, Type type) {
			public static ItemOrdering before(Item item, Item anchor) {
				return new ItemOrdering(item, anchor, Type.BEFORE);
			}

			public static ItemOrdering after(Item item, Item anchor) {
				return new ItemOrdering(item, anchor, Type.AFTER);
			}

			public enum Type {
				BEFORE,
				AFTER;
			}
		}
	}

}