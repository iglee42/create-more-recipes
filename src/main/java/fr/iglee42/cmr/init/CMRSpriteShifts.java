package fr.iglee42.cmr.init;

import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import com.simibubi.create.foundation.block.connected.CTType;
import com.simibubi.create.foundation.block.render.SpriteShiftEntry;
import com.simibubi.create.foundation.block.render.SpriteShifter;

import fr.iglee42.cmr.CreateMoreRecipes;

public class CMRSpriteShifts {

	public static final SpriteShiftEntry COOLER_FLAME =
		get("block/snowman_flame", "block/snowman_flame_scroll"),
		SUPER_COOLER_FLAME = get("block/snowman_flame", "block/snowman_flame_super_scroll");

	//

	private static CTSpriteShiftEntry omni(String name) {
		return getCT(AllCTTypes.OMNIDIRECTIONAL, name);
	}

	private static CTSpriteShiftEntry horizontal(String name) {
		return getCT(AllCTTypes.HORIZONTAL, name);
	}

	private static CTSpriteShiftEntry vertical(String name) {
		return getCT(AllCTTypes.VERTICAL, name);
	}

	//

	public static void init(){}

	private static SpriteShiftEntry get(String originalLocation, String targetLocation) {
		return SpriteShifter.get(CreateMoreRecipes.asResource(originalLocation), CreateMoreRecipes.asResource(targetLocation));
	}

	private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName, String connectedTextureName) {
		return CTSpriteShifter.getCT(type, CreateMoreRecipes.asResource("block/" + blockTextureName),
			CreateMoreRecipes.asResource("block/" + connectedTextureName + "_connected"));
	}

	private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName) {
		return getCT(type, blockTextureName, blockTextureName);
	}


}