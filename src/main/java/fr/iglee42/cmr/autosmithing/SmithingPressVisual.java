package fr.iglee42.cmr.autosmithing;

import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.ShaftVisual;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlock;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.createmod.catnip.math.AngleHelper;
import org.joml.Quaternionf;

import java.util.function.Consumer;

public class SmithingPressVisual extends ShaftVisual<SmithingPressBlockEntity> implements SimpleDynamicVisual {

	private final OrientedInstance pressHead;

	public SmithingPressVisual(VisualizationContext context, SmithingPressBlockEntity blockEntity, float partialTick) {
		super(context, blockEntity, partialTick);

		pressHead = instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial(AllPartialModels.MECHANICAL_PRESS_HEAD))
				.createInstance();

		Quaternionf q = Axis.YP
			.rotationDegrees(AngleHelper.horizontalAngle(blockState.getValue(SmithingPressBlock.HORIZONTAL_FACING)));

		pressHead.rotation(q);

		transformModels(partialTick);
	}

    @Override
    public void beginFrame(DynamicVisual.Context ctx) {
        transformModels(ctx.partialTick());
    }

    private void transformModels(float pt) {
		float renderedHeadOffset = getRenderedHeadOffset(pt);

		pressHead.position(getVisualPosition())
			.translatePosition(0, -renderedHeadOffset, 0)
			.setChanged();
	}

	private float getRenderedHeadOffset(float pt) {
		SmithingBehaviour smithingBehaviour = blockEntity.getSmithingBehaviour();
		return smithingBehaviour.getRenderedHeadOffset(pt)
			* 19f/16f;
	}

	@Override
	public void updateLight(float partialTick) {
		super.updateLight(partialTick);
		relight(pressHead);
	}

	@Override
    protected void _delete() {
		super._delete();
		pressHead.delete();
	}

	@Override
	public void collectCrumblingInstances(Consumer<Instance> consumer) {
		super.collectCrumblingInstances(consumer);
		consumer.accept(pressHead);
	}
}
