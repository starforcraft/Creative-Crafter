package com.YTrollman.CreativeCrafter.util;

import java.util.List;

import net.minecraft.world.item.ItemStack;

public interface IMultipleRequirements {
    List<ItemStack> getMultipleRequirementSets(boolean simulate, int numOfRequirements);
}