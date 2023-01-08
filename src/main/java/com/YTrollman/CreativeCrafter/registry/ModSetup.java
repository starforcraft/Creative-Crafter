package com.YTrollman.CreativeCrafter.registry;

import com.YTrollman.CreativeCrafter.CreativeCrafter;
import com.YTrollman.CreativeCrafter.node.CreativeCrafterNetworkNode;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = CreativeCrafter.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModSetup {

    public static void init(final FMLCommonSetupEvent event) {
        API.instance().getNetworkNodeRegistry().add(CreativeCrafterNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new CreativeCrafterNetworkNode(world, pos)));
        ModTileEntityTypes.CREATIVE_CRAFTER_TILE_ENTITY.get().create(BlockPos.ZERO, null).getDataManager().getParameters().forEach(BlockEntitySynchronizationManager::registerParameter);
    }

    private static INetworkNode readAndReturn(CompoundTag tag, NetworkNode node) {
        node.read(tag);
        return node;
    }
}
