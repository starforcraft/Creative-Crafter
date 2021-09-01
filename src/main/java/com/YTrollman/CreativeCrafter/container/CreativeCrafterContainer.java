package com.YTrollman.CreativeCrafter.container;

import com.YTrollman.CreativeCrafter.registry.ModContainers;
import com.YTrollman.CreativeCrafter.tileentity.CreativeCrafterTileEntity;
import com.refinedmods.refinedstorage.api.network.grid.ICraftingGridListener;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.api.network.grid.handler.IItemGridHandler;
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCache;
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCacheListener;
import com.refinedmods.refinedstorage.container.BaseContainer;
import com.refinedmods.refinedstorage.container.slot.grid.CraftingGridSlot;
import com.refinedmods.refinedstorage.screen.IScreenInfoProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SSetSlotPacket;

import java.util.Iterator;

public class CreativeCrafterContainer extends BaseContainer implements ICraftingGridListener
{
    private final CreativeCrafterTileEntity tile;
    private IGrid grid;
    private IStorageCache storageCache;
    private IStorageCacheListener storageCacheListener;
    private IScreenInfoProvider screenInfoProvider;
    private PlayerEntity player;

    public CreativeCrafterContainer(IGrid grid, int windowId, PlayerEntity player, CreativeCrafterTileEntity tile)
    {
        super(ModContainers.CREATIVE_CRAFTER_CONTAINER.get(), tile, player, windowId);
        this.tile = tile;
        this.grid = grid;
        this.player = player;
        grid.addCraftingListener(this);
    }

    public void initSlots() {
        this.slots.clear();
        this.lastSlots.clear();

        this.transferManager.clearTransfers();

        transferManager.setNotFoundHandler((slotIndex) -> {
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
        });

        addPlayerInventory(8, screenInfoProvider.getYPlayerInventory());

        transferManager.addBiTransfer(player.inventory, tile.getNode().getPatternItems());
    }

    @Override
    public CreativeCrafterTileEntity getTile()
    {
        return tile;
    }

    public IScreenInfoProvider getScreenInfoProvider() {
        return this.screenInfoProvider;
    }

    public void setScreenInfoProvider(IScreenInfoProvider screenInfoProvider) {
        this.screenInfoProvider = screenInfoProvider;
    }

    public IGrid getGrid() {
        return grid;
    }

    @Override
    public void onCraftingMatrixChanged() {
        for(int i = 0; i < this.slots.size(); ++i) {
            Slot slot = this.slots.get(i);
            if (slot instanceof CraftingGridSlot) {
                Iterator var3 = this.containerListeners.iterator();

                while(var3.hasNext()) {
                    IContainerListener listener = (IContainerListener)var3.next();
                    if (listener instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity)listener).connection.send(new SSetSlotPacket(this.containerId, i, slot.getItem()));
                    }
                }
            }
        }
    }

    @Override
    public void broadcastChanges() {
        if (!this.getPlayer().level.isClientSide) {
            if (this.grid.getStorageCache() == null) {
                if (this.storageCacheListener != null) {
                    this.storageCache.removeListener(this.storageCacheListener);
                    this.storageCacheListener = null;
                    this.storageCache = null;
                }
            } else if (this.storageCacheListener == null) {
                this.storageCacheListener = this.grid.createListener((ServerPlayerEntity)this.getPlayer());
                this.storageCache = this.grid.getStorageCache();
                this.storageCache.addListener(this.storageCacheListener);
            }
        }

        super.broadcastChanges();
    }

    @Override
    public void removed(PlayerEntity player) {
        super.removed(player);
        if (!player.getCommandSenderWorld().isClientSide) {
            this.grid.onClosed(player);
            if (this.storageCache != null && this.storageCacheListener != null) {
                this.storageCache.removeListener(this.storageCacheListener);
            }
        }

        this.grid.removeCraftingListener(this);
    }

    @Override
    protected int getDisabledSlotNumber() {
        return grid.getSlotId();
    }
}
