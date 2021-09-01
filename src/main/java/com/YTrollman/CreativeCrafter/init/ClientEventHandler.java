package com.YTrollman.CreativeCrafter.init;

import com.YTrollman.CreativeCrafter.container.CreativeCrafterContainer;
import com.YTrollman.CreativeCrafter.gui.CreativeCrafterScreenFactory;
import com.YTrollman.CreativeCrafter.registry.ModContainers;
import com.refinedmods.refinedstorage.apiimpl.API;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.Container;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientEventHandler {

    public static void init(FMLClientSetupEvent event)
    {
        ScreenManager.register(ModContainers.CREATIVE_CRAFTER_CONTAINER.get(), new CreativeCrafterScreenFactory());

        /*API.instance().addPatternRenderHandler(pattern -> {
            Container container = Minecraft.getInstance().player.containerMenu;

            if (container instanceof CreativeCrafterContainer) {
                for (int i = 0; i < 108; ++i) {
                    if (container.getSlot(i).getItem() == pattern) {
                        return true;
                    }
                }
            }

            return false;
        });*/
    }
}