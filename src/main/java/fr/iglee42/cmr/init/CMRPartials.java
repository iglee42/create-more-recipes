package fr.iglee42.cmr.init;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import fr.iglee42.cmr.CreateMoreRecipes;

public class CMRPartials {

    public static final PartialModel SNOWMAN_INERT = block("snowman_cooler/snowman/inert");
    public static final PartialModel SNOWMAN_SUPER_ACTIVE = block("snowman_cooler/snowman/super_active");
    public static final PartialModel SNOWMAN_IDLE = block("snowman_cooler/snowman/idle");
    public static final PartialModel SNOWMAN_ACTIVE = block("snowman_cooler/snowman/active");
    public static final PartialModel SNOWMAN_SUPER = block("snowman_cooler/snowman/super");
    public static final PartialModel SNOWMAN_FLAME = block("snowman_cooler/flame");
    public static final PartialModel SNOWMAN_CAGE = block("snowman_cooler/block");
    public static final PartialModel BLOCK_SPOUT_BOTTOM = block("block_spout/bottom");

    private static PartialModel block(String path) {
        return PartialModel.of(CreateMoreRecipes.asResource("block/" + path));
    }

    private static PartialModel entity(String path) {
        return PartialModel.of(CreateMoreRecipes.asResource("entity/" + path));
    }

    public static void init() {}

}
