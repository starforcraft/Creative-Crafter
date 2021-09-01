package com.YTrollman.CreativeCrafter.registry;

import com.YTrollman.CreativeCrafter.node.CreativeCrafterNetworkNode;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ModSetup {

    @SubscribeEvent
    public void init(FMLCommonSetupEvent event)
    {
        API.instance().getNetworkNodeRegistry().add(CreativeCrafterNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new CreativeCrafterNetworkNode(world, pos)));
        ModTileEntityTypes.CREATIVE_CRAFTER_TILE_ENTITY.get().create().getDataManager().getParameters().forEach(TileDataManager::registerParameter);
    }

    private static INetworkNode readAndReturn(CompoundNBT tag, NetworkNode node) {
        node.read(tag);
        return node;
    }
}
