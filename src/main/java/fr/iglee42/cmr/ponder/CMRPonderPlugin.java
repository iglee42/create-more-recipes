package fr.iglee42.cmr.ponder;

import fr.iglee42.cmr.CreateMoreRecipes;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class CMRPonderPlugin implements PonderPlugin {
    @Override
    public String getModId() {
        return CreateMoreRecipes.MODID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        CMRPonderScenes.register(helper);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        CMRPonderTags.register(helper);
    }
}