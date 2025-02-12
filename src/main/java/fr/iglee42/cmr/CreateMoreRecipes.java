package fr.iglee42.cmr;

import com.mojang.logging.LogUtils;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.foundation.data.CreateRegistrate;
import fr.iglee42.cmr.mixins.HeatConditionMixin;
import fr.iglee42.cmr.ponder.CMRPonderTags;
import fr.iglee42.cmr.ponder.PonderIndex;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod(CreateMoreRecipes.MODID)
public class CreateMoreRecipes {

    public static final String MODID = "cmr";
    public static final String coldId = "cooled";
    public static final String freezeId = "frozen";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID).setCreativeTab(CMRCreativeModeTabs.MAIN_TAB);

    public static final Map<HeatCondition,String> CUSTOM_HEAT_CONDITIONS = new HashMap<>();

    public CreateMoreRecipes() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        REGISTRATE.registerEventListeners(modEventBus);

        CMRTags.init();
        CMRRegistries.register();

        CMRFanProcessingTypes.register();
        CMRRecipeTypes.register(modEventBus);
        CMRSpriteShifts.init();

        CMRCreativeModeTabs.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);


        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,()-> CMRPartials::init);

        MinecraftForge.EVENT_BUS.register(this);
    }

    public static ResourceLocation asResource(String id) {
        return new ResourceLocation(MODID,id);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    private void clientSetup(final FMLClientSetupEvent event){

        CMRPonderTags.register();
        PonderIndex.register();
    }
    public static BlockPos getPosOfCatalyst(BlockPos fanPos, Level level, Direction direction,int distance){
        BlockPos currentPos = fanPos;
        if (!(level.getBlockEntity(fanPos) instanceof EncasedFanBlockEntity fan)) return fanPos;
        if (!fan.getAirCurrent().pushing) {
            for (int i = 0; i < fan.getAirCurrent().maxDistance; i++) {
                if (!CMRFanProcessingTypes.CUSTOM.isValidAt(level, currentPos))
                    currentPos = fanPos.relative(direction, i);
            }
        } else {
            for (int i = distance; i > 0; i--) {
                if (!CMRFanProcessingTypes.CUSTOM.isValidAt(level, currentPos))
                    currentPos = fanPos.relative(direction, i);
            }
        }

        return currentPos;

    }


}
