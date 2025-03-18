package fr.iglee42.cmr.recipes;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import fr.iglee42.cmr.init.CMRRecipeTypes;

public class CMRProcessingRecipeSerializer<T extends ProcessingRecipe<?>> extends ProcessingRecipeSerializer<T> {

    public final MapCodec<T> CODEC = CMRRecipeTypes.CODEC.dispatchMap(t -> (CMRRecipeTypes) t.getTypeInfo(), CMRRecipeTypes::processingCodec);


    public CMRProcessingRecipeSerializer(ProcessingRecipeBuilder.ProcessingRecipeFactory<T> factory) {
        super(factory);
    }


    @Override
    public MapCodec<T> codec() {
        return CODEC;
    }
}
