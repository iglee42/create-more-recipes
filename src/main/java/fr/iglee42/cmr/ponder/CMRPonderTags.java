package fr.iglee42.cmr.ponder;

import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.RegistryEntry;
import fr.iglee42.cmr.CreateMoreRecipes;
import fr.iglee42.cmr.init.CMRRegistries;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

public class CMRPonderTags {



	private static ResourceLocation loc(String id) {
		return CreateMoreRecipes.asResource(id);
	}

	public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {
		PonderTagRegistrationHelper<RegistryEntry<?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);
		PonderTagRegistrationHelper<ItemLike> itemHelper = helper.withKeyFunction(
				CatnipServices.REGISTRIES::getKeyOrThrow);

		HELPER.addToTag(AllCreatePonderTags.ARM_TARGETS).add(CMRRegistries.SNOWMAN_COOLER);
		HELPER.addToTag(AllCreatePonderTags.KINETIC_APPLIANCES).add(CMRRegistries.SMITHING_PRESS);
		HELPER.addToTag(AllCreatePonderTags.FLUIDS).add(CMRRegistries.BLOCK_SPOUT);


	}

}