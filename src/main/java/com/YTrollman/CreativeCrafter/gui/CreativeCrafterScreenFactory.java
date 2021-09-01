package com.YTrollman.CreativeCrafter.gui;

import com.YTrollman.CreativeCrafter.container.CreativeCrafterContainer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class CreativeCrafterScreenFactory implements ScreenManager.IScreenFactory<CreativeCrafterContainer, CreativeCrafterScreen> {
    @Override
    public CreativeCrafterScreen create(CreativeCrafterContainer container, PlayerInventory inv, ITextComponent title) {
        CreativeCrafterScreen screen = new CreativeCrafterScreen(container, container.getGrid(), inv, title);

        container.setScreenInfoProvider(screen);

        return screen;
    }
}