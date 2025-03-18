package fr.iglee42.cmr.mixins;

import com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.content.logistics.stockTicker.StockCheckingBlockEntity;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import fr.iglee42.cmr.init.CMRRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.util.Locals;

import java.util.List;

@Mixin(StockTickerBlockEntity.class)
public class StockTickerBlockEntityMixin extends StockCheckingBlockEntity implements IHaveHoveringInformation {


    public StockTickerBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type,pos,state);
    }

    @Inject(method = "isKeeperPresent",remap = false,at = @At(value = "INVOKE", target = "Lcom/tterrag/registrate/util/entry/BlockEntityEntry;is(Lnet/minecraft/world/level/block/entity/BlockEntity;)Z",shift= At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void cmr$allowCooler(CallbackInfoReturnable<Boolean> cir, int[] var1, int var2, int var3, int yOffset, Direction[] var5, int var6, int var7, Direction side, BlockPos seatPos){
        if (yOffset == 0 && CMRRegistries.COOLER.is(level.getBlockEntity(seatPos))) cir.setReturnValue(true);
    }

}
