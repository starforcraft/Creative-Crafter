package com.YTrollman.CreativeCrafter.init;

import com.YTrollman.CreativeCrafter.container.CreativeCrafterContainer;
import com.YTrollman.CreativeCrafter.gui.CreativeCrafterScreen;
import com.YTrollman.CreativeCrafter.registry.ModContainers;
import com.refinedmods.refinedstorage.apiimpl.API;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.Container;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientEventHandler {

    public static void init(final FMLClientSetupEvent event)
    {
        ScreenManager.register(ModContainers.CREATIVE_CRAFTER_CONTAINER.get(), CreativeCrafterScreen::new);

        API.instance().addPatternRenderHandler(pattern -> {
            Container container = Minecraft.getInstance().player.containerMenu;

            if (container instanceof CreativeCrafterContainer) {
                for (int i = 0; i < 108; ++i) {
                    if (container.getSlot(i).getItem() == pattern) {
                        return true;
                    }
                }
            }

            return false;
        });
    }
}