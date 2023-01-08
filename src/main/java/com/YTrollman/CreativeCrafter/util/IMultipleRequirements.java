package com.YTrollman.CreativeCrafter.util;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IMultipleRequirements {
    List<ItemStack> getMultipleRequirementSets(boolean simulate, int numOfRequirements);
}