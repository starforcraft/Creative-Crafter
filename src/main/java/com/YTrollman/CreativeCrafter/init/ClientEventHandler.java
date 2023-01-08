package com.YTrollman.CreativeCrafter.init;

import com.YTrollman.CreativeCrafter.container.CreativeCrafterContainerMenu;
import com.YTrollman.CreativeCrafter.gui.CreativeCrafterScreen;
import com.YTrollman.CreativeCrafter.registry.ModContainers;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.CrafterNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientEventHandler {

    public static void init(final FMLClientSetupEvent event) {
        MenuScreens.register(ModContainers.CREATIVE_CRAFTER_CONTAINER.get(), CreativeCrafterScreen::new);

        API.instance().getNetworkNodeRegistry().add(CrafterNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new CrafterNetworkNode(world, pos)));

        API.instance().addPatternRenderHandler(pattern -> {
            AbstractContainerMenu container = Minecraft.getInstance().player.containerMenu;

            if(container instanceof CreativeCrafterContainerMenu) {
                for(int i = 0; i < 108; ++i) {
                    if(container.getSlot(i).getItem() == pattern) {
                        return true;
                    }
                }
            }

            return false;
        });
    }

    private static INetworkNode readAndReturn(CompoundTag tag, NetworkNode node) {
        node.read(tag);

        return node;
    }
}