package com.YTrollman.CreativeCrafter.registry;

import com.YTrollman.CreativeCrafter.CreativeCrafter;
import com.YTrollman.CreativeCrafter.container.CreativeCrafterContainer;
import com.YTrollman.CreativeCrafter.tileentity.CreativeCrafterTileEntity;

import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainers {

    public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, CreativeCrafter.MOD_ID);

    public static final RegistryObject<ContainerType<CreativeCrafterContainer>> CREATIVE_CRAFTER_CONTAINER = CONTAINER_TYPES.register("creative_crafter", () -> IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        TileEntity te = inv.player.getCommandSenderWorld().getBlockEntity(pos);
        if(!(te instanceof CreativeCrafterTileEntity))
        {
            CreativeCrafter.LOGGER.error("Wrong type of tile entity (expected CreativeCrafterTileEntity)!");
            return null;
        }
        return new CreativeCrafterContainer(windowId, inv.player, (CreativeCrafterTileEntity) te);
    }));
}
