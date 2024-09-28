package fr.iglee42.cmr.jei;

import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.block.render.SpriteShiftEntry;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;

import fr.iglee42.cmr.CMRPartials;
import fr.iglee42.cmr.CMRRegistries;
import fr.iglee42.cmr.CMRSpriteShifts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;

import fr.iglee42.cmr.cooler.SnowmanCoolerBlock.HeatLevel;

public class AnimatedSnowmanCooler extends AnimatedKinetics {

	private HeatLevel heatLevel;

	public AnimatedSnowmanCooler withHeat(HeatLevel heatLevel) {
		this.heatLevel = heatLevel;
		return this;
	}

	public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
		PoseStack matrixStack = graphics.pose();
		matrixStack.pushPose();
		matrixStack.translate(xOffset, yOffset, 200);
		matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
		matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));
		int scale = 23;

		float offset = (Mth.sin(AnimationTickHolder.getRenderTime() / 16f) + 0.5f) / 16f;

		blockElement(CMRRegistries.SNOWMAN_COOLER.getDefaultState()).atLocal(0, 1.65, 0)
			.scale(scale)
			.render(graphics);

		PartialModel blaze =
			heatLevel == HeatLevel.FREEZING ? CMRPartials.SNOWMAN_SUPER: CMRPartials.SNOWMAN_ACTIVE;

		blockElement(blaze).atLocal(1, 1.8, 1)
			.rotate(0, 180, 0)
			.scale(scale)
			.render(graphics);

		matrixStack.scale(scale, -scale, scale);
		matrixStack.translate(0, -1.8, 0);

		SpriteShiftEntry spriteShift =
			heatLevel == HeatLevel.FREEZING ? CMRSpriteShifts.SUPER_COOLER_FLAME : CMRSpriteShifts.COOLER_FLAME;

		float spriteWidth = spriteShift.getTarget()
			.getU1()
			- spriteShift.getTarget()
				.getU0();

		float spriteHeight = spriteShift.getTarget()
			.getV1()
			- spriteShift.getTarget()
				.getV0();

		float time = AnimationTickHolder.getRenderTime(Minecraft.getInstance().level);
		float speed = 1 / 32f + 1 / 64f * heatLevel.ordinal();

		double vScroll = speed * time;
		vScroll = vScroll - Math.floor(vScroll);
		vScroll = vScroll * spriteHeight / 2;

		double uScroll = speed * time / 2;
		uScroll = uScroll - Math.floor(uScroll);
		uScroll = uScroll * spriteWidth / 2;

		CachedBufferer.partial(CMRPartials.SNOWMAN_FLAME, Blocks.AIR.defaultBlockState())
		.shiftUVScrolling(spriteShift, (float) uScroll, (float) vScroll)
		.light(LightTexture.FULL_BRIGHT)
			.renderInto(matrixStack, graphics.bufferSource().getBuffer(RenderType.cutoutMipped()));
		matrixStack.popPose();
	}

}