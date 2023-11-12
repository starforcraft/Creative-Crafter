package com.ultramega.creativecrafter.mixin;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.IoUtil;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node.CraftingNode;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node.Node;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node.NodeList;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node.NodeListener;
import com.ultramega.creativecrafter.node.CreativeCrafterNetworkNode;
import com.ultramega.creativecrafter.util.IMultipleRequirements;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(CraftingNode.class)
public abstract class MixinCraftingNode extends Node {
    @Mutable
    @Shadow
    @Final
    private final NonNullList<ItemStack> recipe;

    protected MixinCraftingNode(ICraftingPattern pattern, boolean root, NonNullList<ItemStack> recipe) {
        super(pattern, root);
        this.recipe = recipe;
    }

    @Inject(method = "update", at = @At("HEAD"), remap = false, cancellable = true)
    public void update(INetwork network, int ticks, NodeList nodes, IStorageDisk<ItemStack> internalStorage, IStorageDisk<FluidStack> internalFluidStorage, NodeListener listener, CallbackInfo ci) {
        var containers = network.getCraftingManager().getAllContainers(getPattern());
        var hasCreativeCrafter = containers.stream().anyMatch(CreativeCrafterNetworkNode.class::isInstance);

        if (hasCreativeCrafter) {
            ci.cancel();

            var mRequirements = (IMultipleRequirements) requirements;

            if (IoUtil.extractFromInternalItemStorage(mRequirements.creativeCrafter$getMultipleRequirementSets(true, getQuantity()), internalStorage, Action.SIMULATE) != null) {
                List<ItemStack> requirementSet;
                if (getQuantity() == 1) {
                    requirementSet = requirements.getSingleItemRequirementSet(false);
                } else {
                    requirementSet = mRequirements.creativeCrafter$getMultipleRequirementSets(false, getQuantity());
                }

                IoUtil.extractFromInternalItemStorage(requirementSet, internalStorage, Action.PERFORM);

                ItemStack output = getPattern().getOutput(recipe);
                output.setCount(output.getCount() * getQuantity());

                if (!isRoot()) {
                    internalStorage.insert(output, output.getCount(), Action.PERFORM);
                } else {
                    ItemStack remainder = network.insertItem(output, output.getCount(), Action.PERFORM);

                    internalStorage.insert(remainder, remainder.getCount() * getQuantity(), Action.PERFORM);
                }

                // Byproducts need to always be inserted in the internal storage for later reuse further in the task.
                // Regular outputs can be inserted into the network *IF* it's a root since it's *NOT* expected to be used later on.
                for (ItemStack byp : getPattern().getByproducts(recipe)) {
                    internalStorage.insert(byp, byp.getCount(), Action.PERFORM);
                }

                listener.onAllDone(this);
            }
        }
    }
}