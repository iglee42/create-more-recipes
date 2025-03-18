package fr.iglee42.cmr;

import com.mojang.logging.LogUtils;
import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.RegistrateDistExecutor;
import fr.iglee42.cmr.blockspout.BlockSpoutBlockEntity;
import fr.iglee42.cmr.init.*;
import fr.iglee42.cmr.ponder.CMRPonderPlugin;
import fr.iglee42.cmr.ponder.CMRPonderTags;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

@Mod(CreateMoreRecipes.MODID)
public class CreateMoreRecipes {

    public static final String MODID = "cmr";
    public static final String coldId = "cooled";
    public static final String freezeId = "frozen";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID)
            .defaultCreativeTab((ResourceKey<CreativeModeTab>) null);

    public static final Map<HeatCondition,String> CUSTOM_HEAT_CONDITIONS = new HashMap<>();

    public CreateMoreRecipes() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        REGISTRATE.registerEventListeners(modEventBus);

        CMRTags.init();
        CMRRegistries.register();

        CMRRecipeTypes.register(modEventBus);
        CMRSpriteShifts.init();

        CMRCreativeModeTabs.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerEvent);
        if (FMLEnvironment.dist == Dist.CLIENT)modEventBus.addListener(this::clientSetup);


        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,()-> CMRPartials::init);

        MinecraftForge.EVENT_BUS.register(this);
    }

    public static ResourceLocation asResource(String id) {
        return new ResourceLocation(MODID,id);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    private void clientSetup(final FMLClientSetupEvent event){

        PonderIndex.addPlugin(new CMRPonderPlugin());

    }

    private void registerEvent(RegisterEvent event){
        CMRFanProcessingTypes.init();
        CMRArmInteractionPointTypes.init();
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
