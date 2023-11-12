package com.ultramega.creativecrafter.container;

import com.refinedmods.refinedstorage.container.BaseContainerMenu;
import com.ultramega.creativecrafter.CreativeCrafter;
import com.ultramega.creativecrafter.blockentity.CreativeCrafterBlockEntity;
import com.ultramega.creativecrafter.registry.ModContainerMenus;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.SlotItemHandler;

public class CreativeCrafterContainerMenu extends BaseContainerMenu {
    public CreativeCrafterContainerMenu(CreativeCrafterBlockEntity blockEntity, Player player, int windowId) {
        super(ModContainerMenus.CREATIVE_CRAFTER_CONTAINER.get(), blockEntity, player, windowId);

        for (int i = 0; i < CreativeCrafter.ROWS; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new SlotItemHandler(blockEntity.getNode().getPatternInventory(), (i * 9) + j, 8 + (18 * j), 20 + (18 * i)));
            }
        }

        addPlayerInventory(8, 253);

        transferManager.addBiTransfer(player.getInventory(), blockEntity.getNode().getPatternInventory());
    }
}
