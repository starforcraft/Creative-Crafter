package com.YTrollman.CreativeCrafter.container;

import com.YTrollman.CreativeCrafter.CreativeCrafter;
import com.YTrollman.CreativeCrafter.registry.ModContainers;
import com.YTrollman.CreativeCrafter.tileentity.CreativeCrafterTileEntity;
import com.refinedmods.refinedstorage.container.BaseContainer;
<<<<<<< HEAD
import com.refinedmods.refinedstorage.container.slot.grid.CraftingGridSlot;
import com.refinedmods.refinedstorage.screen.IScreenInfoProvider;
import com.refinedmods.refinedstorage.tile.grid.portable.IPortableGrid;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraftforge.items.SlotItemHandler;
=======
>>>>>>> parent of acbfd01 (WIP Infinite Slots)

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.SlotItemHandler;

public class CreativeCrafterContainer extends BaseContainer
{
    private final CreativeCrafterTileEntity tile;

    public CreativeCrafterContainer(int windowId, PlayerEntity player, CreativeCrafterTileEntity tile)
    {
        super(ModContainers.CREATIVE_CRAFTER_CONTAINER.get(),tile, player, windowId);
        this.tile = tile;
<<<<<<< HEAD
        this.grid = grid;
        this.player = player;
        grid.addCraftingListener(this);
    }

    public void initSlots() {
        this.slots.clear();
        this.lastSlots.clear();

        this.transferManager.clearTransfers();

        for (int i = 0; i < tile.getNode().getPatternInventory().getSlots(); ++i) {
            addSlot(new SlotItemHandler(tile.getNode().getPatternInventory(), i, 8 + (18 * i), 19));
        }

        /*transferManager.setNotFoundHandler((slotIndex) -> {
            if (!this.getPlayer().getCommandSenderWorld().isClientSide) {
                Slot slot = this.slots.get(slotIndex);

                if (slot.hasItem()) {
                    ItemStack stack = slot.getItem();

                    IItemGridHandler itemHandler = this.grid.getItemHandler();
                    if (itemHandler != null) {
                        slot.set(itemHandler.onInsert((ServerPlayerEntity)this.getPlayer(), stack, false));
                    } else if (slot instanceof CraftingGridSlot && this.moveItemStackTo(stack, 14, 14 + (9 * 4), false)) {
                        slot.setChanged();
                        this.grid.onCraftingMatrixChanged();
                    }

                    this.broadcastChanges();
                }
            }

            return ItemStack.EMPTY;
        });*/

        addPlayerInventory(8, screenInfoProvider.getYPlayerInventory());
=======

        for(int i = 0; i < CreativeCrafter.ROWS; i++)
            for(int j = 0; j < 9; j++)
                addSlot(new SlotItemHandler(tile.getNode().getPatternItems(), (i * 9) + j, 8 + (18 * j), 20 + (18 * i)));
>>>>>>> parent of acbfd01 (WIP Infinite Slots)

        addPlayerInventory(8, 253);
        
        transferManager.addBiTransfer(player.inventory, tile.getNode().getPatternItems());
    }

    @Override
    public CreativeCrafterTileEntity getTile()
    {
        return tile;
    }
}
