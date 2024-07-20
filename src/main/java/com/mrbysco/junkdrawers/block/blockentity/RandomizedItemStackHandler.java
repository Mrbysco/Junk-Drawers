package com.mrbysco.junkdrawers.block.blockentity;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RandomizedItemStackHandler extends ItemStackHandler {
	public RandomizedItemStackHandler(int size) {
		super(size);
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return super.isItemValid(slot, stack);
	}

	/**
	 * Randomizes the items in the inventory
	 */
	public void randomizeInventory() {
		Map<Integer, ItemStack> contents = new HashMap<>();

		// Collect all items from the slots
		for (int i = 0; i < getSlots(); i++) {
			contents.put(i, getStackInSlot(i));
		}

		// Shuffle the slot indices
		List<Integer> list = new ArrayList<>(contents.keySet());
		Collections.shuffle(list);

		// Create a temporary map to hold the shuffled items
		Map<Integer, ItemStack> shuffleMap = new LinkedHashMap<>();
		list.forEach(k -> shuffleMap.put(k, contents.get(k)));

		// Clear the original slots and reassign the shuffled items
		for (int i = 0; i < getSlots(); i++) {
			validateSlotIndex(i);
			this.stacks.set(i, ItemStack.EMPTY);
		}

		int index = 0;
		for (Map.Entry<Integer, ItemStack> entry : shuffleMap.entrySet()) {
			validateSlotIndex(index);
			this.stacks.set(index, entry.getValue());
			index++;
		}

		// Verify the integrity of the inventory
		int totalItemsBefore = contents.values().stream().mapToInt(ItemStack::getCount).sum();
		int totalItemsAfter = 0;
		for (int i = 0; i < getSlots(); i++) {
			totalItemsAfter += getStackInSlot(i).getCount();
		}

		if (totalItemsBefore != totalItemsAfter) {
			throw new IllegalStateException("Item duplication detected: before=" + totalItemsBefore + ", after=" + totalItemsAfter);
		}
	}
}
