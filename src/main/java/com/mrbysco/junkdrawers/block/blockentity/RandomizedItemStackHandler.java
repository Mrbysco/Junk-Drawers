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

	public void randomizeInventory() {
		Map<Integer, ItemStack> contents = new HashMap<>();
		for (int i = 0; i < getSlots(); i++) {
			contents.put(i, getStackInSlot(i));
			validateSlotIndex(i);
			this.stacks.set(i, ItemStack.EMPTY);
		}

		List<Integer> list = new ArrayList<>(contents.keySet());
		Collections.shuffle(list);

		Map<Integer, ItemStack> shuffleMap = new LinkedHashMap<>();
		list.forEach(k -> shuffleMap.put(k, contents.get(k)));

		int index = 0;
		for (Map.Entry<Integer, ItemStack> entry : shuffleMap.entrySet()) {
			validateSlotIndex(index);
			this.stacks.set(index, entry.getValue());
			index++;
		}
	}
}
