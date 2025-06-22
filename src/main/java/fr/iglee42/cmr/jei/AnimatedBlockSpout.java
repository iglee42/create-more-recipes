package fr.iglee42.cmr.jei;

import java.util.List;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.fluid.FluidRenderer;

import fr.iglee42.cmr.init.CMRPartials;
import fr.iglee42.cmr.init.CMRRegistries;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.platform.NeoForgeCatnipServices;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;

public class AnimatedBlockSpout extends AnimatedKinetics {

	private List<FluidStack> fluids;
	private BlockState state;

	public AnimatedBlockSpout withFluids(List<FluidStack> fluids) {
		this.fluids = fluids;
		return this;
	}

	public AnimatedBlockSpout withState(BlockState state) {
		this.state = state;
		return this;
	}

	@Override
	public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
		PoseStack matrixStack = graphics.pose();
		matrixStack.pushPose();
		matrixStack.translate(xOffset, yOffset, 100);
		matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
		matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));
		int scale = 20;

		blockElement(CMRRegistries.BLOCK_SPOUT.getDefaultState())
			.scale(scale)
			.render(graphics);

		float cycle = (AnimationTickHolder.getRenderTime() - offset * 8) % 30;
		float squeeze = cycle < 20 ? Mth.sin((float) (cycle / 20f * Math.PI)) : 0;
		squeeze *= 20;

		matrixStack.pushPose();

		blockElement(AllPartialModels.SPOUT_TOP)
			.scale(scale)
			.render(graphics);
		blockElement(AllPartialModels.SPOUT_MIDDLE)
			.scale(scale)
			.render(graphics);
		matrixStack.translate(0, squeeze / 2f, 0);
		blockElement(CMRPartials.BLOCK_SPOUT_BOTTOM)
			.scale(scale)
			.render(graphics);
		matrixStack.translate(0, -3 * squeeze / 32f, 0);

		matrixStack.popPose();

		blockElement(state)
				.atLocal(0, 2, 0)
				.scale(scale)
				.render(graphics);

		AnimatedKinetics.DEFAULT_LIGHTING.applyLighting();
		matrixStack.pushPose();
		UIRenderHelper.flipForGuiRender(matrixStack);
		matrixStack.scale(16, 16, 16);
		float from = 3f / 16f;
		float to = 17f / 16f;
		FluidStack fluidStack = fluids.get(0);
		NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox(fluidStack, from, from, from, to, to, to, graphics.bufferSource(), matrixStack, LightTexture.FULL_BRIGHT, false, true);
		matrixStack.popPose();
		Lighting.setupFor3DItems();

		matrixStack.popPose();
	}


}
