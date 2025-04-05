package fr.iglee42.cmr.mixins;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.logistics.stockTicker.*;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerRenderer;
import com.simibubi.create.foundation.gui.ScreenWithStencils;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import fr.iglee42.cmr.cooler.SnowmanCoolerBlock;
import fr.iglee42.cmr.cooler.SnowmanCoolerBlockEntity;
import fr.iglee42.cmr.cooler.SnowmanCoolerRenderer;
import fr.iglee42.cmr.init.CMRPartials;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.lang.ref.WeakReference;
import java.util.List;

@Mixin(value = StockKeeperRequestScreen.class,remap = false)
public class StockKeeperRequestScreenMixin extends AbstractSimiContainerScreen<StockKeeperRequestMenu>
        implements ScreenWithStencils {

    @Shadow private StockTickerBlockEntity blockEntity;
    @Shadow private int windowHeight;
    @Unique
    private WeakReference<SnowmanCoolerBlockEntity> cmr$cooler;

    public StockKeeperRequestScreenMixin(StockKeeperRequestMenu container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    @Inject(method = "<init>",at = @At("TAIL"))
    private void cmr$initCooler(StockKeeperRequestMenu container, Inventory inv, Component title, CallbackInfo ci) {
        cmr$cooler = new WeakReference<>(null);
        for (int yOffset : Iterate.zeroAndOne) {
            for (Direction side : Iterate.horizontalDirections) {
                BlockPos seatPos = blockEntity.getBlockPos()
                        .below(yOffset)
                        .relative(side);
                if (yOffset == 0 && blockEntity.getLevel()
                        .getBlockEntity(seatPos) instanceof SnowmanCoolerBlockEntity bbbe) {
                    cmr$cooler = new WeakReference<>(bbbe);
                    return;
                }
            }
        }
    }


    @Inject(method = "containerTick",remap = false,at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;closeContainer()V",shift= At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void cmr$dontCloseForCooler(CallbackInfo ci, boolean allEmpty, List clientStockSnapshot, LivingEntity keeper, BlazeBurnerBlockEntity blazeKeeper){
       if (cmr$cooler != null && cmr$cooler.get() != null && !cmr$cooler.get().isRemoved()) ci.cancel();
    }

    @Inject(method = "renderBg",remap = false,at = @At(value = "INVOKE", target = "Ljava/lang/ref/WeakReference;get()Ljava/lang/Object;",shift= At.Shift.BEFORE,ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void cmr$showCooler(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY, CallbackInfo ci, PoseStack ms, float currentScroll, Couple hoveredSlot, int x, int y, int entitySizeOffset, LivingEntity keeper){
        if (cmr$cooler != null) {
            SnowmanCoolerBlockEntity cooler = cmr$cooler.get();
            if (cooler != null && !cooler.isRemoved()) {
                ms.pushPose();
                int entityX = x - 35;
                int entityY = y + windowHeight - 43;
                ms.translate(entityX, entityY, -0);
                ms.mulPose(Axis.XP.rotationDegrees(-22.5f));
                ms.mulPose(Axis.YP.rotationDegrees(-45));
                ms.scale(48, -48, 48);
                float animation = cooler.headAnimation.getValue(AnimationTickHolder.getPartialTicks()) * .175f;
                float horizontalAngle = AngleHelper.rad(270);
                SnowmanCoolerBlock.HeatLevel heatLevel = cooler.getHeatLevelForRender();
                boolean canDrawFlame = heatLevel.isAtLeast(SnowmanCoolerBlock.HeatLevel.FADING);
                boolean drawGoggles = cooler.goggles;
                PartialModel drawHat = AllPartialModels.LOGISTICS_HAT;
                int hashCode = cooler.hashCode();
                Lighting.setupForEntityInInventory();

                VertexConsumer cutout = graphics.bufferSource().getBuffer(RenderType.cutoutMipped());
                CachedBuffers.partial(CMRPartials.SNOWMAN_CAGE, cooler.getBlockState())
                        .rotateCentered(horizontalAngle + Mth.PI, Direction.UP)
                        .light(LightTexture.FULL_BRIGHT)
                        .renderInto(ms, cutout);

                SnowmanCoolerRenderer.renderShared(ms, null, graphics.bufferSource(), minecraft.level,
                        cooler.getBlockState(), heatLevel, animation, horizontalAngle, canDrawFlame, drawGoggles, drawHat,
                        hashCode);
                Lighting.setupFor3DItems();
                ms.popPose();
            }
        }
    }


    @Unique
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {}
}
