package com.mrbysco.junkdrawers.block.blockentity;

import net.minecraft.util.Tuple;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RandomizedItemStackHandler extends ItemStacksResourceHandler {
	public RandomizedItemStackHandler(int size) {
		super(size);
	}

	@Override
	public boolean isValid(int index, ItemResource resource) {
		return super.isValid(index, resource);
	}

	public void randomizeInventory(Transaction tx) {
		Map<Integer, Tuple<ItemResource, Integer>> contents = new HashMap<>();

		// Collect all items from the slots
		for (int i = 0; i < size(); i++) {
			contents.put(i, new Tuple<>(getResource(i), getAmountAsInt(i)));
		}

		// Shuffle the slot indices
		List<Integer> list = new ArrayList<>(contents.keySet());
		Collections.shuffle(list);

		// Create a temporary map to hold the shuffled items
		Map<Integer, Tuple<ItemResource, Integer>> shuffleMap = new LinkedHashMap<>();
		list.forEach(k -> shuffleMap.put(k, contents.get(k)));

		// Clear the original slots and reassign the shuffled items
		for (int i = 0; i < size(); i++) {
			set(i, ItemResource.EMPTY, 0);
		}

		int index = 0;
		for (Map.Entry<Integer, Tuple<ItemResource, Integer>> entry : shuffleMap.entrySet()) {
			set(index, entry.getValue().getA(), entry.getValue().getB());
			index++;
		}

		// Verify the integrity of the inventory
		int totalItemsBefore = contents.values().stream().mapToInt(Tuple::getB).sum();
		int totalItemsAfter = 0;
		for (int i = 0; i < size(); i++) {
			totalItemsAfter += getAmountAsInt(i);
		}

		if (totalItemsBefore != totalItemsAfter) {
			throw new IllegalStateException("Item duplication detected: before=" + totalItemsBefore + ", after=" + totalItemsAfter);
		} else {
			tx.commit();
		}
	}
}
