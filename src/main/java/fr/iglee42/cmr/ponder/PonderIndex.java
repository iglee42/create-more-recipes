package fr.iglee42.cmr.ponder;

import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import fr.iglee42.cmr.CMRRegistries;
import fr.iglee42.cmr.CreateMoreRecipes;

public class PonderIndex {

	static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(CreateMoreRecipes.MODID);


	public static void register() {
		// Register storyboards here
		// (!) Added entries require re-launch
		// (!) Modifications inside storyboard methods only require re-opening the ui


		HELPER.forComponents(CMRRegistries.EMPTY_SNOWMAN_COOLER).addStoryBoard("empty_snowman_cooler",CustomPonderScenes::emptySnowmanCooler);
		HELPER.forComponents(CMRRegistries.SNOWMAN_COOLER).addStoryBoard("snowman_cooler",CustomPonderScenes::snowmanCooler);


	}


}
