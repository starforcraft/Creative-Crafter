package com.ultramega.creativecrafter.setup;

import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.ultramega.creativecrafter.node.CreativeCrafterNetworkNode;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonSetup {
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent e) {
        API.instance().getNetworkNodeRegistry().add(CreativeCrafterNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new CreativeCrafterNetworkNode(world, pos)));
    }

    private static INetworkNode readAndReturn(CompoundTag tag, NetworkNode node) {
        node.read(tag);
        return node;
    }
}
