package com.ultramega.creativecrafter.util;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IMultipleRequirements {
    List<ItemStack> creativeCrafter$getMultipleRequirementSets(boolean simulate, int numOfRequirements);
}