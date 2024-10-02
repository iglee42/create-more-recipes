package fr.iglee42.cmr.mixins;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import fr.iglee42.cmr.CreateMoreRecipes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(value = HeatCondition.class,remap = false)
public class HeatConditionMixin {

    @Shadow
    @Final
    @Mutable
    private static HeatCondition[] $VALUES;


    @Invoker("<init>")
    public static HeatCondition cmr$initInvoker(String internalName, int color,int internalId){
        throw new AssertionError();
    }

    @Inject(method = "testBlazeBurner",at = @At("HEAD"), cancellable = true)
    private void cmr$alwaysForCustom(BlazeBurnerBlock.HeatLevel level, CallbackInfoReturnable<Boolean> cir){
        if (CreateMoreRecipes.CUSTOM_HEAT_CONDITIONS.containsKey(this) && level == BlazeBurnerBlock.HeatLevel.NONE )cir.setReturnValue(true);
    }

    @Inject(method = "<clinit>",at = @At("TAIL"))
    private static void cmr$clinit(CallbackInfo ci){
        for (HeatCondition v : $VALUES) {
            System.out.println(v.name());
        }

        Map<String,Integer> custom = Map.of(CreateMoreRecipes.coldId,0xE3F3F3, CreateMoreRecipes.freezeId,0x82E1FF);


        custom.forEach((n,c)->{
            CreateMoreRecipes.CUSTOM_HEAT_CONDITIONS.put(cmr$addVariant(n.toUpperCase(Locale.ROOT),c),n);
        });
    }

    @Unique
    private static HeatCondition cmr$addVariant(String internalName, int color) {
        ArrayList<HeatCondition> variants = new ArrayList<>(Arrays.asList($VALUES));
        HeatCondition heat = cmr$initInvoker(internalName, variants.get(variants.size() - 1).ordinal() + 1, color);
        variants.add(heat);
        HeatConditionMixin.$VALUES = variants.toArray(new HeatCondition[0]);
        return heat;
    }



}
