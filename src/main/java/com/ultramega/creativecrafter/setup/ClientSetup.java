package com.ultramega.creativecrafter.setup;

import com.refinedmods.refinedstorage.apiimpl.API;
import com.ultramega.creativecrafter.CreativeCrafter;
import com.ultramega.creativecrafter.container.CreativeCrafterContainerMenu;
import com.ultramega.creativecrafter.gui.CreativeCrafterScreen;
import com.ultramega.creativecrafter.registry.ModContainerMenus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientSetup {
    public ClientSetup() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
    }

    @SubscribeEvent
    public void onClientSetup(final FMLClientSetupEvent e) {
        API.instance().addPatternRenderHandler(pattern -> {
            AbstractContainerMenu container = Minecraft.getInstance().player.containerMenu;

            if (container instanceof CreativeCrafterContainerMenu) {
                for (int i = 0; i < CreativeCrafter.SIZE; ++i) {
                    if (container.getSlot(i).getItem() == pattern) {
                        return true;
                    }
                }
            }

            return false;
        });

        e.enqueueWork(() -> MenuScreens.register(ModContainerMenus.CREATIVE_CRAFTER_CONTAINER.get(), CreativeCrafterScreen::new));
    }
}
