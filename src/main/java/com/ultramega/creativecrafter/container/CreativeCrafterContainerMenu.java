package com.ultramega.creativecrafter.container;

import com.ultramega.creativecrafter.CreativeCrafter;
import com.ultramega.creativecrafter.blockentity.CreativeCrafterBlockEntity;
import com.ultramega.creativecrafter.registry.ModContainerMenus;
import com.refinedmods.refinedstorage.container.BaseContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.SlotItemHandler;

public class CreativeCrafterContainerMenu extends BaseContainerMenu {
    private final CreativeCrafterBlockEntity tile;

    public CreativeCrafterContainerMenu(int windowId, Player player, CreativeCrafterBlockEntity tile) {
        super(ModContainerMenus.CREATIVE_CRAFTER_CONTAINER.get(), tile, player, windowId);
        this.tile = tile;

        for(int i = 0; i < CreativeCrafter.ROWS; i++)
            for(int j = 0; j < 9; j++)
                addSlot(new SlotItemHandler(tile.getNode().getPatternItems(), (i * 9) + j, 8 + (18 * j), 20 + (18 * i)));

        addPlayerInventory(8, 253);

        transferManager.addBiTransfer(player.getInventory(), tile.getNode().getPatternItems());
    }

    @Override
    public CreativeCrafterBlockEntity getBlockEntity() {
        return tile;
    }
}
