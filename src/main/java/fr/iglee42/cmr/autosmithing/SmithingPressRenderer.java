package fr.iglee42.cmr.autosmithing;

import static com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;

import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class SmithingPressRenderer extends KineticBlockEntityRenderer<SmithingPressBlockEntity> {

	public SmithingPressRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public boolean shouldRenderOffScreen(SmithingPressBlockEntity be) {
		return true;
	}

	@Override
	protected void renderSafe(SmithingPressBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
		int light, int overlay) {
		renderItem( be.templateInv.getItem(0),true,be,partialTicks,ms,buffer,light,overlay);
		renderItem( be.additionInv.getItem(0),false,be,partialTicks,ms,buffer,light,overlay);
		super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

		if (VisualizationManager.supportsVisualization(be.getLevel()))
			return;

		BlockState blockState = be.getBlockState();
		SmithingBehaviour pressingBehaviour = be.getSmithingBehaviour();
		float renderedHeadOffset =
			pressingBehaviour.getRenderedHeadOffset(partialTicks) * 19f/16f;

		SuperByteBuffer headRender = CachedBuffers.partialFacing(AllPartialModels.MECHANICAL_PRESS_HEAD, blockState,
			blockState.getValue(HORIZONTAL_FACING));
		headRender.translate(0, -renderedHeadOffset, 0)
			.light(light)
			.renderInto(ms, buffer.getBuffer(RenderType.solid()));
	}


	protected void renderItem(ItemStack heldItem,boolean isTemplate,SmithingPressBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
							  int light, int overlay) {

		if (heldItem.isEmpty()) return;

		float headOffset = be.getSmithingBehaviour().getRenderedHeadOffset(partialTicks);
		float headPosY = headOffset * 19f / 16f;
		BlockState state = be.getBlockState();
		Vec3 handOffset;

		if (isTemplate) {
			handOffset = getHandOffset(be, partialTicks, state);
		} else {
			if (headPosY <= 0.325f) {
				handOffset = Vec3.atLowerCornerOf(Direction.DOWN.getNormal()).scale(0);
			} else {
				handOffset = getHandOffset(be, partialTicks, state).add(0, -0.05, 0);
			}
		}
		Vec3 offset =  handOffset
				.add(VecHelper.getCenterOf(BlockPos.ZERO))
				.add(0, isTemplate || headPosY > 0.325 ? 0.15 : -0.35, 0);
		ms.pushPose();
		ms.translate(offset.x, offset.y, offset.z);

		Direction facing = Direction.DOWN;

        ms.mulPose(Axis.YP.rotationDegrees(AngleHelper.horizontalAngle(facing) + 180));

		ms.mulPose(Axis.XP.rotationDegrees(270));
		ms.translate(0, 0, -11 / 16f);

        ItemRenderer itemRenderer = Minecraft.getInstance()
				.getItemRenderer();

		ItemDisplayContext transform = ItemDisplayContext.NONE;
		BakedModel bakedModel = itemRenderer.getModel(heldItem, be.getLevel(), null, 0);
		boolean isBlockItem = (heldItem.getItem() instanceof BlockItem) && bakedModel.isGui3d();

        float scale = isBlockItem ? .75f - 1 / 64f : .5f;
        ms.scale(scale, scale, scale);
        transform = ItemDisplayContext.FIXED;

		if (!isTemplate)ms.scale(1.45f,1.45f,1.45f);

        itemRenderer.render(heldItem, transform, false, ms, buffer, light, overlay, bakedModel);
		ms.popPose();
	}

	protected Vec3 getHandOffset(SmithingPressBlockEntity be, float partialTicks, BlockState blockState) {
		float distance = be.getSmithingBehaviour().getRenderedHeadOffset(partialTicks) * 19f/16f;
		return Vec3.atLowerCornerOf(Direction.DOWN.getNormal()).scale(distance);
	}

	@Override
	protected BlockState getRenderedBlockState(SmithingPressBlockEntity be) {
		return shaft(getRotationAxisOf(be));
	}

}
