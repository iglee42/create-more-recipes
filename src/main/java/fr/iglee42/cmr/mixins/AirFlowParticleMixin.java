package fr.iglee42.cmr.mixins;

import com.simibubi.create.content.kinetics.fan.AirCurrent;
import com.simibubi.create.content.kinetics.fan.AirFlowParticle;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import fr.iglee42.cmr.CMRFanProcessingTypes;
import fr.iglee42.cmr.CreateMoreRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = AirFlowParticle.class,remap = true)
public abstract class AirFlowParticleMixin {


    @Inject(method = "tick",at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/fan/processing/FanProcessingType;morphAirFlow(Lcom/simibubi/create/content/kinetics/fan/processing/FanProcessingType$AirFlowParticleAccess;Lnet/minecraft/util/RandomSource;)V",shift = At.Shift.AFTER),locals = LocalCapture.CAPTURE_FAILSOFT)
    private void cmr$tick(CallbackInfo ci, AirCurrent airCurrent, Vec3 directionVec, Vec3 motion, double distance, FanProcessingType inType){
        if (inType instanceof CMRFanProcessingTypes.CustomizableFanType type){

            Level level = airCurrent.source.getAirCurrentWorld();
            BlockPos currentPos = CreateMoreRecipes.getPosOfCatalyst(airCurrent.source.getAirCurrentPos(),level,airCurrent.direction, (int) distance);

            if (type.isValidAt(level,currentPos)){
                if (level.random.nextFloat() < 1 / 2f){
                    type.getAccess().spawnExtraParticle(new BlockParticleOption(ParticleTypes.BLOCK,level.getBlockState(currentPos)),0.25f);
                }

            }

        }
    }

}
