package com.YTrollman.CreativeCrafter.init;

import com.YTrollman.CreativeCrafter.gui.CreativeCrafterScreen;
import com.YTrollman.CreativeCrafter.registry.ModContainers;

import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientEventHandler {

    public static void init(final FMLClientSetupEvent event) 
    {
        ScreenManager.register(ModContainers.CREATIVE_CRAFTER_CONTAINER.get(), CreativeCrafterScreen::new);
    }
}