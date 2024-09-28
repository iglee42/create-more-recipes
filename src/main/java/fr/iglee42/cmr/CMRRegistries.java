package fr.iglee42.cmr;

import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockItem;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import fr.iglee42.cmr.cooler.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

import static com.simibubi.create.AllInteractionBehaviours.interactionBehaviour;
import static com.simibubi.create.AllMovementBehaviours.movementBehaviour;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;
import static fr.iglee42.cmr.CreateMoreRecipes.REGISTRATE;

public class CMRRegistries {



    public static final BlockEntry<SnowmanCoolerBlock> SNOWMAN_COOLER =
            REGISTRATE.block("snowman_cooler", SnowmanCoolerBlock::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(p -> p.mapColor(MapColor.COLOR_GRAY))
                    .transform(pickaxeOnly())
                    .addLayer(() -> RenderType::cutoutMipped)
                    .tag(AllTags.AllBlockTags.FAN_TRANSPARENT.tag)
                    .blockstate((a,b)->{})
                    .onRegister(movementBehaviour(new SnowmanCoolerMovementBehaviour()))
                    .onRegister(interactionBehaviour(new SnowmanCoolerInteractionBehaviour()))
                    .item()
                    .model(AssetLookup.customBlockItemModel("snowman_cooler", "block_with_blaze"))
                    .build()
                    .register();

    public static final BlockEntry<Block> EMPTY_SNOWMAN_COOLER =
            REGISTRATE.block("empty_snowman_cooler", Block::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(p -> p.mapColor(MapColor.COLOR_GRAY).noOcclusion())
                    .transform(pickaxeOnly())
                    .blockstate((a,b)->{})
                    .addLayer(() -> RenderType::cutoutMipped)
                    .tag(AllTags.AllBlockTags.FAN_TRANSPARENT.tag)
                    .item(EmptyCoolerBlockItem::new)
                    .model(AssetLookup.customBlockItemModel("snowman_cooler", "block"))
                    .build()
                    .register();


    public static final ItemEntry<Item> FROZEN_CAKE_BASE =
            REGISTRATE.item("frozen_cake_base", Item::new)
                    .tag(AllTags.AllItemTags.UPRIGHT_ON_BELT.tag)
                    .register();

    public static final ItemEntry<Item> FROZEN_CAKE = REGISTRATE.item("frozen_cake", Item::new)
            .tag(CMRTags.CMRItemTags.SNOWMAN_COOLER_FUEL_SPECIAL.tag, AllTags.AllItemTags.UPRIGHT_ON_BELT.tag)
            .register();


    public static final BlockEntityEntry<SnowmanCoolerBlockEntity> COOLER = REGISTRATE
            .blockEntity("blaze_heater", SnowmanCoolerBlockEntity::new)
            .validBlocks(SNOWMAN_COOLER)
            .renderer(() -> SnowmanCoolerRenderer::new)
            .register();


    public static void register(){}
}
