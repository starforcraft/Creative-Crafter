package com.YTrollman.CreativeCrafter.container;

import com.YTrollman.CreativeCrafter.CreativeCrafter;
import com.YTrollman.CreativeCrafter.registry.ModContainers;
import com.YTrollman.CreativeCrafter.tileentity.CreativeCrafterTileEntity;
import com.refinedmods.refinedstorage.container.BaseContainer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.SlotItemHandler;

public class CreativeCrafterContainer extends BaseContainer
{
    private final CreativeCrafterTileEntity tile;

    public CreativeCrafterContainer(int windowId, PlayerEntity player, CreativeCrafterTileEntity tile)
    {
        super(ModContainers.CREATIVE_CRAFTER_CONTAINER.get(),tile, player, windowId);
        this.tile = tile;

        for(int i = 0; i < CreativeCrafter.ROWS; i++)
            for(int j = 0; j < 9; j++)
                addSlot(new SlotItemHandler(tile.getNode().getPatternItems(), (i * 9) + j, 8 + (18 * j), 20 + (18 * i)));

        addPlayerInventory(8, 253);
        
        transferManager.addBiTransfer(player.inventory, tile.getNode().getPatternItems());
    }

    @Override
    public CreativeCrafterTileEntity getTile()
    {
        return tile;
    }
}
