package fr.iglee42.cmr;

import com.mojang.logging.LogUtils;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.foundation.data.CreateRegistrate;
import fr.iglee42.cmr.init.*;
import fr.iglee42.cmr.ponder.CMRPonderTags;
import fr.iglee42.cmr.ponder.PonderIndex;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

@Mod(CreateMoreRecipes.MODID)
public class CreateMoreRecipes {

    public static final String MODID = "cmr";
    public static final String coldId = "cooled";
    public static final String freezeId = "frozen";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID).setCreativeTab(CMRCreativeModeTabs.MAIN_TAB);

    public static final Map<HeatCondition,String> CUSTOM_HEAT_CONDITIONS = new HashMap<>();

    public CreateMoreRecipes(IEventBus modEventBus) {

        REGISTRATE.registerEventListeners(modEventBus);

        CMRTags.init();
        CMRRegistries.register();

        CMRFanProcessingTypes.register();
        CMRRecipeTypes.register(modEventBus);
        CMRSpriteShifts.init();

        CMRCreativeModeTabs.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::registerCapabilities);


        RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT,()-> CMRPartials::init);

    }

    public static ResourceLocation asResource(String id) {
        return ResourceLocation.fromNamespaceAndPath(MODID,id);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    private void clientSetup(final FMLClientSetupEvent event){

        CMRPonderTags.register();
        PonderIndex.register();
    }
    private void registerCapabilities(RegisterCapabilitiesEvent event){
        BlockSpoutBlockEntity.registerCapabilities(event);
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
