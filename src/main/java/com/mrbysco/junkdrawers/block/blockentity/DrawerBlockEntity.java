package com.mrbysco.junkdrawers.block.blockentity;

import com.mrbysco.junkdrawers.JunkDrawers;
import com.mrbysco.junkdrawers.config.JunkConfig;
import com.mrbysco.junkdrawers.menu.DrawerMenu;
import com.mrbysco.junkdrawers.registry.JunkRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DrawerBlockEntity extends BlockEntity implements MenuProvider {
	public final RandomizedItemStackHandler handler = new RandomizedItemStackHandler(90) {
		@Override
		protected void onContentsChanged(int slot) {
			if (JunkConfig.COMMON.randomizeOnContentChange.get()) {
				this.randomizeInventory();
			}
			super.onContentsChanged(slot);
			refreshClient();
		}
	};

	public DrawerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
		super(blockEntityType, pos, state);
	}

	public DrawerBlockEntity(BlockPos pos, BlockState state) {
		this(JunkRegistry.DRAWER_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	protected void loadAdditional(@NotNull ValueInput input) {
		super.loadAdditional(input);
		handler.deserialize(input.childOrEmpty("ItemStackHandler"));
	}

	@Override
	protected void saveAdditional(@NotNull ValueOutput output) {
		super.saveAdditional(output);
		handler.serialize(output.child("ItemStackHandler"));
	}

	public RandomizedItemStackHandler getHandler(@Nullable Direction direction) {
		return handler;
	}

	@Override
	public void onDataPacket(@NotNull Connection net, @NotNull ValueInput valueInput) {
		super.onDataPacket(net, valueInput);

		BlockState state = level.getBlockState(getBlockPos());
		level.sendBlockUpdated(getBlockPos(), state, state, 3);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider lookupProvider) {
		CompoundTag tag = new CompoundTag();
		try (ProblemReporter.ScopedCollector problemreporter$scopedcollector = new ProblemReporter.ScopedCollector(JunkDrawers.LOGGER)) {
			TagValueOutput output = TagValueOutput.createWithContext(problemreporter$scopedcollector, lookupProvider);
			this.saveAdditional(output);
			tag.merge(output.buildResult());
		}
		return tag;
	}

	@Override
	public CompoundTag getPersistentData() {
		CompoundTag tag = new CompoundTag();
		try (ProblemReporter.ScopedCollector problemreporter$scopedcollector = new ProblemReporter.ScopedCollector(JunkDrawers.LOGGER)) {
			HolderLookup.Provider lookupProvider = this.level != null ? this.level.registryAccess() : VanillaRegistries.createLookup();
			TagValueOutput output = TagValueOutput.createWithContext(problemreporter$scopedcollector, lookupProvider);
			this.saveAdditional(output);
			tag.merge(output.buildResult());
		}
		return tag;
	}

	public void refreshClient() {
		setChanged();
		BlockState state = level.getBlockState(worldPosition);
		level.sendBlockUpdated(worldPosition, state, state, 2);
	}

	@Nullable
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable(JunkDrawers.MOD_ID + ".container.drawer");
	}

	public boolean stillValid(Player player) {
		if (this.level.getBlockEntity(this.worldPosition) != this) {
			return false;
		} else {
			return !(player.distanceToSqr((double) this.worldPosition.getX() + 0.5D,
					(double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) > 64.0D);
		}
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
		return new DrawerMenu(id, inventory, this);
	}
}
