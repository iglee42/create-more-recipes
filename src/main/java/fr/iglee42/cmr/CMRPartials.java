package fr.iglee42.cmr;

import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.Create;

public class CMRPartials {

    public static final PartialModel SNOWMAN_INERT = block("snowman_cooler/snowman/inert");
    public static final PartialModel SNOWMAN_SUPER_ACTIVE = block("snowman_cooler/snowman/super_active");
    public static final PartialModel SNOWMAN_IDLE = block("snowman_cooler/snowman/idle");
    public static final PartialModel SNOWMAN_ACTIVE = block("snowman_cooler/snowman/active");
    public static final PartialModel SNOWMAN_SUPER = block("snowman_cooler/snowman/super");
    public static final PartialModel SNOWMAN_FLAME = block("snowman_cooler/flame");

    private static PartialModel block(String path) {
        return new PartialModel(CreateMoreRecipes.asResource("block/" + path));
    }

    private static PartialModel entity(String path) {
        return new PartialModel(CreateMoreRecipes.asResource("entity/" + path));
    }

    public static void init() {}

}
