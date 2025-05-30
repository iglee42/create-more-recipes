package fr.iglee42.cmr.ponder;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.simibubi.create.infrastructure.ponder.scenes.fluid.SpoutScenes;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import fr.iglee42.cmr.init.CMRRegistries;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class CMRPonderScenes {

	public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {

		PonderSceneRegistrationHelper<ItemProviderEntry<?,?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

		HELPER.forComponents(CMRRegistries.EMPTY_SNOWMAN_COOLER).addStoryBoard("empty_snowman_cooler",CustomPonderScenes::emptySnowmanCooler);
		HELPER.forComponents(CMRRegistries.SNOWMAN_COOLER).addStoryBoard("snowman_cooler",CustomPonderScenes::snowmanCooler,AllCreatePonderTags.ARM_TARGETS);
		HELPER.forComponents(CMRRegistries.SMITHING_PRESS)
				.addStoryBoard("smithing", CustomPonderScenes::smithing);
		HELPER.forComponents(CMRRegistries.BLOCK_SPOUT)
				.addStoryBoard("block_spout", CustomPonderScenes::blockSpout);

	}


}