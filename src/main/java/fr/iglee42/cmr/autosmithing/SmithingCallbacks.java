package fr.iglee42.cmr.autosmithing;

import static com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour.ProcessingResult.HOLD;
import static com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour.ProcessingResult.PASS;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour.ProcessingResult;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour.TransportedResult;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;

import net.minecraft.world.item.ItemStack;

public class SmithingCallbacks {

	static ProcessingResult onItemReceived(TransportedItemStack transported,
		TransportedItemStackHandlerBehaviour handler, SmithingBehaviour behaviour) {
		if (behaviour.specifics.getKineticSpeed() == 0)
			return PASS;
		if (behaviour.running)
			return HOLD;
		if (!behaviour.specifics.tryProcessOnBelt(transported, null, true))
			return PASS;

		behaviour.start();
		return HOLD;
	}

	static ProcessingResult whenItemHeld(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler,
										 SmithingBehaviour behaviour) {

		if (behaviour.specifics.getKineticSpeed() == 0)
			return PASS;
		if (!behaviour.running)
			return PASS;
		if (behaviour.runningTicks != SmithingBehaviour.CYCLE / 2)
			return HOLD;

		behaviour.particleItems.clear();
		ArrayList<ItemStack> results = new ArrayList<>();
		if (!behaviour.specifics.tryProcessOnBelt(transported, results, false))
			return PASS;

		boolean bulk = false;

		transported.clearFanProcessingData();
		
		List<TransportedItemStack> collect = results.stream()
			.map(stack -> {
				TransportedItemStack copy = transported.copy();
				boolean centered = BeltHelper.isItemUpright(stack);
				copy.stack = stack;
				copy.locked = true;
				copy.angle = centered ? 180 : Create.RANDOM.nextInt(360);
				return copy;
			})
			.collect(Collectors.toList());

        TransportedItemStack left = transported.copy();
        left.stack.shrink(1);

        if (collect.isEmpty())
            handler.handleProcessingOnItem(transported, TransportedResult.convertTo(left));
        else
            handler.handleProcessingOnItem(transported, TransportedResult.convertToAndLeaveHeld(collect, left));

        behaviour.blockEntity.sendData();
		return HOLD;
	}

}
