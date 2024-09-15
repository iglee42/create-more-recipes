package fr.iglee42.cmr;

import com.mojang.logging.LogUtils;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity;
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

@Mod(CreateMoreRecipes.MODID)
public class CreateMoreRecipes {

    public static final String MODID = "cmr";
    private static final Logger LOGGER = LogUtils.getLogger();


    public CreateMoreRecipes() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        CMRFanProcessingTypes.register();
        CMRRecipeTypes.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    public static ResourceLocation asResource(String id) {
        return new ResourceLocation(MODID,id);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
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
