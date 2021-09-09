package com.YTrollman.CreativeCrafter.mixin;

import com.YTrollman.CreativeCrafter.CreativeCrafter;
import com.refinedmods.refinedstorage.container.BaseContainer;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.refinedmods.refinedstorage.container.slot.legacy.LegacyDisabledSlot;
import com.refinedmods.refinedstorage.container.slot.legacy.LegacyFilterSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import javax.annotation.Nullable;

@Mixin(value = BaseContainer.class, remap = false)
public class MixinBaseContainer extends Container {

    protected MixinBaseContainer(@Nullable ContainerType<?> type, int windowId) {
        super(type, windowId);
    }

    /**
     * @author
     */
    @Overwrite
    public ItemStack clicked(int id, int dragType, ClickType clickType, PlayerEntity player) {
        Slot slot = id >= 0 ? this.getSlot(id) : null;
        int disabledSlotNumber = this.getDisabledSlotNumber();
        if (disabledSlotNumber != -1 && clickType == ClickType.SWAP && dragType == disabledSlotNumber) {
            CreativeCrafter.LOGGER.info("asd");
            return ItemStack.EMPTY;
        } else if (slot instanceof FilterSlot) {
            CreativeCrafter.LOGGER.info("asd2");
            if (((FilterSlot)slot).isSizeAllowed()) {
                if (clickType == ClickType.QUICK_MOVE) {
                    slot.set(ItemStack.EMPTY);
                } else if (!player.inventory.getCarried().isEmpty()) {
                    slot.set(player.inventory.getCarried().copy());
                }
            } else if (player.inventory.getCarried().isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else if (slot.mayPlace(player.inventory.getCarried())) {
                slot.set(player.inventory.getCarried().copy());
            }

            return player.inventory.getCarried();
        } else if (slot instanceof FluidFilterSlot) {
            CreativeCrafter.LOGGER.info("asd3");
            if (((FluidFilterSlot)slot).isSizeAllowed()) {
                if (clickType == ClickType.QUICK_MOVE) {
                    ((FluidFilterSlot)slot).onContainerClicked(ItemStack.EMPTY);
                } else if (!player.inventory.getCarried().isEmpty()) {
                    ((FluidFilterSlot)slot).onContainerClicked(player.inventory.getCarried());
                }
            } else if (player.inventory.getCarried().isEmpty()) {
                ((FluidFilterSlot)slot).onContainerClicked(ItemStack.EMPTY);
            } else {
                ((FluidFilterSlot)slot).onContainerClicked(player.inventory.getCarried());
            }

            return player.inventory.getCarried();
        } else if (slot instanceof LegacyFilterSlot) {
            CreativeCrafter.LOGGER.info("asd4");
            if (player.inventory.getCarried().isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else if (slot.mayPlace(player.inventory.getCarried())) {
                slot.set(player.inventory.getCarried().copy());
            }

            return player.inventory.getCarried();
        } else {
            CreativeCrafter.LOGGER.info("asd5");
            CreativeCrafter.LOGGER.info(id + " id");
            CreativeCrafter.LOGGER.info(dragType + " dragType");
            CreativeCrafter.LOGGER.info(clickType + " clickType");
            CreativeCrafter.LOGGER.info(slot.index + " slot");
            return slot instanceof LegacyDisabledSlot ? ItemStack.EMPTY : super.clicked(id, dragType, clickType, player);
        }
    }

    @Override
    public boolean stillValid(PlayerEntity p_75145_1_) {
        return true;
    }

    protected int getDisabledSlotNumber() {
        return -1;
    }
}
