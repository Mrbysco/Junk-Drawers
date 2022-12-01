package com.mrbysco.junkdrawers.menu;

import com.mrbysco.junkdrawers.block.blockentity.DrawerBlockEntity;
import com.mrbysco.junkdrawers.registry.JunkRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.Objects;

public class DrawerMenu extends AbstractContainerMenu {
	private DrawerBlockEntity drawerBE;
	private final BlockPos drawerPos;

	public DrawerMenu(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
		this(windowId, playerInventory, getBlockEntity(playerInventory, data));
	}

	private static DrawerBlockEntity getBlockEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
		Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
		Objects.requireNonNull(data, "data cannot be null!");
		final BlockEntity BlockEntityAtPos = playerInventory.player.level.getBlockEntity(data.readBlockPos());

		if (BlockEntityAtPos instanceof DrawerBlockEntity) {
			return (DrawerBlockEntity) BlockEntityAtPos;
		}

		throw new IllegalStateException("Block entity is not correct! " + BlockEntityAtPos);
	}

	public DrawerMenu(int id, Inventory inventory, DrawerBlockEntity drawerBlockEntity) {
		super(JunkRegistry.DRAWER_MENU.get(), id);
		this.drawerBE = drawerBlockEntity;
		this.drawerPos = drawerBlockEntity.getBlockPos();
		int i = (2 - 4) * 18;

		//Inventory
		if (drawerBE.handler != null) {
			int rows = 10;
			for (int j = 0; j < rows; ++j) {
				for (int k = 0; k < 9; ++k) {
					this.addSlot(new InvisibleSlot(drawerBE.handler, k + j * 9, 8 + k * 18, 18 + j * 18));
				}
			}
		}

		//player inventory here
		for (int l = 0; l < 3; ++l) {
			for (int j1 = 0; j1 < 9; ++j1) {
				this.addSlot(new Slot(inventory, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
			}
		}

		for (int i1 = 0; i1 < 9; ++i1) {
			this.addSlot(new Slot(inventory, i1, 8 + i1 * 18, 161 + i));
		}
	}

	@Override
	public void removed(Player player) {
		if (player != null) {
			player.level.playSound(null, drawerPos, JunkRegistry.DRAWER_CLOSE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
		}
		super.removed(player);
	}

	@Override
	@Nonnull
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);

		if (slot != null && slot.hasItem()) {
			ItemStack slotStack = slot.getItem();
			itemstack = slotStack.copy();
			final int drawerSize = 90;

			if (index < drawerSize) {
				if (!this.moveItemStackTo(slotStack, drawerSize, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else {
				if (!this.moveItemStackTo(slotStack, 0, drawerSize, true)) {
					return ItemStack.EMPTY;
				}
			}

			if (slotStack.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}

		return itemstack;
	}

	@Override
	public void slotsChanged(Container container) {
		super.slotsChanged(container);
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return this.drawerBE.stillValid(playerIn) && !playerIn.isSpectator();
	}

	public class InvisibleSlot extends SlotItemHandler {
		private final int index;

		public InvisibleSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
			super(itemHandler, index, xPosition, yPosition);
			this.index = index;
		}

		@Override
		public boolean isActive() {
			return index < 18;
		}

		@Override
		public boolean mayPlace(@Nonnull ItemStack stack) {
			return super.mayPlace(stack);
		}
	}
}
