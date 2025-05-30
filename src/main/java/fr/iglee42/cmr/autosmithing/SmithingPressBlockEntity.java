package fr.iglee42.cmr.autosmithing;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.advancement.CreateAdvancement;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.recipe.RecipeApplier;

import com.simibubi.create.foundation.utility.CreateLang;
import fr.iglee42.cmr.init.CMRRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class SmithingPressBlockEntity extends BasinOperatingBlockEntity implements SmithingBehaviour.SmithingBehaviourSpecifics {

	private static final Object smithingRecipesKey = new Object();

	public SmithingBehaviour smithingBehaviour;
	protected SmartInventory templateInv;
	protected SmartInventory additionInv;


	public SmithingPressBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		templateInv = new SmartInventory(1,this);
		additionInv = new SmartInventory(1,this);
	}

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(
				Capabilities.ItemHandler.BLOCK,
				CMRRegistries.SMITHING_PRESS_BE.get(),
				(be, context) ->{
					if (Objects.equals(context, Direction.UP) || Objects.equals(context,Direction.DOWN))return be.templateInv;
					return be.additionInv;
				}
		);
	}


	@Override
	protected AABB createRenderBoundingBox() {
		return new AABB(worldPosition).expandTowards(0, -1.5, 0)
			.expandTowards(0, 1, 0);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);
		smithingBehaviour = new SmithingBehaviour(this);
		behaviours.add(smithingBehaviour);

	}

	@Override
	public void destroy() {
		ItemHelper.dropContents(level, worldPosition, templateInv);
		ItemHelper.dropContents(level, worldPosition, additionInv);
		super.destroy();
	}

	public SmithingBehaviour getSmithingBehaviour() {
		return smithingBehaviour;
	}

	@Override
	protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(compound, registries, clientPacket);
		compound.put("Template", templateInv.serializeNBT(registries));
		compound.put("Addition", additionInv.serializeNBT(registries));
	}

	@Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(compound, registries, clientPacket);
		templateInv.deserializeNBT(registries, compound.getCompound("Template"));
		additionInv.deserializeNBT(registries, compound.getCompound("Addition"));
	}


	@Override
	public boolean tryProcessOnBelt(TransportedItemStack input, List<ItemStack> outputList, boolean simulate) {
		Optional<RecipeHolder<SmithingRecipe>> recipe = getRecipe(input.stack);
		if (!recipe.isPresent())
			return false;
		ItemStack output = recipe.get().value().assemble(
				new SmithingRecipeInput(templateInv.getStackInSlot(0),input.stack.copyWithCount(1),additionInv.getStackInSlot(0)),
				level.registryAccess()
		);
		if (simulate) {
			return !output.isEmpty();
		}
		smithingBehaviour.particleItems.add(input.stack);
		if (!output.isEmpty()){
			templateInv.extractItem(0,1,false);
			additionInv.extractItem(0,1,false);
			outputList.add(output);
			return true;
		} else {
			return false;
		}
	}


	@Override
	public void onPressingCompleted() {
		basinChecker.scheduleUpdate();
	}

	public Optional<RecipeHolder<SmithingRecipe>> getRecipe(ItemStack item) {
		if (level == null) return Optional.empty();
        return level.getRecipeManager().getRecipeFor(RecipeType.SMITHING,new SmithingRecipeInput(templateInv.getStackInSlot(0),item,additionInv.getStackInSlot(0)),level);
	}

	@Override
	protected boolean matchStaticFilters(RecipeHolder<? extends Recipe<?>> recipe) {
		return recipe.value() instanceof SmithingRecipe;
	}

	@Override
	public float getKineticSpeed() {
		return getSpeed();
	}


	@Override
	protected Object getRecipeCacheKey() {
		return smithingRecipesKey;
	}

	@Override
	public int getParticleAmount() {
		return 15;
	}


	@Override
	protected void onBasinRemoved() {
		smithingBehaviour.particleItems.clear();
		smithingBehaviour.running = false;
		smithingBehaviour.runningTicks = 0;
		sendData();
	}

	@Override
	protected boolean isRunning() {
		return smithingBehaviour.running;
	}

	@Override
	protected Optional<CreateAdvancement> getProcessedRecipeTrigger() {
		return Optional.of(AllAdvancements.COMPACTING);
	}

	public SmartInventory getInvForSide(Direction direction) {
		if (direction.equals(Direction.UP) || direction.equals(Direction.DOWN))
			return templateInv;
		return additionInv;
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		if (!templateInv.isEmpty())
			CreateLang.translate("tooltip.deployer.contains", Component.translatable(templateInv.getStackInSlot(0).getDescriptionId())
							.getString(), templateInv.getStackInSlot(0).getCount())
					.style(ChatFormatting.GREEN)
					.forGoggles(tooltip);
		if (!additionInv.isEmpty())
			CreateLang.translate("tooltip.deployer.contains", Component.translatable(additionInv.getStackInSlot(0).getDescriptionId())
							.getString(), additionInv.getStackInSlot(0).getCount())
					.style(ChatFormatting.GREEN)
					.forGoggles(tooltip);
		return super.addToGoggleTooltip(tooltip, isPlayerSneaking);
	}
}
