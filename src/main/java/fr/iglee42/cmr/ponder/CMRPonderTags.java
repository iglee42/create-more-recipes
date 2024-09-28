package fr.iglee42.cmr.ponder;

import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.PonderTag;
import com.simibubi.create.infrastructure.ponder.AllPonderTags;
import fr.iglee42.cmr.CMRRegistries;
import fr.iglee42.cmr.CreateMoreRecipes;

public class CMRPonderTags {


	private static PonderTag create(String id) {
		return new PonderTag(CreateMoreRecipes.asResource(id));
	}


	public static void register() {


		PonderRegistry.TAGS.forTag(AllPonderTags.ARM_TARGETS)
						.add(CMRRegistries.SNOWMAN_COOLER);



	}

}
