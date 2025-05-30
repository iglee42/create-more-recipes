package fr.iglee42.cmr.autosmithing;

import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.advancement.CreateAdvancement;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SmithingPressBlockEntity extends BasinOperatingBlockEntity implements SmithingBehaviour.SmithingBehaviourSpecifics {

	private static final Object smithingRecipesKey = new Object();

	public SmithingBehaviour smithingBehaviour;
	protected SmartInventory templateInv;
	protected SmartInventory additionInv;
	protected LazyOptional<IItemHandler> templateCapability;
	protected LazyOptional<IItemHandler> additionCapability;



	public SmithingPressBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		templateInv = new SmartInventory(1,this);
		templateCapability = LazyOptional.of(()->templateInv);
		additionInv = new SmartInventory(1,this);
		additionCapability = LazyOptional.of(()->additionInv);
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (isItemHandlerCap(cap)){
			return Objects.equals(side, Direction.DOWN) || Objects.equals(side, Direction.UP) ? templateCapability.cast() : additionCapability.cast();
		}
		return super.getCapability(cap, side);
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
	public void invalidate() {
		templateCapability.invalidate();
		additionCapability.invalidate();
		super.invalidate();
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
	protected void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		compound.put("Template", templateInv.serializeNBT());
		compound.put("Addition", additionInv.serializeNBT());
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		super.read(compound, clientPacket);
		templateInv.deserializeNBT(compound.getCompound("Template"));
		additionInv.deserializeNBT(compound.getCompound("Addition"));
	}


	@Override
	public boolean tryProcessOnBelt(TransportedItemStack input, List<ItemStack> outputList, boolean simulate) {
		Optional<SmithingRecipe> recipe = getRecipe(input.stack);
		if (!recipe.isPresent())
			return false;
		ItemStackHandler handler = new ItemStackHandler(3);
		handler.setStackInSlot(0,templateInv.getStackInSlot(0));
		handler.setStackInSlot(1,input.stack);
		handler.setStackInSlot(2,additionInv.getStackInSlot(0));
		SimpleContainer inventory = new SimpleContainer(handler.getSlots());
		ItemStack output = recipe.get().assemble(
				inventory,
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

	public Optional<SmithingRecipe> getRecipe(ItemStack item) {
		if (level == null) return Optional.empty();
		ItemStackHandler handler = new ItemStackHandler(3);
		handler.setStackInSlot(0,templateInv.getStackInSlot(0));
		handler.setStackInSlot(1,item);
		handler.setStackInSlot(2,additionInv.getStackInSlot(0));
		SimpleContainer inventory = new SimpleContainer(handler.getSlots());
		for (int i = 0; i < handler.getSlots(); i++) {
			inventory.setItem(i, handler.getStackInSlot(i));
		}

		return level.getRecipeManager().getRecipeFor(RecipeType.SMITHING, inventory,level);
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

	@Override
	protected <C extends Container> boolean matchStaticFilters(Recipe<C> recipe) {
		return recipe instanceof SmithingRecipe;
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
