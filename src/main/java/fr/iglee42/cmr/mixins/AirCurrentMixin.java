package fr.iglee42.cmr.mixins;

import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.fan.AirCurrent;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity;
import com.simibubi.create.content.kinetics.fan.IAirCurrentSource;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import fr.iglee42.cmr.CMRFanProcessingTypes;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

@Mixin(value = AirCurrent.class,remap = false)
public class AirCurrentMixin {

    @Shadow public Direction direction;

    @Shadow @Final public IAirCurrentSource source;

    @Inject(method = "tickAffectedEntities",at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/fan/processing/FanProcessing;canProcess(Lnet/minecraft/world/entity/item/ItemEntity;Lcom/simibubi/create/content/kinetics/fan/processing/FanProcessingType;)Z",shift = At.Shift.BEFORE),locals = LocalCapture.CAPTURE_FAILSOFT)
    private void cmr$tickEntites(Level world, CallbackInfo ci, Iterator iterator, Entity entity, Vec3i flow, float speed, float sneakModifier, double entityDistance, double entityDistanceOld, float acceleration, Vec3 previousMotion, float maxAcceleration, double xIn, double yIn, double zIn, FanProcessingType processingType, ItemEntity itemEntity){
        if (processingType instanceof CMRFanProcessingTypes.CustomizableFanType type){
            if (type.canCustomProcess(itemEntity, processingType,world,direction,source.getAirCurrentPos(),entityDistance))
                if (type.applyProcessingEntity(itemEntity, processingType,direction,source.getAirCurrentPos(),entityDistance)
                        && source instanceof EncasedFanBlockEntity fan)
                    fan.award(AllAdvancements.FAN_PROCESSING);
        }
    }

    @Inject(method = "lambda$tickAffectedHandlers$2",at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/fan/processing/FanProcessing;applyProcessing(Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;Lnet/minecraft/world/level/Level;Lcom/simibubi/create/content/kinetics/fan/processing/FanProcessingType;)Lcom/simibubi/create/content/kinetics/belt/behaviour/TransportedItemStackHandlerBehaviour$TransportedResult;",shift = At.Shift.AFTER),locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void cmr$tickHandlers(Level world, FanProcessingType processingType, TransportedItemStackHandlerBehaviour handler, TransportedItemStack transported, CallbackInfoReturnable<TransportedItemStackHandlerBehaviour.TransportedResult> cir){
        if (processingType instanceof CMRFanProcessingTypes.CustomizableFanType type){
            int dif = switch (direction.getAxis()){
                case X -> handler.blockEntity.getBlockPos().get(Direction.Axis.X) - source.getAirCurrentPos().get(Direction.Axis.X);
                case Y -> handler.blockEntity.getBlockPos().get(Direction.Axis.Y) - source.getAirCurrentPos().get(Direction.Axis.Y);
                case Z -> handler.blockEntity.getBlockPos().get(Direction.Axis.Z) - source.getAirCurrentPos().get(Direction.Axis.Z);
            };
            if (dif < 0) dif = -dif;
            if (transported.processedBy instanceof CMRFanProcessingTypes.CustomizableFanType && transported.processingTime == -1) transported.processedBy = AllFanProcessingTypes.NONE;
            TransportedItemStackHandlerBehaviour.TransportedResult applyProcessing = type.applyProcessingHandlers(transported, world, processingType,direction,source.getAirCurrentPos(),dif);
            if (!applyProcessing.doesNothing() && source instanceof EncasedFanBlockEntity fan)
                fan.award(AllAdvancements.FAN_PROCESSING);
            cir.setReturnValue(applyProcessing);
        }
    }

}
